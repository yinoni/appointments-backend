package com.example.appointments_app.controller;

import com.example.appointments_app.model.*;
import com.example.appointments_app.service.BusinessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/business")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService){
        this.businessService = businessService;
    }

    @PostMapping("")
    public ResponseEntity<?> createBusiness(@RequestBody BusinessInput businessInput, @AuthenticationPrincipal CustomUserDetails currentUser){
        BusinessDTO businessDTO = businessService.createBusiness(businessInput, currentUser.getId());
        return new ResponseEntity<>(businessDTO, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> createBusiness(@AuthenticationPrincipal CustomUserDetails currentUser){
        List<BusinessDTO> businessesDTO = businessService.getBusinessesByOwnerId(currentUser.getId());
        return new ResponseEntity<>(businessesDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/{businessId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBusiness(@PathVariable Long businessId, @AuthenticationPrincipal CustomUserDetails currentUser){
        BusinessDTO businessDTO = businessService.deleteBusiness(businessId, currentUser.getId());
        return ResponseEntity.ok(businessDTO);
    }

    @PostMapping("/addService")
    public ResponseEntity<?> addService(@RequestBody ServiceIn serviceIn , @AuthenticationPrincipal CustomUserDetails currentUser){
        BusinessDTO dto = businessService.addNewService(serviceIn, currentUser.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/removeService")
    public ResponseEntity<?> removeService(@RequestBody ServiceRemoveRequest request, @AuthenticationPrincipal CustomUserDetails currentUser){
        BusinessDTO dto = businessService.removeService(request, currentUser.getId());
        return ResponseEntity.ok(dto);
    }


}
