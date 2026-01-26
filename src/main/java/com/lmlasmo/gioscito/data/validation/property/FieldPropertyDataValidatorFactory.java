package com.lmlasmo.gioscito.data.validation.property;

import com.lmlasmo.gioscito.data.validation.FieldDataValidatorFactory;
import com.lmlasmo.gioscito.model.schema.FieldSchema;

public interface FieldPropertyDataValidatorFactory extends FieldDataValidatorFactory {

	public boolean isSupportedField(FieldSchema fieldSchema);
	
}
