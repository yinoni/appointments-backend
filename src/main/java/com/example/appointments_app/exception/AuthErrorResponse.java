package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class AuthErrorResponse extends ErrorResponse{
    private String field;

    public AuthErrorResponse(String message, HttpStatus status, String field) {
        super(message, status);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
