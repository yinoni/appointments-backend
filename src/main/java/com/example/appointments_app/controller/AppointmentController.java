package com.example.appointments_app.controller;

import com.example.appointments_app.model.appointment.AppointmentDTO;
import com.example.appointments_app.model.appointment.AppointmentIn;
import com.example.appointments_app.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
