package com.antonio.authserver.model.exceptions.controllerexceptions;

public class NullRole extends RuntimeException {
	public NullRole() {
		super("The inserted Role cannot be null!");
	}
}
