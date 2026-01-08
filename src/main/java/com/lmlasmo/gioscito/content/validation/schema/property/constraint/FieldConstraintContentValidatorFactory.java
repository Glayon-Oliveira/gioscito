package com.lmlasmo.gioscito.content.validation.schema.property.constraint;

import com.lmlasmo.gioscito.content.validation.schema.property.FieldPropertyContentValidatorFactory;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.field.property.ConstraintsProperty;
import com.lmlasmo.gioscito.model.schema.field.property.constraint.FieldConstraint;

public interface FieldConstraintContentValidatorFactory extends FieldPropertyContentValidatorFactory {
	
	public default FieldConstraint getCompatibleConstraint(FieldSchema fieldSchema, Class<? extends FieldConstraint> constraintClazz) {
		return fieldSchema.getProperties().stream()
				.filter(ConstraintsProperty.class::isInstance)
				.map(ConstraintsProperty.class::cast)
				.flatMap(p -> p.getValue().stream())
				.filter(constraintClazz::isInstance)
				.map(constraintClazz::cast)
				.findFirst()
				.orElse(null);
	}
	
}
