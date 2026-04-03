package com.example.appointments_app.controller;

import com.example.appointments_app.jwt.JwtService;
import com.example.appointments_app.model.authentication.AuthRequest;
import com.example.appointments_app.model.authentication.CustomUserDetails;
import com.example.appointments_app.model.authentication.PhoneVerifyInput;
import com.example.appointments_app.model.user.User;
import com.example.appointments_app.model.user.UserIn;
import com.example.appointments_app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/authenticate")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager){
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("")
    public ResponseEntity<?> temp(){
        return ResponseEntity.ok("OK");
    }

    @PostMapping("")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){

        try{
            Authentication authentication =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(token);
        }
        catch (Exception e){
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserIn userIn){
        User userRes = userService.register(userIn);
        CustomUserDetails userDetails = new CustomUserDetails(userRes.getId(), userRes.getEmail(), userRes.getPhoneNumber(), userRes.getPassword(), new ArrayList<>());

        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/phone-verify")
    public ResponseEntity<?> phoneVerify(@RequestBody PhoneVerifyInput phoneVerifyInput){
        userService.verifyPhoneNumber(phoneVerifyInput);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendCode(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.resendOtpCode(userDetails.getPhoneNumber());
        return ResponseEntity.ok("OK");
    }

}
