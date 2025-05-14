package com.dilshan.chat_app.exception;

public class RegisteredUserException extends RuntimeException {
    public RegisteredUserException(String message) {
        super(message);
    }
}
