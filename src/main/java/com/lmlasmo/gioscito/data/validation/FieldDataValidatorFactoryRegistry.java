package com.lmlasmo.gioscito.data.validation;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.data.validation.property.FieldPropertyDataValidatorFactory;
import com.lmlasmo.gioscito.data.validation.type.FieldTypeDataValidatorFactory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Component
public class FieldDataValidatorFactoryRegistry {

	private final Set<FieldTypeDataValidatorFactory> fieldTypeFactories;
	
	private final Set<FieldPropertyDataValidatorFactory> fieldPropertyFactories;
	
}
