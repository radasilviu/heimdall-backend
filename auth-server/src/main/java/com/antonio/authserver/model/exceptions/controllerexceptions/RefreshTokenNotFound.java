package com.antonio.authserver.model.exceptions.controllerexceptions;


public class RefreshTokenNotFound extends RuntimeException {
    public RefreshTokenNotFound(String refreshToken) {
        super("RefreshToken [ " + refreshToken + " ] could not be found!");
    }
}
