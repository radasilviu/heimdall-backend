package com.antonio.authserver.model.exceptions.controllerexceptions;


public class SessionExpired extends RuntimeException {
    public SessionExpired() {
        super("Your session has been expired, please log in again");
    }
}
