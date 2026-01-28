package com.lmlasmo.gioscito.data.validation;

import java.util.Set;

import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class CompositeFieldDataValidator {

	@NonNull
	private Set<FieldDataValidator> validators; 
	
	public Mono<ValidationStatus> valid(Object value) {
		return Flux.fromIterable(validators)
				.flatMap(v -> v.valid(value))
				.reduce(new ValidationStatus(), ValidationStatus::merge);
	}
	
}
