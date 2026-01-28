package com.lmlasmo.gioscito.data.validation.property;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.data.validation.FieldDataValidatorFactoryRegistry;
import com.lmlasmo.gioscito.data.validation.FieldPropertyDataValidator;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.field.property.UniqueProperty;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import reactor.core.publisher.Mono;

@ToString
@EqualsAndHashCode
@Component
public class UniquePropertyDataValidator implements FieldPropertyDataValidatorFactory {	
	
	@Override
	public FieldPropertyDataValidator create(FieldSchema field, FullSchema fullSchema, String collectionName,
			ReactiveMongoTemplate template, FieldDataValidatorFactoryRegistry fieldFactoryRegistry) {
		
		String fieldName = field.getName();
		boolean isUnique = field.getProperties().stream()
				.filter(UniqueProperty.class::isInstance)
				.map(UniqueProperty.class::cast)
				.findFirst()
				.orElseThrow()
				.getValue();
		
		return value -> {
			if(value == null || !isUnique) return Mono.just(new ValidationStatus());
			
			Query query = Query.query(Criteria.where(fieldName).is(value)); 
			
			return template.exists(query, collectionName)
					.filter(Boolean::booleanValue)
					.map(e -> new ValidationStatus(new ValidationError(
								fieldName,
								"Unique",
								"The value " + value + " is already used")
							))
					.switchIfEmpty(Mono.just(new ValidationStatus()));
		};
	}

	@Override
	public boolean isSupportedField(FieldSchema fieldSchema) {
		return fieldSchema.getProperties().stream()
				.anyMatch(UniqueProperty.class::isInstance);
	}

}
