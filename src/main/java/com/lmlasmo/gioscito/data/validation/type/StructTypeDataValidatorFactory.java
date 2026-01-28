package com.lmlasmo.gioscito.data.validation.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.content.validation.schema.ValidatorFactoryException;
import com.lmlasmo.gioscito.data.validation.CompositeFieldDataValidator;
import com.lmlasmo.gioscito.data.validation.FieldDataValidator;
import com.lmlasmo.gioscito.data.validation.FieldDataValidatorFactoryRegistry;
import com.lmlasmo.gioscito.data.validation.FieldTypeDataValidator;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.StructSchema;
import com.lmlasmo.gioscito.model.schema.field.type.FieldTypeConstant;
import com.lmlasmo.gioscito.model.schema.field.type.StructFieldType;

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
public class StructTypeDataValidatorFactory implements FieldTypeDataValidatorFactory {

	private final FieldTypeConstant type = FieldTypeConstant.STRUCT;
	
	@Override
	public FieldTypeDataValidator create(FieldSchema field, FullSchema schema, String collectionName,
			ReactiveMongoTemplate template, FieldDataValidatorFactoryRegistry factoriesRegistry) {
		
		if(field.getType() instanceof StructFieldType structField) {
			StructSchema struct = schema.getStructs().stream()
					.filter(s -> structField.getStructName().equals(s.getName()))
					.findFirst()
					.orElseThrow(() -> new ValidatorFactoryException("Struct '" + structField.getStructName() + "' not exists"));
			
			Map<String, CompositeFieldDataValidator> validations = generateFieldValidations(field.getName(), struct, schema, collectionName, template, factoriesRegistry);
			
			return (value) -> {
				if(value == null) return Mono.just(new ValidationStatus());
				
				String nameError = "Struct type field";
				ValidationError error = null;
				
				if(value instanceof Map<?, ?> mapValue) {					
					if(!validations.keySet().containsAll(mapValue.keySet())) {
						error = new ValidationError(
								field.getName(),
								nameError,
								"Fields inputed not corresponding as schema model 'struct' " + field.getName()
								);
					}else {
						return Flux.fromIterable(mapValue.entrySet())
								.flatMap(ey -> validations.get(ey.getKey())
										.valid(ey.getValue()))
								.reduce(new ValidationStatus(), ValidationStatus::merge);
					}
				}else {
					error = new ValidationError(
							field.getName(),
							nameError,
							"Fields inputed not corresponding as schema model 'struct' " + field.getName()
							);
				}
				
				return error == null 
						? Mono.just(new ValidationStatus()) 
						: Mono.just(new ValidationStatus(error));
			};
		}else {
			throw new ValidatorFactoryException("Field '" + field.getName() + "' is not '" + FieldTypeConstant.STRUCT + "' but '" + field.getType().getType() +"'");
		}
	}
	
	private Map<String, CompositeFieldDataValidator> generateFieldValidations(String fieldName, StructSchema structSchema, 
			FullSchema schema, String collectionName, ReactiveMongoTemplate template,
			FieldDataValidatorFactoryRegistry factoriesRegistry) {
		
		Map<String, CompositeFieldDataValidator> fieldValidators = new HashMap<>();
		
		structSchema.getFields().forEach(f -> {
			Set<FieldDataValidator> validators = new HashSet<>();
			
			FieldSchema fakeFieldSchema = new FieldSchema(fieldName + "." + f.getName(), f.getType(), f.getProperties());
			
			validators.addAll(generateFieldTypeValidator(fakeFieldSchema, schema, collectionName, template, factoriesRegistry));
			validators.addAll(generateFieldPropertyValidator(fakeFieldSchema, schema, collectionName, template, factoriesRegistry));
			
			fieldValidators.put(f.getName(), new CompositeFieldDataValidator(validators));
		});
		
		return fieldValidators;
	}
	
	private Set<FieldDataValidator> generateFieldTypeValidator(FieldSchema field, FullSchema schema, String collectionName,
			ReactiveMongoTemplate template, FieldDataValidatorFactoryRegistry factoriesRegistry) {
		
		Set<FieldDataValidator> validators = new HashSet<>();
		
		factoriesRegistry.getFieldTypeFactories().stream()
			.filter(fc -> fc.getType() == field.getType().getType())
			.map(fc -> fc.create(field, schema, collectionName, template, factoriesRegistry))
			.forEach(validators::add);
		
		return validators;
	}
	
	private Set<FieldDataValidator> generateFieldPropertyValidator(FieldSchema field, FullSchema schema, String collectionName,
			ReactiveMongoTemplate template, FieldDataValidatorFactoryRegistry factoriesRegistry) {
		
		Set<FieldDataValidator> validators = new HashSet<>();
		
		factoriesRegistry.getFieldPropertyFactories().stream()
			.filter(fc -> fc.isSupportedField(field))
			.map(fc -> fc.create(field, schema, collectionName, template, factoriesRegistry))
			.forEach(validators::add);
		
		return validators;
	}
	
}
