package com.lmlasmo.gioscito.content.normalization.schema;

import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class CollectionNormalizer {

	private final String collectionName;
	private final Map<String, CompositeFieldNormalizer> fieldNormalizers;
	
}
