package com.lmlasmo.gioscito.service.data;

import java.util.Map;

import reactor.core.publisher.Mono;

public interface CreateDataService {
	
	public Mono<Map<String, Object>> create(Map<String, Object> createMap);
	
}
