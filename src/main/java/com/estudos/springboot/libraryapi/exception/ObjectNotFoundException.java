package com.estudos.springboot.libraryapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ObjectNotFoundException extends ResponseStatusException {

	public ObjectNotFoundException(HttpStatus status, String reason) {
		super(status, reason);

	}

	private static final long serialVersionUID = 1L;

}
