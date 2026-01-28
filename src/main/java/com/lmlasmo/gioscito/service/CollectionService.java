package com.lmlasmo.gioscito.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import com.lmlasmo.gioscito.content.MissingFieldException;
import com.lmlasmo.gioscito.content.UnsupportedFieldsException;
import com.lmlasmo.gioscito.content.normalization.schema.CollectionNormalizer;
import com.lmlasmo.gioscito.content.normalization.schema.FieldTypeContentNormalizer;
import com.lmlasmo.gioscito.content.validation.schema.CollectionValidator;
import com.lmlasmo.gioscito.content.validation.schema.ContentValidationException;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;
import com.lmlasmo.gioscito.data.dao.FindControl;
import com.lmlasmo.gioscito.data.dao.FindControlBuilder;
import com.lmlasmo.gioscito.data.dao.Where;
import com.lmlasmo.gioscito.data.dao.WhereFindControlBuilder;
import com.lmlasmo.gioscito.data.validation.CollectionDataValidator;
import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
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
	
	@Getter private final CollectionSchema collection;
	
	private final CollectionValidator collectionValidator;
	private final CollectionDataValidator collectionDataValidator;
	private final CollectionNormalizer collectionNormalizer;	
	private final CollectionDataServiceGroup dataServices;
	
	public Mono<Map<String, Object>> runCreate(Map<String, Object> createMap) {
		if(createMap == null) throw new IllegalArgumentException("create map cannot be null");
		
		Set<String> createFields = createMap.keySet();
		String[] missingFields = collection.getFields().stream()
				.map(FieldSchema::getName)
				.filter(fn -> !createFields.contains(fn))
				.toArray(String[]::new);
		
		if(missingFields.length > 0) {
			return Mono.error(() -> new MissingFieldException(missingFields));
		}
		
		if(collection.getFields().size() != createMap.size()) {
			return Mono.error(() -> new UnsupportedFieldsException("Unssuported fields were provided"));
		}
				
		Set<Entry<String, Object>> entryContent = createMap.entrySet();
		normalizeContent(entryContent);
		
		ValidationStatus validationStatus = validateContent(entryContent);
		
		if(!validationStatus.isValid()) {
			return Mono.error(() -> new ContentValidationException(validationStatus));
		}
		
		return validateData(entryContent)
				.flatMap(vs -> vs.isValid() 
							? dataServices.getCreateDataService().create(createMap)
							: Mono.error(() -> new ContentValidationException(vs))
				);
	}	
	
	public Mono<Map<String, Object>> runUpdateById(String id, Map<String, Object> updateMap) {
		if(id == null) throw new IllegalArgumentException("id cannot be null");
		if(updateMap == null) throw new IllegalArgumentException("update map cannot be null");
		
		Set<String> collectionFields = collection.getFields().stream()
				.map(FieldSchema::getName)
				.collect(Collectors.toSet());
		
		if(!collectionFields.containsAll(updateMap.keySet())) {
			String delimitedFields = collectionFields.stream()
					.collect(Collectors.joining(", "));
			
			return Mono.error(() -> 
				new UnsupportedFieldsException("Unssuported fields were provided. The allowed fields are: " + delimitedFields)
			);
		}
				
		Set<Entry<String, Object>> entryContent = updateMap.entrySet();
		normalizeContent(entryContent);
		
		ValidationStatus validationStatus = validateContent(entryContent);
		
		if(!validationStatus.isValid()) {
			return Mono.error(() -> new ContentValidationException(validationStatus));
		}
		
		return validateData(entryContent)
				.flatMap(vs -> vs.isValid() 
							? dataServices.getUpdateDataService().updateById(id, updateMap)
							: Mono.error(() -> new ContentValidationException(vs))
				);
	}
	
	public Mono<Long> runUpdate(Where where, Map<String, Object> updateMap) {
		if(updateMap == null) throw new IllegalArgumentException("update map cannot be null");
		
		Set<String> collectionFields = collection.getFields().stream()
				.map(FieldSchema::getName)
				.collect(Collectors.toSet());
		
		if(!collectionFields.containsAll(updateMap.keySet())) {
			String delimitedFields = collectionFields.stream()
					.collect(Collectors.joining(", "));
			
			return Mono.error(() -> 
				new UnsupportedFieldsException("Unssuported fields were provided. The allowed fields are: " + delimitedFields)
			);
		}
				
		Set<Entry<String, Object>> entryContent = updateMap.entrySet();
		normalizeContent(entryContent);
		
		ValidationStatus validationStatus = validateContent(entryContent);
		
		if(!validationStatus.isValid()) {
			return Mono.error(() -> new ContentValidationException(validationStatus));
		}
		
		return Mono.fromCallable(() -> normalizeWhere(where))
				.flatMap(w -> dataServices.getUpdateDataService().update(updateMap, w));
	}
	
	public Mono<Boolean> runDeleteById(String id) {
		if(id == null) throw new IllegalArgumentException("id cannot be null");
		
		return dataServices.getDeleteDataService().deleteById(id);
	}
	
	public Mono<Long> runDelete(Where where) {
		return Mono.fromCallable(() -> normalizeWhere(where))
				.flatMap(dataServices.getDeleteDataService()::delete);
	}
	
	public Mono<Map<String, Object>> runFindById(String id) {
		if(id == null) throw new IllegalArgumentException("id cannot be null");
		
		return dataServices.getFindDataService().findById(id);
	}
	
	public Flux<Map<String, Object>> runFind(FindControl findControl) {
		FindControlBuilder fcBuilder = FindControl.builder()
				.withPageable(findControl.getPageable())
				.includeFields(findControl.getFields().toArray(String[]::new));
		
		return Mono.fromCallable(() -> normalizeWhere(findControl.getWhere()))
				.map(w -> fcBuilder.withWhere(w).build())
				.flatMapMany(dataServices.getFindDataService()::find);
	}
	
	private ValidationStatus validateContent(Set<Entry<String, Object>> content) {
		return content.stream()
				.map(ey -> collectionValidator.getFieldsValidators()
						.get(ey.getKey())
						.valid(ey.getValue()))
				.reduce(new ValidationStatus(), ValidationStatus::merge);
	}
	
	private Where normalizeWhere(Where where) throws UnsupportedFieldsException {
		if(where == null) return null;
		
		Set<String> fields = collection.getFields().stream()
				.map(FieldSchema::getName)
				.collect(Collectors.toSet());
				
		if(!fields.contains(where.getField())) {
			throw new UnsupportedFieldsException("Unssuported field was provided: " + where.getField());
		}
		
		Set<FieldTypeContentNormalizer> normalizers = collectionNormalizer.getFieldNormalizers()
				.get(where.getField())
				.getNormalizers().stream()
				.filter(FieldTypeContentNormalizer.class::isInstance)
				.map(FieldTypeContentNormalizer.class::cast)
				.collect(Collectors.toSet());
		
		Object normalized = where.getValue();
		
		for(FieldTypeContentNormalizer normalizer: normalizers) {
			normalized = normalizer.normalize(normalized);
		}
		
		WhereFindControlBuilder builder = WhereFindControlBuilder
				.newInstance(where.getField(), normalized, where.getType());
		
		if(where.and() != null) {
			builder.andWhere(normalizeWhere(where.and()));
		}
		
		if(where.or() != null) {
			builder.orWhere(normalizeWhere(where.or()));
		}
		
		return builder.build();
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
