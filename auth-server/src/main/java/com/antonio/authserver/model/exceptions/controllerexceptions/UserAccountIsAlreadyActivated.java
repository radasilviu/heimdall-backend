package com.antonio.authserver.model.exceptions.controllerexceptions;
public class UserAccountIsAlreadyActivated extends RuntimeException {
	public UserAccountIsAlreadyActivated(String username) {
		super("User account with the username " + username + " has been already activated.");
	}
}
