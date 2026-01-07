package com.lmlasmo.gioscito.model.schema.parser.syntax.field.property;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.field.property.RequiredProperty;
import com.lmlasmo.gioscito.model.schema.field.type.FieldType;
import com.lmlasmo.gioscito.model.schema.parser.ValueParserException;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Component
public class RequiredPropertySyntaxParser implements FieldPropertySyntaxParser<RequiredProperty> {
	
	private static final Pattern PATTERN = Pattern.compile("^required$");
	
	@Override
	public boolean matches(String raw) {
		return raw != null && PATTERN.matcher(raw).matches();
	}

	@Override
	public RequiredProperty parse(Object value, FieldType fieldType) {
		if(value == null) return new RequiredProperty(false);
		
		if(value instanceof Boolean required) {
			return new RequiredProperty(required);
		}else {
			throw new ValueParserException("Property 'required' must be boolean value");
		}
	}
	
}
