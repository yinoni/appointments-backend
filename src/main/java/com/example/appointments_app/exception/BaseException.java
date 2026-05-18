package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    private HttpStatus status;
    private String code;
    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = "";
    }

    public BaseException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
