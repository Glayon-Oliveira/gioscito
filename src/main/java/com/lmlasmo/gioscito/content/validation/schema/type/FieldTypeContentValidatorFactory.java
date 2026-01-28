package com.lmlasmo.gioscito.content.validation.schema.type;

import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactory;
import com.lmlasmo.gioscito.content.validation.schema.FieldTypeContentValidator;
import com.lmlasmo.gioscito.model.schema.field.type.FieldTypeConstant;

public interface FieldTypeContentValidatorFactory extends FieldContentValidatorFactory<FieldTypeContentValidator> {
	
	public FieldTypeConstant getType();
	
}
