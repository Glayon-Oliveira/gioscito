package com.lmlasmo.gioscito.content.validation.schema;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ContentValidationException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final Set<ValidationError> errors;
	
	public ContentValidationException(ValidationStatus status) {
		this(status.getErrors());
	}
	
}
