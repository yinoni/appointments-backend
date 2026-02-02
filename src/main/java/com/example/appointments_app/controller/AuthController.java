package com.example.appointments_app.controller;

import com.example.appointments_app.jwt.JwtService;
import com.example.appointments_app.model.AuthRequest;
import com.example.appointments_app.model.CustomUserDetails;
import com.example.appointments_app.model.UserIn;
import com.example.appointments_app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        userService.register(userIn);

        return ResponseEntity.ok("The user registered successfully");
    }

}
