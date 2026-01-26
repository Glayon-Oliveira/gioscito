package com.lmlasmo.gioscito.data.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DataValidationCompiler {

	private final FullSchema schema;
	private final ReactiveMongoTemplate template;
	private final FieldDataValidatorFactoryRegistry factoriesRegistry;	
	
	public Set<CollectionDataValidator> generateCollectionDataValidation() {
		Set<CollectionDataValidator> collectionValidators = new HashSet<>();
		
		schema.getCollections().forEach(c -> {
			collectionValidators.add(new CollectionDataValidator(c.getName(), generateFieldValidations(c)));
		});
		
		return collectionValidators;
	}
	
	public Map<String, CompositeFieldDataValidator> generateFieldValidations(CollectionSchema collection) {
		Map<String, CompositeFieldDataValidator> fieldValidators = new HashMap<>();
		
		collection.getFields().forEach(f -> {
			Set<FieldDataValidator> validators = new LinkedHashSet<>();
			
			validators.addAll(generateFieldTypeValidator(f, collection.getName()));
			validators.addAll(generateFieldPropertyValidator(f, collection.getName()));
			
			fieldValidators.put(f.getName(), new CompositeFieldDataValidator(validators));
		});
		
		return fieldValidators;
	}
	
	private Set<FieldDataValidator> generateFieldTypeValidator(FieldSchema field, String collectionName) {
		Set<FieldDataValidator> validators = new HashSet<>();
		
		factoriesRegistry.getFieldTypeFactories().stream()
			.filter(fc -> fc.getType() == field.getType().getType())
			.map(fc -> fc.create(field, schema, collectionName, template, factoriesRegistry))
			.forEach(validators::add);
		
		return validators;
	}
	
	private Set<FieldDataValidator> generateFieldPropertyValidator(FieldSchema field, String collectionName) {
		Set<FieldDataValidator> validators = new HashSet<>();
		
		factoriesRegistry.getFieldPropertyFactories().stream()
			.filter(fc -> fc.isSupportedField(field))
			.map(fc -> fc.create(field, schema, collectionName, template, factoriesRegistry))
			.forEach(validators::add);
		
		return validators;
	}
	
}
