package com.lmlasmo.gioscito.content.validation.schema.type;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidator;
import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactoryRegistry;
import com.lmlasmo.gioscito.content.validation.schema.FieldTypeContentValidator;
import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.content.validation.schema.ValidatorFactoryException;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.field.type.ArrayFieldType;
import com.lmlasmo.gioscito.model.schema.field.type.FieldTypeConstant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Component
public class ArrayTypeContentValidatorFactory implements FieldTypeContentValidatorFactory {

	private final FieldTypeConstant type = FieldTypeConstant.ARRAY;

	@Override
	public FieldTypeContentValidator create(FieldSchema field, FullSchema schema, FieldContentValidatorFactoryRegistry factoriesRegistry) {
		if(field.getType() instanceof ArrayFieldType arrayField) {
			FieldSchema fakeSubFieldSchema = new FieldSchema(field.getName()+".array", arrayField.getSubType(), field.getProperties());
			
			FieldContentValidator subTypeValidator = factoriesRegistry.getFieldTypeFactories().stream()
					.filter(f -> f.getType() == arrayField.getSubType().getType())
					.findFirst()
					.map(f -> f.create(fakeSubFieldSchema, schema, factoriesRegistry))
					.orElseThrow();
			
			return (value) -> {
				if(value == null) return new ValidationStatus();
				
				if(value instanceof Collection<?> collectionValue) {
					return collectionValue.stream()
							.map(subTypeValidator::valid)
							.reduce(new ValidationStatus(), ValidationStatus::merge);
				}else {
					ValidationError error = new ValidationError(
							field.getName(),
							"Array type field",
							"Value is not values collection");
					
					return new ValidationStatus(error);
				}
			};
		}else {
			throw new ValidatorFactoryException("Field '" + field.getName() + "' is not '" + FieldTypeConstant.ARRAY + "' but '" + field.getType().getType() +"'");
		}
	}
	
}
