package com.lmlasmo.gioscito.content;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

public class MissingFieldException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	@Getter private final String[] missingFields;
	
	public MissingFieldException(String... fields) {
		super(mountMessage(fields));
		this.missingFields = fields;
	}
	
	private static String mountMessage(String... fields) {
		if(fields.length == 0) return "There are missing fields";
		
		StringBuilder strBuilder = new StringBuilder("The following fields are missing: ")
				.append(Stream.of(fields)
						.collect(Collectors.joining(", ")));
		
		return strBuilder.toString();
	}

}
