package com.lmlasmo.gioscito.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmlasmo.gioscito.content.normalization.schema.CollectionNormalizer;
import com.lmlasmo.gioscito.content.validation.schema.CollectionValidator;
import com.lmlasmo.gioscito.data.validation.CollectionDataValidator;
import com.lmlasmo.gioscito.model.schema.CollectionSchema;
import com.lmlasmo.gioscito.service.data.CollectionDataServiceGroup;
import com.lmlasmo.gioscito.service.data.DataServicesCompiler;

@Configuration
public class CollectionServiceConf {

	@Bean
	public Set<CollectionDataServiceGroup> dataServiceCollections(DataServicesCompiler compile) {
		return compile.generateDataServiceCollection();
	}
	
	@Bean
	public Set<CollectionService> collectionServices(Set<CollectionValidator> validators,
			Set<CollectionDataValidator> dataValidators,
			Set<CollectionNormalizer> normalizers,
			Set<CollectionDataServiceGroup> dataServices) {
		
		Set<CollectionService> collectionServices = new LinkedHashSet<>();
		
		dataServices.forEach(ds -> {
			CollectionSchema collection = ds.getCollection();
			
			CollectionValidator collectionValidator = validators.stream()
					.filter(cv -> cv.getCollectionName().equals(collection.getName()))
					.findFirst()
					.orElseThrow();
			
			CollectionDataValidator collectionDataValidator = dataValidators.stream()
					.filter(cv -> cv.getCollectionName().equals(collection.getName()))
					.findFirst()
					.orElseThrow();
			
			CollectionNormalizer collectionNormalizer = normalizers.stream()
					.filter(cn -> cn.getCollectionName().equals(collection.getName()))
					.findFirst()
					.orElseThrow();
			
			collectionServices.add(new CollectionService(
					ds.getCollection(),
					collectionValidator,
					collectionDataValidator,
					collectionNormalizer,
					ds));
		});
		
		return collectionServices;
	}
	
}
