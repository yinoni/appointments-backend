package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends RuntimeException{
    private String message;
    private final HttpStatus status;

    public AuthenticationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
