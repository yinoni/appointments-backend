package com.example.appointments_app.controller;


import com.example.appointments_app.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("schedule")
public class ScheduleController {


    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService){
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{scheduleId}/available_hours")
    public ResponseEntity<?> getAvailableHours(@PathVariable("scheduleId") Long scheduleId){
        List<LocalTime> hours = scheduleService.getAvailableHours(scheduleId);

        return ResponseEntity.ok(hours);
    }

}
