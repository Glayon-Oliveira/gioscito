package com.lmlasmo.gioscito.model.schema.parser.syntax.field.property;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.field.property.ConstraintsProperty;
import com.lmlasmo.gioscito.model.schema.field.property.constraint.FieldConstraint;
import com.lmlasmo.gioscito.model.schema.field.type.FieldType;
import com.lmlasmo.gioscito.model.schema.parser.ValueParserException;
import com.lmlasmo.gioscito.model.schema.parser.syntax.field.property.constraint.FieldConstraintSyntaxParser;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
@Component
public class ConstraintsPropertySyntaxParser implements FieldPropertySyntaxParser<ConstraintsProperty> {
	
	private static final Pattern PATTERN = Pattern.compile("^constraints$");
	
	@NonNull
	private final Set<FieldConstraintSyntaxParser> fieldConstraintParsers;
	
	@Override
	public boolean matches(String raw) {
		return raw != null && PATTERN.matcher(raw).matches();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ConstraintsProperty parse(Object property, FieldType fieldType) {		
		if(property instanceof Collection || property == null) {
			Set<FieldConstraint> fieldConstraints = parse((Collection<String>) property, fieldType);
			
			return new ConstraintsProperty(fieldConstraints);
		}else {
			throw new ValueParserException("Property 'constraints' must be " + property.getClass());
		}
	}
	
	private Set<FieldConstraint> parse(Collection<String> constraints, FieldType fieldType) {
		if(constraints == null) constraints = new HashSet<>();
		
		Set<FieldConstraint> fieldConstraints = new HashSet<>();
		
		for(String constraint: constraints) {
			FieldConstraint fieldConstraint = fieldConstraintParsers.stream()
					.filter(p -> p.matches(constraint, fieldType))
					.findFirst()
					.map(p -> p.parse(constraint))
					.orElseThrow(() -> new RuntimeException("Constraint '" + constraint +"' is invalid"));
			
			fieldConstraints.add(fieldConstraint);
		}
		
		for (FieldConstraintSyntaxParser parser : fieldConstraintParsers) {
	        if (!parser.supports(fieldType)) continue;

	        FieldConstraint defaultConstraint = parser.defaultConstraint(fieldType);
	        
	        if (defaultConstraint == null) continue;

	        boolean exists = fieldConstraints.stream()
	        		.anyMatch(fc -> fc.getConstraint() == fc.getConstraint());
	        
	        if (!exists) fieldConstraints.add(defaultConstraint);
	    }
		
		return fieldConstraints;
	}
	
}
