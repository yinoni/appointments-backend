package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class UserNotVerified extends BaseException {
    public UserNotVerified(String message) {
        super(message, HttpStatus.FORBIDDEN, "USER_NOT_VERIFIED");
    }


}
