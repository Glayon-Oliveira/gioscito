package com.lmlasmo.gioscito.model.schema.validation;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactoryRegistry;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.model.schema.ComponentSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.field.property.DefaultProperty;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DefaultValueValidator implements SchemaValidator {
	
	@NonNull
	private FieldContentValidatorFactoryRegistry registry;

	@Override
	public void validate(FullSchema schema) {
		validate(schema.getCollections(), schema);
		validate(schema.getStructs(), schema);
	}
	
	private void validate(Set<? extends ComponentSchema> components, FullSchema schema) {
		components.stream()
			.map(ComponentSchema::getFields)
			.flatMap(Set::stream)
			.forEach(f -> {
				DefaultProperty defaultProperty = f.getProperties().stream()
						.filter(DefaultProperty.class::isInstance)
						.map(DefaultProperty.class::cast)
						.findFirst()
						.orElse(null);
				
				if(defaultProperty == null || defaultProperty.getValue() == null) return;
				
				ValidationStatus status = registry.getFieldTypeFactories().stream()
					.filter(fy -> fy.getType() == f.getType().getType())
					.map(fy -> fy.create(f, schema, registry))
					.map(v -> v.valid(defaultProperty.getValue()))
					.map(s -> {
						return registry.getFieldPropertyFactories().stream()
									.filter(fy -> fy.isSupportedField(f))
									.map(fy -> fy.create(f, schema, registry))
									.map(v -> v.valid(defaultProperty.getValue()))
									.reduce(s, ValidationStatus::merge);
					})
					.reduce(new ValidationStatus(), ValidationStatus::merge);
				
				if(!status.isValid()) {
					Throwable th = status.getErrors().stream()
							.map(p -> new Throwable("Field '" + p.getFieldName() + "': " + p.getMessage()))
							.reduce(Throwable::initCause)
							.orElse(null);
					
					throw new SchemaValidationException("Default value '" + defaultProperty.getValue() + "' is invalid", th);
				}
			});
	}

}
