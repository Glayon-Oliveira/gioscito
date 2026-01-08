package com.lmlasmo.gioscito.content.validation.schema;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComponentValidationConf {
	
	@Bean
	public Set<CollectionValidator> collectionValidations(ContentValidationCompiler validation) {
		return validation.generateCollectionValidation();
	}
	
}
