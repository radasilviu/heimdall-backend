package com.antonio.authserver.model.exceptions.controllerexceptions;


public class TokenExpired extends RuntimeException {
    public TokenExpired(String token) {
        super("Token [" + token + "] expired!");
    }
}
