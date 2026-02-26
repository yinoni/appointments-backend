package com.example.appointments_app.service;

import com.example.appointments_app.exception.BusinessException;
import com.example.appointments_app.exception.ServiceNotFoundException;
import com.example.appointments_app.model.business.Business;
import com.example.appointments_app.model.service.ServiceDTO;
import com.example.appointments_app.model.service.ServiceIn;
import com.example.appointments_app.model.service.ServiceRemoveRequest;
import com.example.appointments_app.repo.ServiceRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ServiceService {

    private final ServiceRepo serviceRepo;
    private final BusinessService businessService;

    public ServiceService(ServiceRepo serviceRepo, BusinessService businessService){
        this.serviceRepo = serviceRepo;
        this.businessService = businessService;
    }


    public com.example.appointments_app.model.service.Service findById(Long serviceId) {
        return serviceRepo.findById(serviceId).orElseThrow(() ->
                new ServiceNotFoundException("Service not exists!"));
    }

    public com.example.appointments_app.model.service.Service addNewService(com.example.appointments_app.model.service.Service service){
        return serviceRepo.save(service);
    }

    public com.example.appointments_app.model.service.Service updateService(Long serviceId, ServiceIn serviceIn, Long ownerId){
        com.example.appointments_app.model.service.Service service = findById(serviceId);

        if(service.getBusiness().getId() != serviceIn.getBusinessId())
            throw new ServiceNotFoundException("This service is not exists on this business!");

        if(service.getBusiness().getOwner().getId() != ownerId)
            throw new BusinessException("The user is not allowed to update the service!", HttpStatus.FORBIDDEN);

        service.setServiceName(serviceIn.getServiceName());
        service.setDuration(serviceIn.getDuration());
        service.setPrice(serviceIn.getPrice());

        return serviceRepo.save(service);
    }


    /***
     *
     * @param serviceIn - The service input ( see ServiceIn class)
     * @param ownerId - The owner id
     * @return - DTO of the service that just added, and adds the service in the Business table too
     */
    public ServiceDTO addNewService(ServiceIn serviceIn, Long ownerId){
        Business business = businessService.findBusinessByIdAndOwnerId(serviceIn.getBusinessId(), ownerId);

        com.example.appointments_app.model.service.Service service = serviceIn.toService();

        service.setBusiness(business);

        service = addNewService(service);

        business.getServices().add(service);

        businessService.save(business);

        return service.convertToDTO();
    }

    /***
     *
     * @param request - see ServiceRemoveRequest class
     * @param ownerId - The owner id
     * @return - DTO of the service that just deleted
     */
    public void removeService(ServiceRemoveRequest request, Long ownerId) {
        // 1. מציאת העסק
        Business business = businessService.findBusinessByIdAndOwnerId(request.getBusinessId(), ownerId);

        com.example.appointments_app.model.service.Service service = findById(request.getServiceId());

        // Check if the business id of the service and the business id from the request are equal
        if(!Objects.equals(service.getBusiness().getId(), request.getBusinessId()))
            throw new BusinessException("The business not contains this service!", HttpStatus.BAD_GATEWAY);

        business.getServices().removeIf(s -> s.getId().equals(request.getServiceId()));

        // 3. שמירת העסק - בזכות orphanRemoval=true, ה-Service יימחק מה-DB אוטומטית!
        businessService.save(business);
    }

}
