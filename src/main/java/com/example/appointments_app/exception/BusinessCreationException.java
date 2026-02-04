package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class BusinessCreationException extends BaseException {
    public BusinessCreationException(String message, HttpStatus status) {
        super(message, status);
    }
}
