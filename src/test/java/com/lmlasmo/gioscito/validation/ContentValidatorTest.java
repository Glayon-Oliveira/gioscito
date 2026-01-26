package com.lmlasmo.gioscito.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.test.context.ActiveProfiles;

import com.lmlasmo.gioscito.content.ContentProperties;
import com.lmlasmo.gioscito.content.validation.schema.CollectionValidator;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;

@SpringBootTest
@ComponentScans({
	@ComponentScan("com.lmlasmo.gioscito.model"),
	@ComponentScan("com.lmlasmo.gioscito.content")
	})
@ActiveProfiles("test")
public class ContentValidatorTest {
	
	@Autowired
	private Set<CollectionValidator> validators;
	
	@Test
	void testFailCollections() {
		Map<String, Map<String, Object>> fail = ContentProperties.failCreater();
		
		fail.entrySet().stream()
			.forEach(etc -> {
				ValidationStatus status = validators.stream()
						.filter(v -> v.getCollectionName().equals(etc.getKey()))
						.map(v -> {
							return etc.getValue().entrySet().stream()
									.map(etv -> {
										return v.getFieldsValidators().entrySet().stream()
												.filter(ev -> ev.getKey().equals(etv.getKey()))
												.map(ev -> ev.getValue().valid(etv.getValue()))
												.reduce(new ValidationStatus(), ValidationStatus::merge);
									})
									.reduce(new ValidationStatus(), ValidationStatus::merge);
						})
						.reduce(new ValidationStatus(), ValidationStatus::merge);
				
				assertTrue(status.getErrors().size() == etc.getValue().keySet().size());
			});
	}
	
}
