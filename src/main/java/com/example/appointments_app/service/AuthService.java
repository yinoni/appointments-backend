package com.example.appointments_app.service;

import com.example.appointments_app.exception.AuthenticationException;
import com.example.appointments_app.model.authentication.AuthRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    public String login(AuthRequest request){
        if(!request.getEmail().equals("y@dev.com") && request.getPassword().equals("12345"))
            throw new AuthenticationException("Username or password incorrect!", HttpStatus.BAD_REQUEST);

        return "logged in as " + request.getEmail();
    }
}