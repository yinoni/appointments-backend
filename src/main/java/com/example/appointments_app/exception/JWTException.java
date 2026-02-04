package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class JWTException extends BaseException {

    public JWTException(String message, HttpStatus status) {
        super(message, status);
    }
}
