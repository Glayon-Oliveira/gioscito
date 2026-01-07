package com.lmlasmo.gioscito.model.schema.parser.syntax.field.property.constraint;

import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.field.property.constraint.FieldConstraint;
import com.lmlasmo.gioscito.model.schema.field.property.constraint.SizeFieldConstraint;
import com.lmlasmo.gioscito.model.schema.field.type.FieldType;
import com.lmlasmo.gioscito.model.schema.field.type.StringFieldType;
import com.lmlasmo.gioscito.model.schema.parser.ValueParserException;

@Component
public class SizeConstraintSyntaxParser implements FieldConstraintSyntaxParser {
	
	private static final Pattern PATTERN = Pattern.compile("^size\\(\\s*\\d+\\s*,\\s*\\d\\s*\\)$");

	@Override
	public boolean matches(String raw, FieldType fieldType) {
		return raw != null && PATTERN.matcher(raw).matches();
	}

	@Override
	public FieldConstraint parse(String raw) {
		String inner = raw.substring("size(".length(), raw.length()-1);
		inner = inner.replaceAll("\\s*", "");
		
		String[] parts = inner.split(",");
		
		try {
			Integer min = Integer.parseInt(parts[0]);
			
			if(min < 0) throw new Exception();
			
			Integer max = Integer.parseInt(parts[1]);
			return new SizeFieldConstraint(min, max);
		}catch(Exception e) {
			throw new ValueParserException("Min or Max value '" + raw + "' is not supported. Min must be integer greater than 0 and Max must be integer less than " + Integer.MAX_VALUE);
		}
	}

	@Override
	public FieldConstraint defaultConstraint(FieldType fieldType) {
		return new SizeFieldConstraint(null, null);
	}

	@Override
	public Set<Class<? extends FieldType>> getSupportedFieldTypes() {
		return Set.of(
				StringFieldType.class
				);
	}

}
