package com.example.appointments_app.controller;

import com.example.appointments_app.model.*;
import com.example.appointments_app.service.BusinessService;
import com.example.appointments_app.service.ScheduleService;
import com.example.appointments_app.util.DateUtils;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.appointments_app.util.DateUtils.getDateLocalDate;

@RestController
@RequestMapping("/business")
public class BusinessController {

    private final BusinessService businessService;
    private final ScheduleService scheduleService;

    public BusinessController(BusinessService businessService, ScheduleService scheduleService){
        this.businessService = businessService;
        this.scheduleService = scheduleService;
    }

    /***
     *
     * @param businessInput - The business input data (See BusinessIn class)
     * @param currentUser - The current user details from the JWT
     * @return - This function create new business with the business data from the request body
     */
    @PostMapping("")
    public ResponseEntity<?> createBusiness(@RequestBody BusinessInput businessInput, @AuthenticationPrincipal CustomUserDetails currentUser){
        BusinessDTO businessDTO = businessService.createBusiness(businessInput, currentUser.getId());
        return new ResponseEntity<>(businessDTO, HttpStatus.OK);
    }


    /***
     *
     * @param currentUser - The current user data from the JWT
     * @return - All the current user businesses
     */
    @GetMapping("")
    public ResponseEntity<?> getUserBusinesses(@AuthenticationPrincipal CustomUserDetails currentUser){
        List<BusinessDTO> businessesDTO = businessService.getBusinessesByOwnerId(currentUser.getId());
        return new ResponseEntity<>(businessesDTO, HttpStatus.OK);
    }

    /***
     *
     * @param businessId - The business id
     * @param currentUser - The current user data from the JWT
     * @return - This function deletes the business by the business id and by the owner id.
     * @throws - BusinessException - If the business not exists or if the user is not the business owner
     */
    @RequestMapping(value = "/{businessId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBusiness(@PathVariable Long businessId, @AuthenticationPrincipal CustomUserDetails currentUser){
        BusinessDTO businessDTO = businessService.deleteBusiness(businessId, currentUser.getId());
        return ResponseEntity.ok(businessDTO);
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
        BusinessDTO dto = businessService.addNewService(serviceIn, currentUser.getId());
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
        BusinessDTO dto = businessService.removeService(request, currentUser.getId());
        return ResponseEntity.ok(dto);
    }

    /***
     *
     * @param businessId - The business id
     * @param currentUser - The current user data from JWT
     * @param scheduleIn - The schedule input data
     * @return - Adds new schedule to the business with businessId
     * @throws - BusinessException if the business is not exits or if the user is not the business owner
     */
    @PostMapping("/{businessId}/schedule")
    public ResponseEntity<?> addSchedule(@PathVariable Long businessId, @AuthenticationPrincipal CustomUserDetails currentUser, @Valid @RequestBody ScheduleIn scheduleIn){
        ScheduleDTO dto = businessService.addNewSchedule(businessId, scheduleIn, currentUser.getId());
        return ResponseEntity.ok(dto);
    }

    /***
     *
     * @param businessId - The business id
     * @return - ALl the business schedules
     * @throws - BusinessException if the business is not exits
     */
    @GetMapping("/{businessId}/schedule")
    public ResponseEntity<?> getSchedules(@PathVariable Long businessId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        List<ScheduleDTO> dtos = businessService.findAllBusinessSchedules(businessId, page, size);

        return ResponseEntity.ok(dtos);
    }

    /***
     *
     * @param businessId - The business id
     * @param scheduleId - The schedule id
     * @param currentUser - The current user data from the JWT
     * @return - Deletes the schedule with the scheduleId from the business with businessId
     * @throws - BusinessException if the business is not exits and ScheduleNotFoundException if the schedule not found
     */
    @DeleteMapping("/{businessId}/schedule/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long businessId, @PathVariable Long scheduleId, @AuthenticationPrincipal CustomUserDetails currentUser){
        businessService.deleteSchedule(businessId, scheduleId, currentUser.getId());
        return ResponseEntity.ok("The schedule has been deleted");
    }

    /***
     *
     * @param businessId - The business id
     * @param date - The date to look for
     * @return - The schedule in the chosen date
     * @throws - BusinessException if the business is not exits and ScheduleNotFoundException if the schedule is not exists
     */
    @GetMapping("/{businessId}/schedule/byDate")
    public ResponseEntity<?> getScheduleByDate(@PathVariable Long businessId, @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        ScheduleDTO scheduleDTO = businessService.findScheduleByDateAndBusiness(businessId, date);

        return ResponseEntity.ok(scheduleDTO);
    }



}
