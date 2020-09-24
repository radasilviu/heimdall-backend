package com.antonio.authserver.model.exceptions.controllerexceptions;


public class BadTokenException extends RuntimeException {
    public BadTokenException(String token) {
        super("Token [ " + token + " ] can not be trusted");
    }
}
