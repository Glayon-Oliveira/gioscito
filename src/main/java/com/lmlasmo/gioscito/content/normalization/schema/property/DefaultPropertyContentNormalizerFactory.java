package com.lmlasmo.gioscito.content.normalization.schema.property;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.content.normalization.schema.FieldPropertyContentNormalizer;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.field.property.DefaultProperty;
import com.lmlasmo.gioscito.model.schema.field.property.FieldPropertyType;

@Component
public class DefaultPropertyContentNormalizerFactory implements FieldPropertyContentNormalizerFactory {

	@Override
	public FieldPropertyContentNormalizer create(FieldSchema fieldSchema) {
		DefaultProperty property = getProperty(fieldSchema);
				
		Object defaultValue = property != null ? property.getValue() : null;
		
		return value -> {
			if(defaultValue == null || value != null) return value;
			
			return value;
		};
	}

	@Override
	public boolean supportsField(FieldSchema fieldSchema) {
		DefaultProperty property = getProperty(fieldSchema);
		return property != null && property.getValue() != null;
	}

	private DefaultProperty getProperty(FieldSchema fieldSchema) {
		return fieldSchema.getProperties().stream()
				.filter(f -> f.getType() ==  FieldPropertyType.DEFAULT)
				.map(DefaultProperty.class::cast)
				.findFirst()
				.orElse(null);
	}
	
}
