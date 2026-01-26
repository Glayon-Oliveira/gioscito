package com.lmlasmo.gioscito.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.lmlasmo.gioscito.content.UnsupportedFieldsException;
import com.lmlasmo.gioscito.content.normalization.schema.CollectionNormalizer;
import com.lmlasmo.gioscito.content.validation.schema.CollectionValidator;
import com.lmlasmo.gioscito.content.validation.schema.ContentValidationException;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.data.dao.FindControl;
import com.lmlasmo.gioscito.data.dao.Where;
import com.lmlasmo.gioscito.data.validation.CollectionDataValidator;
import com.lmlasmo.gioscito.service.data.CollectionDataServiceGroup;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class CollectionService {
	
	@Getter private final String collectionName;
	
	private final CollectionValidator collectionValidator;
	private final CollectionDataValidator collectionDataValidator;
	private final CollectionNormalizer collectionNormalizer;	
	private final CollectionDataServiceGroup dataServices;
	
	public Mono<Map<String, Object>> runCreate(Map<String, Object> createMap) {
		if(!collectionValidator.getFieldsValidators().keySet().containsAll(createMap.keySet())) {
			return Mono.error(() -> new RuntimeException());
		}
				
		Set<Entry<String, Object>> entryContent = createMap.entrySet();
		ValidationStatus validationStatus = validateContent(entryContent);
		
		if(!validationStatus.isValid()) {
			return Mono.error(() -> new ContentValidationException(validationStatus));
		}
		
		normalizeContent(entryContent);
		
		return validateData(entryContent)
				.flatMap(vs -> vs.isValid() 
							? dataServices.getCreateDataService().create(createMap)
							: Mono.error(() -> new ContentValidationException(vs))
				);
	}	
	
	public Mono<Map<String, Object>> runUpdateById(String id, Map<String, Object> updateMap) {
		if(!collectionValidator.getFieldsValidators().keySet().containsAll(updateMap.keySet())) {
			return Mono.error(() -> new RuntimeException());
		}
				
		Set<Entry<String, Object>> entryContent = updateMap.entrySet();
		ValidationStatus validationStatus = validateContent(entryContent);
		
		if(!validationStatus.isValid()) {
			return Mono.error(() -> new ContentValidationException(validationStatus));
		}
		
		normalizeContent(entryContent);
		
		return validateData(entryContent)
				.flatMap(vs -> vs.isValid() 
							? dataServices.getUpdateDataService().updateById(id, updateMap)
							: Mono.error(() -> new ContentValidationException(vs))
				);
	}
	
	public Mono<Long> runUpdate(Where where, Map<String, Object> updateMap) {
		if(!collectionValidator.getFieldsValidators().keySet().containsAll(updateMap.keySet())) {
			return Mono.error(() -> new UnsupportedFieldsException("Unssuported fields were provided"));
		}
				
		Set<Entry<String, Object>> entryContent = updateMap.entrySet();
		ValidationStatus validationStatus = validateContent(entryContent);
		
		if(!validationStatus.isValid()) {
			return Mono.error(() -> new ContentValidationException(validationStatus));
		}
		
		normalizeContent(entryContent);
		
		return dataServices.getUpdateDataService().update(updateMap, where);
	}
	
	public Mono<Boolean> runDeleteById(String id) {
		return dataServices.getDeleteDataService().deleteById(id);
	}
	
	public Mono<Long> runDelete(Where where) {
		return dataServices.getDeleteDataService().delete(where);
	}
	
	public Mono<Map<String, Object>> runFindById(String id) {
		return dataServices.getFindDataService().findById(id);
	}
	
	public Flux<Map<String, Object>> runFind(FindControl findControl) {
		return dataServices.getFindDataService().find(findControl);
	}
	
	private ValidationStatus validateContent(Set<Entry<String, Object>> content) {
		return content.stream()
				.map(ey -> collectionValidator.getFieldsValidators()
						.get(ey.getKey())
						.valid(ey.getValue()))
				.reduce(new ValidationStatus(), ValidationStatus::merge);
	}
	
	private Mono<ValidationStatus> validateData(Set<Entry<String, Object>> content) {
		return Flux.fromIterable(content)
				.flatMap(ey -> collectionDataValidator.getFieldsValidators()
						.get(ey.getKey())
						.valid(ey.getValue()))
				.reduce(new ValidationStatus(), ValidationStatus::merge);
	}
	
	private void normalizeContent(Set<Entry<String, Object>> content) {
		content.forEach(ey -> {
			Object normalizedValue = collectionNormalizer.getFieldNormalizers()
					.get(ey.getKey())
					.normalizer(ey.getValue());
			
			ey.setValue(normalizedValue);
		});
	}
	
}
