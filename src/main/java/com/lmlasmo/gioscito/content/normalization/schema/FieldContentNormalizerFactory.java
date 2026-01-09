package com.lmlasmo.gioscito.content.normalization.schema;

import com.lmlasmo.gioscito.model.schema.FieldSchema;

public interface FieldContentNormalizerFactory {

	public FieldContentNormalizer create(FieldSchema fieldSchema);
	
	public boolean supportsField(FieldSchema fieldSchema);
	
}
