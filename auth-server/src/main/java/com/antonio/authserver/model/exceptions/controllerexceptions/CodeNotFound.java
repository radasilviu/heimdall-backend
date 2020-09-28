package com.antonio.authserver.model.exceptions.controllerexceptions;


public class CodeNotFound extends RuntimeException {
    public CodeNotFound(String code) {
        super("Code [ " + code + " ] could not be found!");
    }
}
