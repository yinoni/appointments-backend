package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class ServiceNotFoundException extends BaseException {

    public ServiceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
