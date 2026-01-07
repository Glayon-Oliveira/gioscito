package com.lmlasmo.gioscito.model.schema.parser.syntax.field.type;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.field.type.EnumFieldType;
import com.lmlasmo.gioscito.model.schema.field.type.FieldType;

@Component
public class EnumTypeSyntaxParser implements FieldTypeSyntaxParser {
	
	private static final Pattern PATTERN = Pattern.compile("^enum\\(\\s*\"[^\"]*\"(\\s*,\\s*\"[^\"]*\")*\\s*\\)$");

	@Override
	public boolean matches(String raw) {
		return raw != null && PATTERN.matcher(raw).matches();
	}

	@Override
	public FieldType parse(String raw) {
		String inner = raw.substring("enum(".length(), raw.length()-1);
		String trimmed = inner.trim();

	    Set<String> values = new HashSet<>();
	    StringBuilder current = new StringBuilder();
	    boolean inQuotes = false;

	    for (int i = 0; i < trimmed.length(); i++) {
	        char c = trimmed.charAt(i);

	        if (c == '"') {
	            inQuotes = !inQuotes;
	            continue;
	        }

	        if (c == ',' && !inQuotes) {
	            String v = current.toString().trim();
	            
	            if (!v.isEmpty()) values.add(v);
	            
	            current.setLength(0);
	            continue;
	        }

	        current.append(c);
	    }
	    
	    String v = current.toString().trim();
	    
	    if (!v.isEmpty()) values.add(v);

	    return new EnumFieldType(values);
	}

}
