package com.lmlasmo.gioscito.data.validation.type;

import java.util.Collection;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.content.validation.schema.ValidatorFactoryException;
import com.lmlasmo.gioscito.data.validation.FieldDataValidator;
import com.lmlasmo.gioscito.data.validation.FieldDataValidatorFactoryRegistry;
import com.lmlasmo.gioscito.data.validation.FieldTypeDataValidator;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.field.type.ArrayFieldType;
import com.lmlasmo.gioscito.model.schema.field.type.FieldTypeConstant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Component
public class ArrayTypeDataValidatorFactory implements FieldTypeDataValidatorFactory {

	private final FieldTypeConstant type = FieldTypeConstant.ARRAY;
	
	@Override
	public FieldTypeDataValidator create(FieldSchema field, FullSchema schema, String collectionName,
			ReactiveMongoTemplate template, FieldDataValidatorFactoryRegistry factoriesRegistry) {
		
		if(field.getType() instanceof ArrayFieldType arrayField) {
			FieldSchema fakeSubFieldSchema = new FieldSchema(field.getName()+".array", arrayField.getSubType(), field.getProperties());
			
			FieldDataValidator subTypeValidator = factoriesRegistry.getFieldTypeFactories().stream()
					.filter(f -> f.getType() == arrayField.getSubType().getType())
					.findFirst()
					.map(f -> f.create(fakeSubFieldSchema, schema, collectionName, template, factoriesRegistry))
					.orElseThrow();
			
			return (value) -> {
				if(value == null) return Mono.just(new ValidationStatus());
				
				if(value instanceof Collection<?> collectionValue) {
					return Flux.fromIterable(collectionValue)
							.flatMap(subTypeValidator::valid)
							.reduce(new ValidationStatus(), ValidationStatus::merge);
				}else {
					ValidationError error = new ValidationError(
							field.getName(),
							"Array type field",
							"Value is not values collection");
					
					return Mono.just(new ValidationStatus(error));
				}
			};
		}else {
			throw new ValidatorFactoryException("Field '" + field.getName() + "' is not '" + FieldTypeConstant.ARRAY + "' but '" + field.getType().getType() +"'");
		}
	}
	
}
