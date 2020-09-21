package com.antonio.authserver.model.exceptions.controllerexceptions;

public class NullUser extends RuntimeException {
	public NullUser() {
		super("The inserted user cannot be null!");
	}
}
