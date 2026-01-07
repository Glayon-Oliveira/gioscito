package com.lmlasmo.gioscito.model.schema.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.FieldSchema;
import com.lmlasmo.gioscito.model.schema.field.property.FieldProperty;
import com.lmlasmo.gioscito.model.schema.field.type.FieldType;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FieldParser {
	
	private static final Pattern FIELD_NAME_PATTERN = PatternConstants.TOP_COMPONENT_NAME_PATTERN;
	
	@NonNull private FieldTypeParser fieldTypeParser;
	@NonNull private FieldPropertyParser fieldPropertyParser;
	
	public Set<FieldSchema> parse(Map<String, Object> fieldsMap) {
		Set<FieldSchema> fieldSchemas = new HashSet<>();
		
		fieldsMap.forEach((k, v) -> {
			FieldSchema field = parse(k, v);
			
			fieldSchemas.add(field);
		});
		
		return fieldSchemas;
	}
	
	@SuppressWarnings("unchecked")
	public FieldSchema parse(String fieldName, Object field) {
		if(!FIELD_NAME_PATTERN.matcher(fieldName).matches()) {
			throw new PatternSyntaxParserException("Syntax of field name '" + fieldName + "' is invalid", FIELD_NAME_PATTERN);
		}
		
		if(field instanceof String) {
			return parseRaw(fieldName, field.toString());
		}else if(field instanceof Map map) {
			return parseMap(fieldName, map);
		}
		
		throw new KeyParserException("Key type '" + field.getClass() + "' is not supported");
	}
	
	private FieldSchema parseRaw(String fieldName, String fieldRaw) {
		if(fieldRaw == null || fieldRaw.isBlank()) {
			throw new KeyParserException("Key 'type' not found in field '"+ fieldName +"'");
		}
		
		FieldType fieldType = fieldTypeParser.parse(fieldRaw);
		Set<FieldProperty<?>> properties = fieldPropertyParser.parse(new HashMap<>(), fieldType);
		
		return new FieldSchema(fieldName, fieldType, properties);
	}
	
	private FieldSchema parseMap(String fieldName, Map<String, Object> fieldMap) {
		FieldType fieldType = fieldTypeParser.parse(fieldMap.get("type").toString());
		
		if(fieldType == null) throw new KeyParserException("Key 'type' not found in field '" + fieldName);
		
		Set<FieldProperty<?>> properties = fieldPropertyParser.parse(fieldMap, fieldType);
		
		return new FieldSchema(fieldName, fieldType, properties);
	}

}
