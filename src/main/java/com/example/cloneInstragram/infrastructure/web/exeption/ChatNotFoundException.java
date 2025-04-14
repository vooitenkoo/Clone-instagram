package com.example.cloneInstragram.infrastructure.web.exeption;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(String message) {
        super(message);
    }
}