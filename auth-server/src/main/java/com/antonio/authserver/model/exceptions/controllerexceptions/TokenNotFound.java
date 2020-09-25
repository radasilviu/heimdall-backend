package com.antonio.authserver.model.exceptions.controllerexceptions;


public class TokenNotFound extends RuntimeException {
    public TokenNotFound(String token) {
        super("Token [ " + token + " ] could not be found!");
    }
}
