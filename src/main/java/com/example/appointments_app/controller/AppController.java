package com.example.appointments_app.controller;

import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AppController {

    private final AuthService authService;

    public AppController(AuthService authService) {
        this.authService = authService;
    }
}
