package com.lmlasmo.gioscito.service.data.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.data.dao.FindControl;
import com.lmlasmo.gioscito.data.dao.DocumentDAO;
import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.service.data.FindDataService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FindDataServiceFactory implements DataServiceFactory<FindDataService> {

	@Override
	public FindDataService create(CollectionSchema collection, DocumentDAO gdao, FullSchema schema) {
		return new FindDataServiceImpl(collection.getName(), gdao);
	}
	
	@RequiredArgsConstructor
	private class FindDataServiceImpl implements FindDataService {
		
		@NonNull private final String collectionName;
		@NonNull private final DocumentDAO gdao;

		@Override
		public Mono<Map<String, Object>> findById(String id) {
			if(id == null) return Mono.error(() -> new IllegalArgumentException("The id cannot be null"));
			
			return gdao.findById(id, collectionName);
		}

		@Override
		public Flux<Map<String, Object>> find(FindControl control) {
			return gdao.find(control, collectionName);
		}
		
	}

}
