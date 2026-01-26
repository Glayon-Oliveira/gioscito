package com.lmlasmo.gioscito.service.data.factory;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.data.dao.DocumentDAO;
import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.service.data.CreateDataService;

@Component
public class CreateDataServiceFactory implements DataServiceFactory<CreateDataService> {

	@Override
	public CreateDataService create(CollectionSchema collection, DocumentDAO gdao, FullSchema schema) {
		String collectionName = collection.getName();
		
		return createMap -> gdao.create(createMap, collectionName);
	}

}
