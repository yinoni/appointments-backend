package com.example.appointments_app.controller;

import com.example.appointments_app.exception.AppointmentAlreadyExistsException;
import com.example.appointments_app.model.*;
import com.example.appointments_app.service.AppointmentService;
import com.example.appointments_app.service.ScheduleService;
import com.example.appointments_app.service.ServiceService;
import com.example.appointments_app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {


    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }


    @PostMapping("")
    public ResponseEntity<?> addNewAppointment(@RequestBody AppointmentIn appointmentIn) {
        AppointmentDTO dto = appointmentService.insertAppointment(appointmentIn).convertToDTO();

        return ResponseEntity.ok(dto);
    }

}
