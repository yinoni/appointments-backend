package com.example.appointments_app.controller;

import com.example.appointments_app.model.appointment.AppointmentDTO;
import com.example.appointments_app.model.appointment.AppointmentIn;
import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("")
    public ResponseEntity getUpcomingAppointments(@RequestParam Integer page, @AuthenticationPrincipal CustomUserDetails userDetails){
        List<AppointmentDTO> appointmentDTOList = appointmentService.getUpcomingAppointments(userDetails.getId(), page);
        return ResponseEntity.ok(appointmentDTOList);
    }
}
