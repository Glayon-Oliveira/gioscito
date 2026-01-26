package com.lmlasmo.gioscito.service.data;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.data.dao.DocumentDAO;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.service.data.factory.CreateDataServiceFactory;
import com.lmlasmo.gioscito.service.data.factory.DeleteDataServiceFactory;
import com.lmlasmo.gioscito.service.data.factory.FindDataServiceFactory;
import com.lmlasmo.gioscito.service.data.factory.UpdateDataServiceFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DataServicesCompiler {

	private final CreateDataServiceFactory createDataFactory;
	private final FindDataServiceFactory findDataFactory;
	private final UpdateDataServiceFactory updateDataFactory;
	private final DeleteDataServiceFactory deleteDataFactory;
	private final DocumentDAO gdao;
	private final FullSchema fullSchema;
	
	public Set<CollectionDataServiceGroup> generateDataServiceCollection() {
		Set<CollectionDataServiceGroup> dataServiceCollections = new HashSet<>();
		
		fullSchema.getCollections().forEach(c -> {
			CreateDataService createDataService = createDataFactory.create(c, gdao, fullSchema);
			FindDataService findDataService = findDataFactory.create(c, gdao, fullSchema);
			UpdateDataService updateDataService = updateDataFactory.create(c, gdao, fullSchema);
			DeleteDataService deleteDataService = deleteDataFactory.create(c, gdao, fullSchema);
			
			dataServiceCollections.add(new CollectionDataServiceGroup(
					c.getName(),
					createDataService,
					findDataService,
					updateDataService,
					deleteDataService));
		});
		
		return dataServiceCollections;
	}
	
}
