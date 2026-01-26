package com.lmlasmo.gioscito.data.validation;

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
public class CollectionDataValidator {

	@NonNull private final String collectionName;
	@NonNull private final Map<String, CompositeFieldDataValidator> fieldsValidators;
	
}
