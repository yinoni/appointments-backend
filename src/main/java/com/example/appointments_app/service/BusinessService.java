package com.example.appointments_app.service;

import com.example.appointments_app.exception.BusinessCreationException;
import com.example.appointments_app.exception.BusinessException;
import com.example.appointments_app.exception.ServiceNotFoundException;
import com.example.appointments_app.exception.UserNotFoundException;
import com.example.appointments_app.model.*;
import com.example.appointments_app.repo.AppointmentRepo;
import com.example.appointments_app.repo.BusinessRepo;
import com.example.appointments_app.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.appointments_app.model.AppointmentBuilder.anAppointment;

@Service
public class BusinessService {

    private final BusinessRepo businessRepo;
    private final ServiceService serviceService;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final ScheduleService scheduleService;

    public BusinessService(BusinessRepo businessRepo,
                           UserRepository userRepository,
                           ServiceService serviceService,
                           AppointmentService appointmentService,
                           UserService userService,
                           ScheduleService scheduleService){
        this.businessRepo = businessRepo;
        this.serviceService = serviceService;
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.scheduleService = scheduleService;
    }

    public BusinessDTO createBusiness(BusinessInput businessInput, Long ownerId){
        User user = userService.findById(ownerId);

        Business business = businessInput.toBusiness();
        business.setOwner(user);
        business.setTotalAppointments(0);
        business.setSchedules(new ArrayList<>());
        business.setServices(new ArrayList<>());
        try{
            return businessRepo.save(business).convertToDTO();
        }
        catch (Exception e){
            throw new BusinessCreationException("Business name already exists!", HttpStatus.CONFLICT);
        }
    }

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

    public BusinessDTO deleteBusiness(Long businessId, Long userId){
        Business business = businessRepo.findById(businessId).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));

        BusinessDTO bDTO;

        if(!Objects.equals(business.getOwner().getId(), userId))
            throw new BusinessException("User not allowed to delete this business!", HttpStatus.FORBIDDEN);

        appointmentService.deleteAllAppointmentsByBusinessId(businessId);

        bDTO = business.convertToDTO();

        businessRepo.deleteById(businessId);

        return bDTO;
    }

    public BusinessDTO addNewService(ServiceIn serviceIn, Long ownerId){
        Business business = findBusinessByIdAndOwnerId(serviceIn.getBusinessId(), ownerId);

        com.example.appointments_app.model.Service service = serviceIn.toService();

        service.setBusiness(business);

        service = serviceService.addNewService(service);

        business.getServices().add(service);

        businessRepo.save(business);

        return business.convertToDTO();
    }

    public BusinessDTO removeService(ServiceRemoveRequest request, Long ownerId) {
        // 1. מציאת העסק
        Business business = findBusinessByIdAndOwnerId(request.getBusinessId(), ownerId);

        com.example.appointments_app.model.Service service = serviceService.findById(request.getServiceId());

        // Check if the business id of the service and the business id from the request are equal
        if(!Objects.equals(service.getBusiness().getId(), request.getBusinessId()))
            throw new BusinessException("The business not contains this service!", HttpStatus.BAD_GATEWAY);

        appointmentService.deleteAppointmentByServiceId(request.getServiceId(), ownerId);

        business.getServices().removeIf(s -> s.getId().equals(request.getServiceId()));

        // 3. שמירת העסק - בזכות orphanRemoval=true, ה-Service יימחק מה-DB אוטומטית!
        return businessRepo.save(business).convertToDTO();
    }

    public List<ScheduleDTO> findAllBusinessSchedules(Long businessId){
        Business business = businessRepo.findById(businessId).orElseThrow(() ->
                new BusinessException("Business not found!", HttpStatus.NOT_FOUND));

        List<Schedule> schedules = scheduleService.getSchedulesByBusinessId(businessId);

        return  schedules.stream().map(Schedule::convertToDTO).toList();
    }

}
