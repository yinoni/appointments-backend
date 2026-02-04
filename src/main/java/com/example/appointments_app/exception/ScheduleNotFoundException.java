package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class ScheduleNotFoundException extends BaseException {

    public ScheduleNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
