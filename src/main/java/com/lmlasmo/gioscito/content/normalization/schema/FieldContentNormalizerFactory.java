package com.lmlasmo.gioscito.content.normalization.schema;

import com.lmlasmo.gioscito.model.schema.FieldSchema;

public interface FieldContentNormalizerFactory <T extends FieldContentNormalizer> {

	public T create(FieldSchema fieldSchema);
	
	public boolean supportsField(FieldSchema fieldSchema);
	
}
