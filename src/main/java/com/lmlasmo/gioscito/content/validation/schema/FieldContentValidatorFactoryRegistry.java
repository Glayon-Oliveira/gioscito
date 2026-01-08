package com.lmlasmo.gioscito.content.validation.schema;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.property.FieldPropertyContentValidatorFactory;
import com.lmlasmo.gioscito.content.validation.schema.type.FieldTypeContentValidatorFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Component
public class FieldContentValidatorFactoryRegistry {

	private final Set<FieldTypeContentValidatorFactory> fieldTypeFactories;
	
	private final Set<FieldPropertyContentValidatorFactory> fieldPropertyFactories;
	
}
