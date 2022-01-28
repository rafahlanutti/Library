package com.estudos.springboot.libraryapi.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.ObjectError;

public class ApiErrors {

	private List<String> errors = new ArrayList<>();

	public List<String> getErrors() {
		return errors;
	}

	public ApiErrors(List<ObjectError> allErrors) {
		allErrors.forEach(error -> this.errors.add(error.getDefaultMessage()));
	}

	public ApiErrors(BusinessException ex) {
		this.errors.add(ex.getMessage());
	}

}
