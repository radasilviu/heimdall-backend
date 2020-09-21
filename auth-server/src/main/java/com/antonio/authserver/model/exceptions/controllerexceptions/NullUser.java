package com.antonio.authserver.model.exceptions.controllerexceptions;

public class NullUser extends RuntimeException {
	public NullUser(String type) {
		super("The inserted " + type + " cannot be null!");
	}
}
