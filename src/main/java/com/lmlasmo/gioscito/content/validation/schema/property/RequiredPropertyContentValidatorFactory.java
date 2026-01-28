package com.lmlasmo.gioscito.content.validation.schema.property;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactoryRegistry;
import com.lmlasmo.gioscito.content.validation.schema.FieldPropertyContentValidator;
import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.field.property.DefaultProperty;
import com.lmlasmo.gioscito.model.schema.field.property.RequiredProperty;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Component
public class RequiredPropertyContentValidatorFactory implements FieldPropertyContentValidatorFactory {
	
	@Override
	public FieldPropertyContentValidator create(FieldSchema field, FullSchema fullSchema, FieldContentValidatorFactoryRegistry registry) {
		RequiredProperty property = field.getProperties().stream()
				.filter(RequiredProperty.class::isInstance)
				.map(RequiredProperty.class::cast)
				.findFirst()
				.orElseThrow();
		
		DefaultProperty defaultProperty = field.getProperties().stream()
				.filter(DefaultProperty.class::isInstance)
				.map(DefaultProperty.class::cast)
				.findFirst()
				.orElse(null);
		
		return (value) -> {
			if(value == null && property.getValue() && (defaultProperty == null || defaultProperty.getValue() == null)) {
				return new ValidationStatus(new ValidationError(
						field.getName(),
						"Required",
						"It field cannot be null"
						));
			}
			
			return new ValidationStatus();
		};
	}

	@Override
	public boolean isSupportedField(FieldSchema fieldSchema) {
		return fieldSchema.getProperties().stream()
				.anyMatch(RequiredProperty.class::isInstance);
	}

}
