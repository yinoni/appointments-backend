package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class JWTException extends RuntimeException {
    private final HttpStatus status;

    public JWTException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
