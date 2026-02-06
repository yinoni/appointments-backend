package com.example.appointments_app.controller;

import com.example.appointments_app.model.*;
import com.example.appointments_app.service.BusinessService;
import com.example.appointments_app.util.DateUtils;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.example.appointments_app.util.DateUtils.getDateLocalDate;

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
    public ResponseEntity<?> addService(@Valid @RequestBody ServiceIn serviceIn , @AuthenticationPrincipal CustomUserDetails currentUser){
        BusinessDTO dto = businessService.addNewService(serviceIn, currentUser.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/removeService")
    public ResponseEntity<?> removeService(@Valid @RequestBody ServiceRemoveRequest request, @AuthenticationPrincipal CustomUserDetails currentUser){
        BusinessDTO dto = businessService.removeService(request, currentUser.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{businessId}/schedule")
    public ResponseEntity<?> addSchedule(@PathVariable Long businessId, @AuthenticationPrincipal CustomUserDetails currentUser, @Valid @RequestBody ScheduleIn scheduleIn){
        ScheduleDTO dto = businessService.addNewSchedule(businessId, scheduleIn, currentUser.getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{businessId}/schedule")
    public ResponseEntity<?> getSchedules(@PathVariable Long businessId){
        List<ScheduleDTO> dtos = businessService.findAllBusinessSchedules(businessId);

        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{businessId}/schedule/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long businessId, @PathVariable Long scheduleId, @AuthenticationPrincipal CustomUserDetails currentUser){
        businessService.deleteSchedule(businessId, scheduleId, currentUser.getId());
        return ResponseEntity.ok("The schedule has been deleted");
    }

    @GetMapping("/{businessId}/schedule/byDate")
    public ResponseEntity<?> getScheduleByDate(@PathVariable Long businessId, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        ScheduleDTO scheduleDTO = businessService.findScheduleByDateAndBusiness(businessId, date).convertToDTO();

        return ResponseEntity.ok(scheduleDTO);
    }

}
