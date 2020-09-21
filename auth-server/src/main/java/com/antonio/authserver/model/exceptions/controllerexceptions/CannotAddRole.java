package com.antonio.authserver.model.exceptions.controllerexceptions;

public class CannotAddRole extends RuntimeException {
    public CannotAddRole(String name) {
        super("Cannot add the role " + name + " to the user. It needs to be created first.");
    }
}
