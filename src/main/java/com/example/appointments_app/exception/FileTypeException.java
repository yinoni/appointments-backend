package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;

public class FileTypeException extends BaseException{

    public FileTypeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }


}
