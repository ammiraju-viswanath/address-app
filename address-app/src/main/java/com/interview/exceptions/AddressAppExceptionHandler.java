package com.interview.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AddressAppExceptionHandler {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public Map<String, String> handleException(Exception exp) {
		final Map<String, String> errorDetails = new HashMap();
		errorDetails.put("errorMessage", exp.toString());
		return errorDetails;
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Map<String, String> handleMethodArgumentNotValidException(HttpRequestMethodNotSupportedException exp) {
		final Map<String, String> errorDetails = new HashMap();
		errorDetails.put("errorMessage", exp.toString());
		return errorDetails;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
		final Map<String, String> errorDetails = new HashMap();
		exp.getBindingResult().getFieldErrors()
		.forEach(err -> errorDetails.put(err.getField(), err.getDefaultMessage()));
		return errorDetails;
	}

}
