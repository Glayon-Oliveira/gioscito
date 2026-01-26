package com.lmlasmo.gioscito.content;

public class UnsupportedFieldsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public UnsupportedFieldsException(String message) {
		super(message);
	}
	
	public UnsupportedFieldsException(String message, Throwable th) {
		super(message, th);
	}

}
