package com.lmlasmo.gioscito.model.schema.parser.syntax.field.property;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.field.property.UniqueProperty;
import com.lmlasmo.gioscito.model.schema.field.type.FieldType;
import com.lmlasmo.gioscito.model.schema.parser.ValueParserException;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Component
public class UniquePropertySyntaxParser implements FieldPropertySyntaxParser<UniqueProperty> {
	
	private static final Pattern PATTERN = Pattern.compile("^unique$");
	
	@Override
	public boolean matches(String raw) {
		return raw != null && PATTERN.matcher(raw).matches();
	}

	@Override
	public UniqueProperty parse(Object value, FieldType fieldType) {
		if(value == null) return new UniqueProperty(false);
		
		if(value instanceof Boolean unique) {
			return new UniqueProperty(unique);
		}else {
			throw new ValueParserException("Property 'unique' must be boolean value");
		}
	}

}
