package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }

}
