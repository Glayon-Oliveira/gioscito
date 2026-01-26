package com.lmlasmo.gioscito.data.validation.type;

import com.lmlasmo.gioscito.data.validation.FieldDataValidatorFactory;
import com.lmlasmo.gioscito.model.schema.field.type.FieldTypeConstant;

public interface FieldTypeDataValidatorFactory extends FieldDataValidatorFactory {

	public FieldTypeConstant getType();
	
}
