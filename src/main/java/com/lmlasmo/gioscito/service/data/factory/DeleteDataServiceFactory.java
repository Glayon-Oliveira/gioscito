package com.lmlasmo.gioscito.service.data.factory;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.data.dao.DocumentDAO;
import com.lmlasmo.gioscito.data.dao.Where;
import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.service.data.DeleteDataService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
public class DeleteDataServiceFactory implements DataServiceFactory<DeleteDataService> {

	@Override
	public DeleteDataService create(CollectionSchema collection, DocumentDAO gdao, FullSchema schema) {
		return new DeleteDataServiceImpl(collection.getName(), gdao);
	}
	
	@RequiredArgsConstructor
	private class DeleteDataServiceImpl implements DeleteDataService {
		
		@NonNull private final String collectionName;
		@NonNull private final DocumentDAO gdao;

		@Override
		public Mono<Boolean> deleteById(String id) {
			if(id == null) return Mono.error(() -> new IllegalArgumentException("The id cannot be null"));
			
			return gdao.deleteById(id, collectionName);
		}

		@Override
		public Mono<Long> delete(Where where) {
			return gdao.delete(where, collectionName);
		}
		
	}

}
