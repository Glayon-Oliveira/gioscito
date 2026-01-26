package com.lmlasmo.gioscito.data.dao;

import java.util.Map;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Component
public class DocumentDAO {

	private final ReactiveMongoTemplate template;
	
	public Mono<Map<String, Object>> create(Map<String, Object> documentMap, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");
		
		return template.save(documentMap, collectionName);
	}
	
	public Mono<Map<String, Object>> updateById(String id, Map<String, Object> updateMap, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");
		if(id == null) throw new IllegalArgumentException("The id cannot be null");
		
		Update update = new Update();
		updateMap.remove("_id");
		updateMap.forEach(update::set);
		
		Query query = Query.query(Criteria.where("_id").is(id));
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		
		return template.findAndModify(query, update, options, Document.class, collectionName)
				.map(d -> (Map<String, Object>) d);
	}
	
	public Mono<Long> update(Map<String, Object> updateMap, Where where, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");
		if(updateMap == null) throw new IllegalArgumentException("The update map cannot be null");
		
		if(where == null) return Mono.just(0L);
		
		Update update = new Update();
		updateMap.remove("_id");
		updateMap.forEach(update::set);
		
		Query query = Query.query(mountCriteriaWithWhere(where));
		
		return template.updateMulti(query, update, collectionName)
				.map(UpdateResult::getModifiedCount);
	}
	
	public Mono<Boolean> deleteById(String id, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");
		if(id == null) throw new IllegalArgumentException("The id cannot be null");
		
		Query query = new Query(Criteria.where("_id").is(id));
		return template.remove(query, collectionName)
				.map(DeleteResult::getDeletedCount)
				.map(c -> c == 1);
	}
	
	public Mono<Long> delete(Where where, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");
		
		if(where == null) return Mono.just(0L);
		
		Query query = Query.query(mountCriteriaWithWhere(where));
		
		return template.remove(query, collectionName)
				.map(DeleteResult::getDeletedCount);
	}
	
	public Mono<Map<String, Object>> findById(String id, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");
		if(id == null) throw new IllegalArgumentException("The id cannot be null");
		
		return template.findById(id, Document.class, collectionName)
				.map(d -> (Map<String, Object>) d);
	}
	
	public Flux<Map<String, Object>> find(FindControl control, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");		
		if(control == null) throw new IllegalArgumentException("The FindControl cannot be null");
		
		Query query = new Query();
		Criteria criteria = null;
		
		if(control.getPageable() != null) query.with(control.getPageable());
		
		if(control.getWhere() != null) criteria = mountCriteriaWithWhere(control.getWhere());
		
		if(control.getFields() != null) query.fields().include(control.getFields());
		
		if(criteria != null) query.addCriteria(criteria);
		
		return template.find(query, Document.class, collectionName)
				.map(d -> (Map<String, Object>) d);
	}
	
	public Mono<Boolean> existsById(String id, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");
		if(id == null) throw new IllegalArgumentException("The id cannot be null");
		
		Query query = new Query(Criteria.where("_id").is(id).exists(true));
		
		return template.exists(query, collectionName);
	}
	
	public Mono<Boolean> exists(Where where, String collectionName) {
		if(collectionName == null) throw new IllegalArgumentException("The collection name cannot be null");
		
		if(where == null) return Mono.just(false);
		
		Query query = new Query(mountCriteriaWithWhere(where));
		
		return template.exists(query, collectionName);
	}
	
	private Criteria mountCriteriaWithWhere(Where where) {
		if(where == null) new Criteria();
		
		String field = where.getField();				
		Criteria criteria = Criteria.where(field);
		
		switch(where.getType()) {
			case INCLUDES:
				String scapedValue = Pattern.quote(where.getValue().toString());
				criteria.regex(".*"+scapedValue+".*", "i");
				break;
			case EQUALS:
				criteria.is(where.getValue());
				break;
		}
		
		if(where.and() != null) {
			criteria.andOperator(mountCriteriaWithWhere(where.and()));
		}
		
		if(where.or() != null) {
			criteria.orOperator(mountCriteriaWithWhere(where.or()));
		}
		
		return criteria;
	}
	
}
