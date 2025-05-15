package com.dilshan.chat_app.controller;

import com.dilshan.chat_app.exception.IncorrectOtpException;
import com.dilshan.chat_app.exception.RegisteredUserException;
import com.dilshan.chat_app.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException e){
        Map<String, String> error = new HashMap<>();
        error.put("Error", "User not Found");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RegisteredUserException.class)
    public ResponseEntity<Map<String, String>> handleRegisteredUserException(RegisteredUserException e){
        Map<String, String> error = new HashMap<>();
        error.put("Error", "Registration Conflict");
        error.put("message", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IncorrectOtpException.class)
    public ResponseEntity<Map<String, String>> handleIncorrectOtpException(IncorrectOtpException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("Error", "Authentication Failed");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("Error", "Internal Server Error");
        error.put("message", "An unexpected error occurred: "+ex.getMessage());
        // Consider logging the full error details.
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
