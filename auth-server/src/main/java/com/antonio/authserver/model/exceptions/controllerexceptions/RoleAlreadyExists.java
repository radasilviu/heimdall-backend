package com.antonio.authserver.model.exceptions.controllerexceptions;

public class RoleAlreadyExists extends RuntimeException {
    public RoleAlreadyExists(String name) {
        super("Role with the name [ " + name + " ] already exists!");
    }
}
