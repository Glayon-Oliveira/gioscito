package com.lmlasmo.gioscito.content.validation.schema;

import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class CollectionValidator {

	@NonNull private final String collectionName;
	@NonNull private final Map<String, CompositeFieldValidator> fieldsValidators;
	
}
