package com.antonio.authserver.model.exceptions.controllerexceptions;

public class NullClient extends RuntimeException {
	public NullClient(String type) {
		super("The inserted " + type + " cannot be null!");
	}
}
