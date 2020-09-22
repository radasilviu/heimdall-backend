package com.antonio.authserver.model.exceptions.controllerexceptions;

public class ClientNotFound extends RuntimeException{
    public ClientNotFound(String name) {
        super("Client with the name "+ name +" could not be found!");
    }
}
