package com.lmlasmo.gioscito.service.data.factory;

import com.lmlasmo.gioscito.data.dao.DocumentDAO;
import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;

public interface DataServiceFactory<T> {
	
	public T create(CollectionSchema collection, DocumentDAO gdao, FullSchema schema);
	
}
