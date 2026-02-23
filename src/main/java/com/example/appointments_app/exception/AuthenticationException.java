package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BaseException{

    public AuthenticationException(String message, HttpStatus status) {
        super(message, status);
    }
}
