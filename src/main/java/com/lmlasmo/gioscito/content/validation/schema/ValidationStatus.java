package com.lmlasmo.gioscito.content.validation.schema;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ValidationStatus {

	private final Set<ValidationError> errors;
	
	public ValidationStatus() {
		this.errors = Set.of();
	}
	
	public ValidationStatus(ValidationError error) {
		Set<ValidationError> errors = new LinkedHashSet<>();
		errors.add(error);
		this.errors = Collections.unmodifiableSet(errors);
	}
	
	public ValidationStatus(Set<ValidationError> errors) {
		this.errors = Collections.unmodifiableSet(new LinkedHashSet<>(errors));
	}
	
	public boolean isValid() {
		return errors.isEmpty();
	}	

	public ValidationStatus merge(ValidationStatus other) {
		Set<ValidationError> errors = new LinkedHashSet<>(this.errors);
		errors.addAll(other.getErrors());
		
		return new ValidationStatus(errors);
	}
	
}
