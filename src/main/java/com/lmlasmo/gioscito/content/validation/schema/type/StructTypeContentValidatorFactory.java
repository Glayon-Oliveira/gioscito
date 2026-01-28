package com.lmlasmo.gioscito.content.validation.schema.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.CompositeFieldValidator;
import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidator;
import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactoryRegistry;
import com.lmlasmo.gioscito.content.validation.schema.FieldTypeContentValidator;
import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.content.validation.schema.ValidatorFactoryException;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.StructSchema;
import com.lmlasmo.gioscito.model.schema.field.type.FieldTypeConstant;
import com.lmlasmo.gioscito.model.schema.field.type.StructFieldType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Component
public class StructTypeContentValidatorFactory implements FieldTypeContentValidatorFactory {

	private final FieldTypeConstant type = FieldTypeConstant.STRUCT;

	@Override
	public FieldTypeContentValidator create(FieldSchema field, FullSchema fullSchema, FieldContentValidatorFactoryRegistry factoriesRegistry) {
		if(field.getType() instanceof StructFieldType structField) {
			StructSchema struct = fullSchema.getStructs().stream()
					.filter(s -> structField.getStructName().equals(s.getName()))
					.findFirst()
					.orElseThrow(() -> new ValidatorFactoryException("Struct '" + structField.getStructName() + "' not exists"));
			
			Map<String, CompositeFieldValidator> validations = generateFieldValidations(field.getName(), struct, fullSchema, factoriesRegistry);
			
			return (value) -> {
				if(value == null) return new ValidationStatus();
				
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
						return mapValue.keySet().stream()
								.map(e -> validations.get(e).valid(mapValue.get(e)))
								.reduce(new ValidationStatus(), ValidationStatus::merge);
					}
				}else {
					error = new ValidationError(
							field.getName(),
							nameError,
							"Fields inputed not corresponding as schema model 'struct' " + field.getName()
							);
				}
				
				return error == null ? new ValidationStatus() : new ValidationStatus(error);
			};
		}else {
			throw new ValidatorFactoryException("Field '" + field.getName() + "' is not '" + FieldTypeConstant.STRUCT + "' but '" + field.getType().getType() +"'");
		}
	}
	
	public Map<String, CompositeFieldValidator> generateFieldValidations(String fieldName, StructSchema structSchema, FullSchema fullSchema, FieldContentValidatorFactoryRegistry factoriesRegisty) {
		Map<String, CompositeFieldValidator> fieldValidators = new HashMap<>();
		
		structSchema.getFields().forEach(f -> {
			Set<FieldContentValidator> validators = new HashSet<>();
			
			FieldSchema fakeFieldSchema = new FieldSchema(fieldName + "." + f.getName(), f.getType(), f.getProperties());
			
			validators.addAll(generateFieldTypeValidator(fakeFieldSchema, fullSchema, factoriesRegisty));
			validators.addAll(generateFieldPropertyValidator(fakeFieldSchema, fullSchema, factoriesRegisty));
			
			fieldValidators.put(f.getName(), new CompositeFieldValidator(validators));
		});
		
		return fieldValidators;
	}
	
	private Set<FieldContentValidator> generateFieldTypeValidator(FieldSchema field, FullSchema schema, FieldContentValidatorFactoryRegistry factoriesRegistry) {
		Set<FieldContentValidator> validators = new HashSet<>();
		
		factoriesRegistry.getFieldTypeFactories().stream()
			.filter(fc -> fc.getType() == field.getType().getType())
			.map(fc -> fc.create(field, schema, factoriesRegistry))
			.forEach(validators::add);
		
		return validators;
	}
	
	private Set<FieldContentValidator> generateFieldPropertyValidator(FieldSchema field, FullSchema schema, FieldContentValidatorFactoryRegistry factoriesRegistry) {
		Set<FieldContentValidator> validators = new HashSet<>();
		
		factoriesRegistry.getFieldPropertyFactories().stream()
			.filter(fc -> fc.isSupportedField(field))
			.map(fc -> fc.create(field, schema, factoriesRegistry))
			.forEach(validators::add);
		
		return validators;
	}
	
}
