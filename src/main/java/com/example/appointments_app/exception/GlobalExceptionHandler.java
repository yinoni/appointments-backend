package com.example.appointments_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> hanleAuthException(AuthenticationException ae){
        ErrorResponse er = new ErrorResponse(ae.getMessage(), ae.getStatus());

        return new ResponseEntity<>(er, er.getStatus());
    }

    @ExceptionHandler(JWTException.class)
    public ResponseEntity<?> handleJWTException(JWTException je){
        ErrorResponse er = new ErrorResponse(je.getMessage(), je.getStatus());

        return new ResponseEntity<>(er, er.getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException unfe){
        ErrorResponse er = new ErrorResponse(unfe.getMessage(), unfe.getStatus());

        return new ResponseEntity<>(er, er.getStatus());
    }

    @ExceptionHandler(BusinessCreationException.class)
    public ResponseEntity<?> handleBusinessCreationException(BusinessCreationException bce){
        ErrorResponse er = new ErrorResponse(bce.getMessage(), bce.getStatus());

        return new ResponseEntity<>(er, er.getStatus());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessCreationException(BusinessException be){
        ErrorResponse er = new ErrorResponse(be.getMessage(), be.getStatus());

        return new ResponseEntity<>(er, er.getStatus());
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<?> handleBusinessCreationException(ServiceNotFoundException be){
        ErrorResponse er = new ErrorResponse(be.getMessage(), be.getStatus());

        return new ResponseEntity<>(er, er.getStatus());
    }
}
