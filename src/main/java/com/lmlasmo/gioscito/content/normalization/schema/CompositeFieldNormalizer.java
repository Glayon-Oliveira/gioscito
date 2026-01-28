package com.lmlasmo.gioscito.content.normalization.schema;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class CompositeFieldNormalizer {

	private final Set<FieldContentNormalizer> normalizers;
	
	public Object normalizer(Object value) {
		Object normalizedValue = value;
		
		for(FieldContentNormalizer normalizer: normalizers) {
			normalizedValue = normalizer.normalize(normalizedValue);
		}
		
		return normalizedValue;
	}
	
}
