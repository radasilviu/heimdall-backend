package com.antonio.authserver.model.exceptions.controllerexceptions;

public class RoleNotFound extends RuntimeException {
    public RoleNotFound(String name) {
        super("Role with the name " + name + " could not be found!");
    }
}
