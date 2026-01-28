package com.lmlasmo.gioscito.content.validation.schema.type;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactoryRegistry;
import com.lmlasmo.gioscito.content.validation.schema.FieldTypeContentValidator;
import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.content.validation.schema.ValidatorFactoryException;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.field.type.EnumFieldType;
import com.lmlasmo.gioscito.model.schema.field.type.FieldTypeConstant;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Component
public class EnumTypeContentValidatorFactory implements FieldTypeContentValidatorFactory {

	@Override
	public FieldTypeContentValidator create(FieldSchema field, FullSchema fullSchema, FieldContentValidatorFactoryRegistry registry) {
		if(field.getType() instanceof EnumFieldType enumField) {
			return (value) -> {
				if(enumField.getValues().contains(value)) {
					return new ValidationStatus();
				}else {
					return new ValidationStatus(new ValidationError(
							field.getName(),
							"Enum type field",
							"Only values inclued in the list are allowed: " + enumField.getValues()));
				}
			};
		}else {
			throw new ValidatorFactoryException("Field '" + field.getName() + "' is not '" + FieldTypeConstant.ENUM + "' but '" + field.getType().getType() + "'");
		}
	}

	@Override
	public FieldTypeConstant getType() {
		return FieldTypeConstant.ENUM;
	}

}
