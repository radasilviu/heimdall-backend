package com.antonio.authserver.model.exceptions.controllerexceptions;

public class ClientAlreadyExists extends RuntimeException{
    public ClientAlreadyExists(String name) {
        super("Client with the name " + name + " already exists!");
    }
}
