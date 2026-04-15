package com.example.appointments_app.controller;

import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.model.business.BusinessDTO;
import com.example.appointments_app.model.user.UserDTO;
import com.example.appointments_app.model.user.UserUpdateRequest;
import com.example.appointments_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token){
        userService.logout(token);
        return ResponseEntity.ok("Success");
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

}
