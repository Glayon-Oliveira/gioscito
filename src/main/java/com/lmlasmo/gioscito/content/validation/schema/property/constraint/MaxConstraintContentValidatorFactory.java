package com.lmlasmo.gioscito.content.validation.schema.property.constraint;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.validation.schema.FieldConstraintContentValidator;
import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactoryRegistry;
import com.lmlasmo.gioscito.content.validation.schema.ValidationError;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.field.property.constraint.ConstraintViolationException;
import com.lmlasmo.gioscito.model.schema.field.property.constraint.FieldConstraint;
import com.lmlasmo.gioscito.model.schema.field.property.constraint.MaxFieldConstraint;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Component
public class MaxConstraintContentValidatorFactory implements FieldConstraintContentValidatorFactory {

	@Override
	public FieldConstraintContentValidator create(FieldSchema field, FullSchema fullSchema, FieldContentValidatorFactoryRegistry registry) {
		FieldConstraint constraint = getCompatibleConstraint(field, MaxFieldConstraint.class);
		
		return (value) -> {
			try {
				constraint.valid(value);
			}catch(ConstraintViolationException e) {
				return new ValidationStatus(new ValidationError(
						field.getName(),
						"Constraint 'Max'",
						e.getMessage()
						));
			}
			
			return new ValidationStatus();
		};
	}
	
	@Override
	public boolean isSupportedField(FieldSchema fieldSchema) {
		return getCompatibleConstraint(fieldSchema, MaxFieldConstraint.class) != null;
	}

}
