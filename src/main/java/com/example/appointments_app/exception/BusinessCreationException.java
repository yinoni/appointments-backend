package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class BusinessCreationException extends RuntimeException {
    private final HttpStatus status;
    public BusinessCreationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
