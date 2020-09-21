package com.antonio.authserver.model.exceptions.controllerexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionAdvice {

	@ResponseBody
	@ExceptionHandler(ClientAlreadyExists.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String clientAlreadyExists(ClientAlreadyExists e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(RoleAlreadyExists.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String roleAlreadyExists(RoleAlreadyExists e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(UserAlreadyExists.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public String userAlreadyExists(UserAlreadyExists e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(ClientNotFound.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String clientNotFound(ClientNotFound e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(RoleNotFound.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String roleNotFound(RoleNotFound e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(UserNotFound.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String userNotFound(UserNotFound e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(CannotAddRole.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String cannotAddRole(CannotAddRole e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(NullResource.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String nullClient(NullResource e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(NullRole.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String nullRole(NullRole e) {
		return e.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(NullUser.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String nullUser(NullUser e) {
		return e.getMessage();
	}
}
