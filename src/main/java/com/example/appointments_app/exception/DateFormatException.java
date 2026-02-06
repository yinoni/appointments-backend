package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class DateFormatException extends BaseException {
    public DateFormatException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
