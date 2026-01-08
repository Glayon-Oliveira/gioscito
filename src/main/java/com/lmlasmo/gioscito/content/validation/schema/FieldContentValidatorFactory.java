package com.lmlasmo.gioscito.content.validation.schema;

import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;

public interface FieldContentValidatorFactory {
	
	public FieldContentValidator create(FieldSchema field, FullSchema fullSchema, FieldContentValidatorFactoryRegistry registry);
	
}
