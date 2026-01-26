package com.lmlasmo.gioscito.service.data;

import java.util.Map;

import com.lmlasmo.gioscito.data.dao.Where;

import reactor.core.publisher.Mono;

public interface UpdateDataService {
	
	public Mono<Map<String, Object>> updateById(String id, Map<String, Object> updateMap);
	
	public Mono<Long> update(Map<String, Object> updateMap, Where where);
	
}
