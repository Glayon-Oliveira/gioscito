package com.lmlasmo.gioscito.service.data;

import com.lmlasmo.gioscito.model.schema.CollectionSchema;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CollectionDataServiceGroup {
	
	private final CollectionSchema collection;
	private final CreateDataService createDataService;
	private final FindDataService findDataService;
	private final UpdateDataService updateDataService;
	private final DeleteDataService deleteDataService;
	
}
