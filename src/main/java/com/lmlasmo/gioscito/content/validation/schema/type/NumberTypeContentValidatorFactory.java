package com.lmlasmo.gioscito.content.validation.schema.type;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactoryRegistry;
import com.lmlasmo.gioscito.content.validation.schema.FieldTypeContentValidator;
import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.content.validation.schema.ValidatorFactoryException;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.field.type.FieldTypeConstant;
import com.lmlasmo.gioscito.model.schema.field.type.NumberFieldType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Component
public class NumberTypeContentValidatorFactory implements FieldTypeContentValidatorFactory {
	
	private final FieldTypeConstant type = FieldTypeConstant.NUMBER;

	@Override
	public FieldTypeContentValidator create(FieldSchema field, FullSchema schema, FieldContentValidatorFactoryRegistry factoriesRegistry) {
		if(!(field.getType() instanceof NumberFieldType)) throw new ValidatorFactoryException("Field '" + field.getName() + "' is not '" + FieldTypeConstant.NUMBER + "' but '" + field.getType().getType() +"'");
		
		return (value) -> {
			if(value == null) return new ValidationStatus();
			
			String nameError = "Number type field";
			ValidationError error = null;
			
			if (
				value instanceof Long ||
				value instanceof Integer ||
				value instanceof Short ||
				value instanceof Byte
			){
				return new ValidationStatus();
			}else if (value instanceof Number) {
				error = new ValidationError(field.getName(), nameError, "Numeric type not supported");
	        }else {
	        	error = new ValidationError(field.getName(), nameError, "Value '" + value + "' must be long integer");
	        }
			
			return error == null ? new ValidationStatus() : new ValidationStatus(error);
		};
	}	

}
