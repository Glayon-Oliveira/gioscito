package com.lmlasmo.gioscito.content.validation.schema.property;

import com.lmlasmo.gioscito.content.validation.schema.FieldContentValidatorFactory;
import com.lmlasmo.gioscito.model.schema.FieldSchema;

public interface FieldPropertyContentValidatorFactory extends FieldContentValidatorFactory {
	
	public boolean isSupportedField(FieldSchema fieldSchema);

}
