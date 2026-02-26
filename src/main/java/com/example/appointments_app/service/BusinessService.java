package com.example.appointments_app.service;

import com.example.appointments_app.elasticsearch.ElasticSearchService;
import com.example.appointments_app.exception.*;
import com.example.appointments_app.kafka.BusinessProducer;
import com.example.appointments_app.model.business.Business;
import com.example.appointments_app.model.business.BusinessCategory;
import com.example.appointments_app.model.business.BusinessDTO;
import com.example.appointments_app.model.business.BusinessInput;
import com.example.appointments_app.model.schedule.Schedule;
import com.example.appointments_app.model.schedule.ScheduleDTO;
import com.example.appointments_app.model.schedule.ScheduleIn;
import com.example.appointments_app.model.service.ServiceDTO;
import com.example.appointments_app.model.service.ServiceIn;
import com.example.appointments_app.model.user.User;
import com.example.appointments_app.repo.AppointmentRepo;
import com.example.appointments_app.repo.BusinessRepo;
import com.example.appointments_app.repo.ServiceRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static com.example.appointments_app.model.business.BusinessBuilder.aBusiness;

@Service
public class BusinessService {

    private final BusinessRepo businessRepo;
    private final AppointmentRepo appointmentRepo;
    private final UserService userService;
    private final ScheduleService scheduleService;
    private final BusinessProducer businessProducer;
    private final ElasticSearchService elasticSearchService;
    private final ServiceRepo serviceRepo;


    public BusinessService(BusinessRepo businessRepo,
                           AppointmentRepo appointmentRepo,
                           UserService userService,
                           ScheduleService scheduleService,
                           ElasticSearchService elasticSearchService,
                           BusinessProducer businessProducer,
                           ServiceRepo serviceRepo){
        this.businessRepo = businessRepo;
        this.appointmentRepo = appointmentRepo;
        this.userService = userService;
        this.scheduleService = scheduleService;
        this.businessProducer = businessProducer;
        this.elasticSearchService = elasticSearchService;
        this.serviceRepo = serviceRepo;
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
        business.setDescription(businessInput.getBusinessDesc());
        business.setCity(businessInput.getCity());
        business.setStreet(businessInput.getStreet());
        return save(business);
    }

    /***
     *
     * @param businessInput - see BusinessIn class
     * @param ownerId - The owner id
     * @return - DTO of the business that added
     */
    @Transactional
    public BusinessDTO createBusiness(BusinessInput businessInput, Long ownerId){
        User user = userService.findById(ownerId);
        BusinessDTO bDTO;

        Business business = businessInput.toBusiness();
        final Business finalBusiness = business;
        business.setOwner(user);

        if(businessInput.getServices() != null){
            List<com.example.appointments_app.model.service.Service> services =
                    businessInput.getServices().stream().map(service -> {
                        var s = service.toService();
                        s.setBusiness(finalBusiness);
                        return s;
                    }).toList();

            business.setServices(services);
        }

        business = businessRepo.save(business);
        bDTO = business.convertToDTO();

        businessProducer.sendBusinessCreatedEvent(bDTO);

        return  bDTO;
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

    public String searchBusiness(String text){
        String s = "";
        try{
             s = elasticSearchService.search("businesses", text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return s;
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
        ScheduleDTO dto;

        schedule.setBusiness(business);
        schedule.setAppointments(new ArrayList<>());

        schedule = scheduleService.addNewSchedule(schedule);

        dto = schedule.convertToDTO();
        dto.setAvailable_hours(scheduleService.getAvailableHours(schedule.getId()));

        return dto;
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
