package com.lmlasmo.gioscito.content.normalization.schema;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Component
public class ContentNormalizerCompiler {

	private final FullSchema schema;
	private final FieldContentNormalizerFactoryRegistry factoryRegistry;
	
	public Set<CollectionNormalizer> generateCollectionNormalizers() {
		Set<CollectionNormalizer> normalizers = new LinkedHashSet<>();
		
		schema.getCollections().stream()
			.map(c -> new CollectionNormalizer(c.getName(), generateFieldNormalizers(c)))
			.forEach(normalizers::add);
		
		return normalizers;
	}
	
	private Map<String, CompositeFieldNormalizer> generateFieldNormalizers(CollectionSchema collectionSchema) {
		Map<String, CompositeFieldNormalizer> fieldNormalizers = new LinkedHashMap<>();
		
		collectionSchema.getFields().forEach(f -> {
			CompositeFieldNormalizer compositeNormalizer = new CompositeFieldNormalizer(generateFieldNormalizers(f));
			fieldNormalizers.put(f.getName(), compositeNormalizer);
		});
		
		return fieldNormalizers;
	}
	
	private Set<FieldContentNormalizer> generateFieldNormalizers(FieldSchema fieldSchema) {
		Set<FieldContentNormalizer> fieldNormalizers = new LinkedHashSet<>();
		
		fieldNormalizers.addAll(
				generateFieldNormalizers(
						fieldSchema,
						factoryRegistry.getTypeNormalizerFactories()
						)
				);
		
		fieldNormalizers.addAll(
				generateFieldNormalizers(
						fieldSchema,
						factoryRegistry.getPropertyNormalizerFactories()
						)
				);
		
		return fieldNormalizers;
	}
	
	@SuppressWarnings("rawtypes")
	private Set<FieldContentNormalizer> generateFieldNormalizers(FieldSchema fieldSchema, Set<? extends FieldContentNormalizerFactory> factories) {
		Set<FieldContentNormalizer> fieldNormalizers = new LinkedHashSet<>();
		
		factories.stream()
			.filter(fn -> fn.supportsField(fieldSchema))
			.map(fn -> fn.create(fieldSchema))
			.forEach(fieldNormalizers::add);
		
		return fieldNormalizers;
	}
	
}
