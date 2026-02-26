package com.example.appointments_app.controller;

import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.model.ScreensDTO.HomeDTO;
import com.example.appointments_app.model.ScreensDTO.InsightsDTO;
import com.example.appointments_app.model.business.BusinessSummary;
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

    @GetMapping("/home/owner")
    public ResponseEntity<?> getHomePageDTO(@AuthenticationPrincipal CustomUserDetails ownerDetails){
        HomeDTO homeDTO = appService.getOwnerHomePageDTO(ownerDetails.getId());
        return ResponseEntity.ok(homeDTO);
    }

    @GetMapping("/home/analytics/{businessId}")
    public ResponseEntity<?> getBusinessSummary(@AuthenticationPrincipal CustomUserDetails ownerDetails, @PathVariable Long businessId){
        BusinessSummary businessSummary = appService.getBusinessSummary(ownerDetails.getId(), businessId);
        return ResponseEntity.ok(businessSummary);
    }

    @GetMapping("/analytics/owner")
    public ResponseEntity<?> getAnalytics(@AuthenticationPrincipal CustomUserDetails ownerDetails, @RequestParam Long businessId, @RequestParam String range){
        InsightsDTO insightsDTO = appService.getInsightsPageDTO(ownerDetails.getId(), businessId, range);
        return ResponseEntity.ok(insightsDTO);
    }
}
