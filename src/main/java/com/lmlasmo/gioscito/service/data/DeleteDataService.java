package com.lmlasmo.gioscito.service.data;

import com.lmlasmo.gioscito.data.dao.Where;

import reactor.core.publisher.Mono;

public interface DeleteDataService {
	
	public Mono<Boolean> deleteById(String id);
	
	public Mono<Long> delete(Where where);
	
}
