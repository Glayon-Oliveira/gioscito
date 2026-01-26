package com.lmlasmo.gioscito.service.data;

import java.util.Map;

import com.lmlasmo.gioscito.data.dao.FindControl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindDataService {
	
	public Mono<Map<String, Object>> findById(String id);
	
	public Flux<Map<String, Object>> find(FindControl control);
	
}
