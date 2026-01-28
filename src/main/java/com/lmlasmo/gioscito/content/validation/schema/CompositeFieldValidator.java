package com.lmlasmo.gioscito.content.validation.schema;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class CompositeFieldValidator {
	
	@NonNull
	private Set<FieldContentValidator> validators; 
	
	public ValidationStatus valid(Object value) {
		return validators.stream()
				.map(v -> v.valid(value))
				.reduce(new ValidationStatus(), ValidationStatus::merge);
	}

}
