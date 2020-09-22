package com.antonio.authserver.model.exceptions.controllerexceptions;


public class IncorrectPassword extends RuntimeException {
    public IncorrectPassword(String password) {
        super("Password [" + password + "] is wrong.");
    }
}
