package com.antonio.authserver.model.exceptions.controllerexceptions;

public class NullClient extends RuntimeException {
	public NullClient() {
		super("The inserted client cannot be null!");
	}
}
