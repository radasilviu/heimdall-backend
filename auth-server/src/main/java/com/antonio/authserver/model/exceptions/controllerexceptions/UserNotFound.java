package com.antonio.authserver.model.exceptions.controllerexceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String username) {
        super("User with the username [ " + username + " ] could not be found!");
    }
}
