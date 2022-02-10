package com.estudos.springboot.libraryapi.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidateExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrors> handleValidationExceptions(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		BindingResult bindingResult = ex.getBindingResult();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrors(bindingResult.getAllErrors()));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiErrors> handleValidationExceptions(BusinessException ex, HttpServletRequest request) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiErrors(ex));
	}

	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<ApiErrors> handleObjectNotFoundExceptions(ObjectNotFoundException ex,
			HttpServletRequest request) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrors(ex));
	}
}
