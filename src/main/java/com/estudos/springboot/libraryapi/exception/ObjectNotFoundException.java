package com.estudos.springboot.libraryapi.exception;

public class ObjectNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ObjectNotFoundException(String reason) {
		super(reason);

	}

}
