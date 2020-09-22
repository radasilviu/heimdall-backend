package com.antonio.authserver.model.exceptions.controllerexceptions;

public class UserAlreadyExists extends RuntimeException {
    public UserAlreadyExists(String username) {
        super("User with the username " + username + " already exists!");
    }
}
