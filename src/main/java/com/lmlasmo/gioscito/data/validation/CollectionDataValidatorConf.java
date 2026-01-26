package com.lmlasmo.gioscito.data.validation;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectionDataValidatorConf {

	@Bean
	public Set<CollectionDataValidator> collectionDataValidations(DataValidationCompiler validation) {
		return validation.generateCollectionDataValidation();
	}
	
}
