package com.lmlasmo.gioscito.content.validation.schema;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ValidationError {
	
	private final String fieldName;
	private final String error;
	private final String message;
	
}
