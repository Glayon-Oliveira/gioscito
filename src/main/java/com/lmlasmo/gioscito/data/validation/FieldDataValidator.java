package com.lmlasmo.gioscito.data.validation;

import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;

import reactor.core.publisher.Mono;

public interface FieldDataValidator {

	public Mono<ValidationStatus> valid(Object value);
	
}
