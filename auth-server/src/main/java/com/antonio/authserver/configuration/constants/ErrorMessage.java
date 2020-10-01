package com.antonio.authserver.configuration.constants;

public enum ErrorMessage {
    INVALID_CREDENTIALS("Invalid credentials"),
    REALM_NOT_FOUND("Realm not found");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
