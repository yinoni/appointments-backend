package com.example.appointments_app.controller;

import com.example.appointments_app.model.CustomUserDetails;
import com.example.appointments_app.model.ScreensDTO.HomeDTO;
import com.example.appointments_app.model.ScreensDTO.InsightsDTO;
import com.example.appointments_app.service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AppController {

    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping("/home")
    public ResponseEntity<?> getHomePageDTO(@AuthenticationPrincipal CustomUserDetails ownerDetails){
        HomeDTO homeDTO = appService.getHomePageDTO(ownerDetails.getId());
        return ResponseEntity.ok(homeDTO);
    }

    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(@AuthenticationPrincipal CustomUserDetails ownerDetails, @RequestParam String range){
        InsightsDTO insightsDTO = appService.getInsightsPageDTO(ownerDetails.getId(), 9L, range);
        return ResponseEntity.ok(insightsDTO);
    }
}
