package com.example.appointments_app.service;

import com.example.appointments_app.exception.*;
import com.example.appointments_app.model.*;
import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.repo.AppointmentRepo;
import com.example.appointments_app.repo.BusinessRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.appointments_app.model.AppointmentBuilder.anAppointment;

@Service
public class BusinessService {

    private final BusinessRepo businessRepo;
    private final AppointmentRepo appointmentRepo;
    private final UserService userService;
    private final ScheduleService scheduleService;


    public BusinessService(BusinessRepo businessRepo,
                           AppointmentRepo appointmentRepo,
                           UserService userService,
                           ScheduleService scheduleService,
                           Redis redis){
        this.businessRepo = businessRepo;
        this.appointmentRepo = appointmentRepo;
        this.userService = userService;
        this.scheduleService = scheduleService;
    }

    /***
     *
     * @param b_id - The business id
     * @return - The business with id of b_id
     */
    public Business findBusinessById(Long b_id){
        return businessRepo.findById(b_id).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));
    }

    public Business save(Business business){
        return businessRepo.save(business);
    }

    public Business updateBusiness(Long b_id, Long ownerId, BusinessInput businessInput){
        Business business = findBusinessByIdAndOwnerId(b_id, ownerId);
        business.setBusinessName(businessInput.getBusinessName());
        return save(business);
    }

    /***
     *
     * @param businessInput - see BusinessIn class
     * @param ownerId - The owner id
     * @return - DTO of the business that added
     */
    public BusinessDTO createBusiness(BusinessInput businessInput, Long ownerId){
        User user = userService.findById(ownerId);

        Business business = businessInput.toBusiness();
        business.setOwner(user);
        business.setTotalAppointments(0);
        business.setSchedules(new ArrayList<>());
        business.setServices(new ArrayList<>());
        try{
            return save(business).convertToDTO();
        }
        catch (Exception e){
            throw new BusinessCreationException("Business name already exists!", HttpStatus.CONFLICT);
        }
    }

    /***
     *
     * @param businessId - The business id
     * @param ownerId - The id of the business owner
     * @return - The business that found by business id and owner id and throw BusinessException if the business is not found or if the owner is not the business owner
     */
    public Business findBusinessByIdAndOwnerId(Long businessId, Long ownerId){
        Business business = businessRepo.findById(businessId).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));

        if(!Objects.equals(ownerId, business.getOwner().getId()))
            throw new BusinessException("The owner is not allowed!", HttpStatus.FORBIDDEN);

        return business;
    }

    public List<BusinessDTO> getBusinessesByOwnerId(Long ownerId){
        List<Business> businesses = businessRepo.findAllByOwnerId(ownerId);

        return businesses.stream().map(Business::convertToDTO).toList();
    }


    /***
     *
     * @param businessId - The business id
     * @param userId - The user id
     * @return - DTO of the deleted business
     */
    public BusinessDTO deleteBusiness(Long businessId, Long userId){
        Business business = businessRepo.findById(businessId).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));

        BusinessDTO bDTO;

        if(!Objects.equals(business.getOwner().getId(), userId))
            throw new BusinessException("User not allowed to delete this business!", HttpStatus.FORBIDDEN);

        appointmentRepo.deleteAllAppointmentsByBusinessId(businessId);

        bDTO = business.convertToDTO();

        businessRepo.deleteById(businessId);

        return bDTO;
    }



    /***
     *
     * @param businessId - The business id
     * @param scheduleIn - Schedule input (See ScheduleIn class)
     * @param ownerId - The id of the owner
     * @return - new schedule dto of the new schedule
     */
    public ScheduleDTO addNewSchedule(Long businessId, ScheduleIn scheduleIn, Long ownerId){

        Business business = findBusinessByIdAndOwnerId(businessId, ownerId);
        Schedule schedule = scheduleIn.toSchedule();

        schedule.setBusiness(business);
        schedule.setAppointments(new ArrayList<>());

        schedule = scheduleService.addNewSchedule(schedule);

        return  schedule.convertToDTO();
    }

    /***
     *
     * @param businessId - The business id
     * @return - All list of all the business schedules
     */
    public List<ScheduleDTO> findAllBusinessSchedules(Long businessId, int pageNumber, int size){
        businessRepo.findById(businessId).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));

        Page<Schedule> page = scheduleService.getSchedulesByBusinessId(businessId, pageNumber, size);
        List<Schedule> schedules = page.stream().toList();

        return  schedules.stream().map(Schedule::convertToDTO).toList();
    }

    /**
     *
     * @param businessId - The id of the business
     * @param scheduleId - The schedule id
     * @param ownerId - The id of the current user
     *                This function delete schedule by the schedule id
     */
    public void deleteSchedule(Long businessId, Long scheduleId, Long ownerId){
        findBusinessByIdAndOwnerId(businessId, ownerId);
        Schedule schedule = scheduleService.findById(scheduleId);

        if(!Objects.equals(schedule.getBusiness().getId(), businessId))
            throw new BusinessException("The business not contains this schedule!", HttpStatus.BAD_GATEWAY);

        scheduleService.deleteById(scheduleId);
    }

    /***
     *
     * @param businessId - The business id
     * @param date - The date of the schedule
     * @return - The schedule of business {businessId} and with the date {date}
     */
    public ScheduleDTO findScheduleByDateAndBusiness(Long businessId, LocalDate date) {
        findBusinessById(businessId);
        Schedule schedule = scheduleService.getScheduleByDate(businessId, date);
        ScheduleDTO dto = schedule.convertToDTO();
        List<LocalTime> availableHours = scheduleService.getAvailableHours(schedule.getId());

        dto.setAvailable_hours(availableHours);

        return dto;
    }


}
