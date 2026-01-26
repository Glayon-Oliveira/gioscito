package com.lmlasmo.gioscito.service.data.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.data.dao.DocumentDAO;
import com.lmlasmo.gioscito.data.dao.Where;
import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.service.data.UpdateDataService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
public class UpdateDataServiceFactory implements DataServiceFactory<UpdateDataService> {

	@Override
	public UpdateDataService create(CollectionSchema collection, DocumentDAO gdao, FullSchema schema) {
		return new UpdateDataServiceImpl(collection.getName(), gdao);
	}
	
	@RequiredArgsConstructor
	private class UpdateDataServiceImpl implements UpdateDataService {
		
		@NonNull private final String collectionName;
		@NonNull private final DocumentDAO gDao;

		@Override
		public Mono<Map<String, Object>> updateById(String id, Map<String, Object> updateMap) {
			if(id == null) return Mono.error(() -> new IllegalArgumentException("The id cannot be null"));
			
			return gDao.updateById(id, updateMap, collectionName);
		}

		@Override
		public Mono<Long> update(Map<String, Object> updateMap, Where where) {
			if(updateMap == null) return Mono.error(() -> new IllegalArgumentException("The 'updateMap' cannot be null"));
			
			return gDao.update(updateMap, where, collectionName);
		}		
		
	}

}
