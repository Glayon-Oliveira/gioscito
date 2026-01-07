package com.lmlasmo.gioscito.model.schema.field.property.constraint;

import java.util.Collection;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class MinFieldConstraint implements FieldConstraint {
		
	private final FieldConstraintType constraint = FieldConstraintType.MIN;
		
	@EqualsAndHashCode.Exclude
	private final Long min;
	
	public MinFieldConstraint(Long min) {
		this.min = min != null ? min : Long.MIN_VALUE;
	}

	@Override
	public void valid(Object value) {
		if(min == null) return;
		
		if(value instanceof Number numberValue) {
			try {
				if(Long.parseLong(numberValue.toString()) < min) {
					throw new ConstraintViolationException("Value '" + value + "' is less than " + min);
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
