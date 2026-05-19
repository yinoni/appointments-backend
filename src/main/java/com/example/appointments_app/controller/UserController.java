package com.example.appointments_app.controller;

import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.model.business.BusinessDTO;
import com.example.appointments_app.model.user.UserDTO;
import com.example.appointments_app.model.user.UserUpdateRequest;
import com.example.appointments_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getUserData(@AuthenticationPrincipal CustomUserDetails userDetails){
        UserDTO dto = userService.findById(userDetails.getId()).convertToUserDTO();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        userService.logout(token, customUserDetails.getId());

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // זמן תוקף 0 אומר לדפדפן: תמחק אותה עכשיו!
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logged out successfully");
    }

    @PutMapping("")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        UserDTO dto = userService.updateUser(userUpdateRequest, customUserDetails.getId());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/saved")
    public ResponseEntity<?> getSavedBusinesses(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam Integer page){
        Set<BusinessDTO> businessDTOSet = userService.getSavedBusinesses(userDetails.getId(), page);

        return ResponseEntity.ok(businessDTOSet);
    }

    @GetMapping("/get-view-state")
    public ResponseEntity<?> getViewState(@AuthenticationPrincipal CustomUserDetails userDetails){
        String currentView = userService.getViewState(userDetails.getId());

        return ResponseEntity.ok(currentView);
    }

    @PostMapping("/toggle-view-state")
    public ResponseEntity<?> changeViewState(@AuthenticationPrincipal CustomUserDetails userDetails){
        String currentView = userService.changeViewState(userDetails.getId());
        return ResponseEntity.ok(currentView);
    }

}
