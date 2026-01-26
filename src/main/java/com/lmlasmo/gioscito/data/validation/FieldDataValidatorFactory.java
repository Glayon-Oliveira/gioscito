package com.lmlasmo.gioscito.data.validation;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;

public interface FieldDataValidatorFactory {

	public FieldDataValidator create(FieldSchema field, FullSchema schema, String collectionName, ReactiveMongoTemplate template, FieldDataValidatorFactoryRegistry factoriesRegistry);
	
}
