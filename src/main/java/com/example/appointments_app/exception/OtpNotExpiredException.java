package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class OtpNotExpiredException extends BaseException {
    public OtpNotExpiredException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
