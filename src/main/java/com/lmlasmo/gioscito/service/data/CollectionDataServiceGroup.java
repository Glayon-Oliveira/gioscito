package com.lmlasmo.gioscito.service.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CollectionDataServiceGroup {
	
	private final String collectionName;
	private final CreateDataService createDataService;
	private final FindDataService findDataService;
	private final UpdateDataService updateDataService;
	private final DeleteDataService deleteDataService;
	
}
