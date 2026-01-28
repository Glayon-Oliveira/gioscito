package com.lmlasmo.gioscito.data.validation;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;

public interface FieldDataValidatorFactory <T extends FieldDataValidator> {

	public T create(FieldSchema field, FullSchema schema, String collectionName, ReactiveMongoTemplate template, FieldDataValidatorFactoryRegistry factoriesRegistry);
	
}
