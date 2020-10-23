package com.antonio.authserver.configuration.constants;
public enum PrivilegeType {

    READ("READ_PRIVILEGE"),
    WRITE("WRITE_PRIVILEGE"),
    EDIT("EDIT_PRIVILEGE"),
    DELETE("DELETE_PRIVILEGE");

    private final String message;

    PrivilegeType(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
