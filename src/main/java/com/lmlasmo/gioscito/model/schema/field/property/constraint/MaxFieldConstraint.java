package com.lmlasmo.gioscito.model.schema.field.property.constraint;

import java.util.Collection;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class MaxFieldConstraint implements FieldConstraint {

	private final FieldConstraintType constraint = FieldConstraintType.MAX;
		
	@EqualsAndHashCode.Exclude	
	private final Long max;
	
	public MaxFieldConstraint(Long max) {
		this.max = max != null ? max : Long.MAX_VALUE;
	}
	
	public void valid(Object value) {
		if(max == null) return;
		
		if(value instanceof Number numberValue) {
			try {
				if(Long.parseLong(numberValue.toString()) > max) {
					throw new ConstraintViolationException("Value '" + value + "' is greater than " + max);
				}
			}catch(Exception e) {
				throw new ValueTypeViolationException("Type of value '" + value + "' must be long integer");
			}
		}else if(value instanceof Collection<?> valueCollection) {
			valueCollection.stream()
				.forEach(this::valid);
		}
	}
	
}
