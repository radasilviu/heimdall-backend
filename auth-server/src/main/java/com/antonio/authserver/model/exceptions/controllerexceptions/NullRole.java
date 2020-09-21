package com.antonio.authserver.model.exceptions.controllerexceptions;

public class NullRole extends RuntimeException {
	public NullRole(String type) {
		super("The inserted " + type + " cannot be null!");
	}
}
