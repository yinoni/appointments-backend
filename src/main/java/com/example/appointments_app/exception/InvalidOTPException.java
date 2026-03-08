package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class InvalidOTPException extends BaseException {
    public InvalidOTPException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
