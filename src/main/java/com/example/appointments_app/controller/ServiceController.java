package com.example.appointments_app.controller;

import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.model.service.ServiceDTO;
import com.example.appointments_app.model.service.ServiceIn;
import com.example.appointments_app.model.service.ServiceRemoveRequest;
import com.example.appointments_app.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService){
        this.serviceService = serviceService;
    }

    /***
     *
     * @param serviceIn - The service data
     * @param currentUser - The current user data from JWT
     * @return - This function adds new service to the business
     * @throws  - BusinessException if the business is not exists
     */
    @PostMapping("/addService")
    public ResponseEntity<?> addService(@Valid @RequestBody ServiceIn serviceIn , @AuthenticationPrincipal CustomUserDetails currentUser){
        ServiceDTO dto = serviceService.addNewService(serviceIn, currentUser.getId());
        return ResponseEntity.ok(dto);
    }

    /***
     *
     * @param request - The request params for deleting the service from the business (See ServiceRemoveRequest class)
     * @param currentUser - The current user data from the JWT
     * @return - removes services from the business by service id
     * @throw - BusinessException if the business not exists or if the user is not the business owner. It also deletes all the appointments that have link to this service
     */
    @PostMapping("/removeService")
    public ResponseEntity<?> removeService(@Valid @RequestBody ServiceRemoveRequest request, @AuthenticationPrincipal CustomUserDetails currentUser){
        serviceService.removeService(request, currentUser.getId());
        return ResponseEntity.ok("The service with id: " + request.getServiceId() + " has deleted successfully!");
    }

    @PutMapping("/{serviceId}")
    public ResponseEntity<?> updateService(@PathVariable Long serviceId, @RequestBody ServiceIn serviceIn, @AuthenticationPrincipal CustomUserDetails currentUser){
        ServiceDTO dto = serviceService.updateService(serviceId, serviceIn, currentUser.getId()).convertToDTO();

        return ResponseEntity.ok(dto);
    }
}
