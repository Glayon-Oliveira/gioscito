package com.lmlasmo.gioscito.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.test.context.ActiveProfiles;

import com.lmlasmo.gioscito.content.validation.schema.CollectionValidator;
import com.lmlasmo.gioscito.content.validation.schema.ValidationStatus;

@SpringBootTest
@ComponentScans({
	@ComponentScan("com.lmlasmo.gioscito.model"),
	@ComponentScan("com.lmlasmo.gioscito.content")
	})
@ActiveProfiles("test")
class ContentValidatorTest {
	
	@Autowired
	private Set<CollectionValidator> validators;

	static Map<String, Map<String, Object>> successCollections() {
		Map<String, Object> reference = new HashMap<>();
	    reference.put("name", "Albert");

	    Map<String, Object> article = new HashMap<>();
	    article.put("id", 1L);
	    article.put("title", "Hello World");
	    article.put("category", "News");
	    article.put("views", 10);
	    article.put("likes", 1000);
	    article.put("references", List.of(reference));

	    Map<String, Map<String, Object>> root = new HashMap<>();
	    root.put("article", article);

	    return root;
	}
	
	static Map<String, Map<String, Object>> failCollections() {
		Map<String, Object> invalidReference = new HashMap<>();
	    invalidReference.put("name", null);

	    Map<String, Object> article = new HashMap<>();
	    article.put("id", "notANumber");
	    article.put("title", null);
	    article.put("category", "InvalidEnum");
	    article.put("views", -15);
	    article.put("likes", Long.MAX_VALUE);
	    article.put("references", List.of(invalidReference));

	    Map<String, Map<String, Object>> root = new HashMap<>();
	    root.put("article", article);

	    return root;
	}
	
	@Test
	void testSuccessCollections() {
		Map<String, Map<String, Object>> success = successCollections();
		
		success.entrySet().stream()
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
				
				assertTrue(status.isValid());
			});
	}
	
	@Test
	void testFailCollections() {
		Map<String, Map<String, Object>> fail = failCollections();
		
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
