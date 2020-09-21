package com.antonio.authserver.model.exceptions.controllerexceptions;

public class NullResource extends RuntimeException {
	public NullResource(String type) {
		super("The inserted " + type + " cannot be null!");
	}
}
