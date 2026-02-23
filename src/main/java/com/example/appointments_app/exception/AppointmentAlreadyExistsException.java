package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class AppointmentAlreadyExistsException extends BaseException {
    public AppointmentAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
