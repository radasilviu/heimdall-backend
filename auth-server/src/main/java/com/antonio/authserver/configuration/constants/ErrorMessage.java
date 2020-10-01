package com.antonio.authserver.configuration.constants;

public enum ErrorMessage {
    INVALID_CREDENTIALS("Invalid credentials"),
    INVALID_CLIENT("Client is invalid"),
    IDENTITY_PROVIDER_NOT_FOUND("Identity Provider not found"),
    IDENTITY_PROVIDER_EXIST("Identity Provider already exist"),
    IDENTITY_PROVIDER_NOT_NULL("Provider can not be null");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }
}
