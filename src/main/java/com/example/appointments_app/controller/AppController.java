package com.example.appointments_app.controller;

import com.example.appointments_app.redis.Redis;
import com.example.appointments_app.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AppController {

    private final AuthService authService;

    public AppController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> helloWorld() {
        return ResponseEntity.ok("Hello world!");
    }
}
