package com.lmlasmo.gioscito.model.schema.validation;

public class SchemaValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SchemaValidationException(String message) {
		super(message);
	}
	
	public SchemaValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
