package com.lmlasmo.gioscito.content.validation.schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ContentValidationCompiler {

	private final FullSchema schema;
	private final FieldContentValidatorFactoryRegistry factoriesRegistry;
	
	public Set<CollectionValidator> generateCollectionValidation() {
		Set<CollectionValidator> collectionValidators = new HashSet<>();
		
		schema.getCollections().forEach(c -> {
			collectionValidators.add(new CollectionValidator(c.getName(), generateFieldValidations(c)));
		});
		
		return collectionValidators;
	}
	
	public Map<String, CompositeFieldValidator> generateFieldValidations(CollectionSchema component) {
		Map<String, CompositeFieldValidator> fieldValidators = new HashMap<>();
		
		component.getFields().forEach(f -> {
			Set<FieldContentValidator> validators = new LinkedHashSet<>();
			
			validators.addAll(generateFieldTypeValidator(f));
			validators.addAll(generateFieldPropertyValidator(f));
			
			fieldValidators.put(f.getName(), new CompositeFieldValidator(validators));
		});
		
		return fieldValidators;
	}
	
	private Set<FieldContentValidator> generateFieldTypeValidator(FieldSchema field) {
		Set<FieldContentValidator> validators = new HashSet<>();
		
		factoriesRegistry.getFieldTypeFactories().stream()
			.filter(fc -> fc.getType() == field.getType().getType())
			.map(fc -> fc.create(field, schema, factoriesRegistry))
			.forEach(validators::add);
		
		return validators;
	}
	
	private Set<FieldContentValidator> generateFieldPropertyValidator(FieldSchema field) {
		Set<FieldContentValidator> validators = new HashSet<>();
		
		factoriesRegistry.getFieldPropertyFactories().stream()
			.filter(fc -> fc.isSupportedField(field))
			.map(fc -> fc.create(field, schema, factoriesRegistry))
			.forEach(validators::add);
		
		return validators;
	}
	
}
