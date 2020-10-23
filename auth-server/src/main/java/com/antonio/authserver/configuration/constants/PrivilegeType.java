package com.antonio.authserver.configuration.constants;
public enum PrivilegeType {

    READ("_READ_PRIVILEGE"),
    WRITE("_WRITE_PRIVILEGE"),
    EDIT("_EDIT_PRIVILEGE"),
    DELETE("_DELETE_PRIVILEGE");

    private final String message;

    PrivilegeType(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
