package com.lmlasmo.gioscito.content.normalization.schema;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.normalization.schema.property.FieldPropertyContentNormalizerFactory;
import com.lmlasmo.gioscito.content.normalization.schema.type.FieldTypeContentNormalizerFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Component
public class FieldContentNormalizerFactoryRegistry {

	private final Set<FieldTypeContentNormalizerFactory> typeNormalizerFactories;
	private final Set<FieldPropertyContentNormalizerFactory> propertyNormalizerFactories;
	
}
