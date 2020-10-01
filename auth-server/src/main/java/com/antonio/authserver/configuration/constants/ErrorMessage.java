package com.antonio.authserver.configuration.constants;

public enum ErrorMessage {
    INVALID_CREDENTIALS("Invalid credentials"),
    INVALID_CLIENT("Client  is invalid");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
