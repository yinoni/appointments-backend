package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class ServiceNotFoundException extends RuntimeException{
    private String message;
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public ServiceNotFoundException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
