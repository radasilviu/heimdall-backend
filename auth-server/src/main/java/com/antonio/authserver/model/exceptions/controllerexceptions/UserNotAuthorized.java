package com.antonio.authserver.model.exceptions.controllerexceptions;

public class UserNotAuthorized extends RuntimeException {

    public UserNotAuthorized() {
        super("User with the username is not authorized!");
    }

    public UserNotAuthorized(String username) {
        super("User with the username [ " + username + " ] is not authorized!");
    }
}
