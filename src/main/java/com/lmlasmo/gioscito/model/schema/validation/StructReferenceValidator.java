package com.lmlasmo.gioscito.model.schema.validation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.lmlasmo.gioscito.model.schema.ComponentSchema;
import com.lmlasmo.gioscito.model.schema.FullSchema;
import com.lmlasmo.gioscito.model.schema.StructSchema;
import com.lmlasmo.gioscito.model.schema.field.type.ArrayFieldType;
import com.lmlasmo.gioscito.model.schema.field.type.StructFieldType;

@Component
public class StructReferenceValidator implements SchemaValidator {

	@Override
	public void validate(FullSchema schema) {
		validate(schema.getCollections(), schema.getStructs());
		validate(schema.getStructs());
	}
	
	private void validate(Set<StructSchema> structSchemas) {
		validate(structSchemas, structSchemas);
	}
	
	private void validate(Set<? extends ComponentSchema> componentSchemas, Set<StructSchema> structSchemas) {
		componentSchemas.forEach(c -> {
			c.getFields().forEach(f -> {
					if(f.getType() instanceof StructFieldType structType) {
						validate(structType.getStructName(), structSchemas);
					}else if(f.getType() instanceof ArrayFieldType arrayType 
							&& arrayType.getSubType() instanceof StructFieldType structType) {
						validate(structType.getStructName(), structSchemas);
					}
				});
			});
	}
	
	private void validate(String structName, Set<StructSchema> structSchemas) {
		StructSchema struct = structSchemas.stream()
				.filter(s -> s.getName().equals(structName))
				.findFirst()
				.orElse(null);
		
		if(struct == null) {
			throw new SchemaValidationException("Struct '" + structName + "' not exists");
		}else {
			validateNoCicle(struct, structSchemas);
		}
	}
	
	private void validateNoCicle(StructSchema structSchema, Set<StructSchema> structSchemas) {
		validateNoCicle(structSchema, structSchemas, null);
	}
	
	private void validateNoCicle(StructSchema structSchema, Set<StructSchema> structSchemas, Set<String> visitedStructs) {
		Set<String> visited = (visitedStructs == null) ? new LinkedHashSet<>() : visitedStructs;
		
		visited.add(structSchema.getName());
		
		structSchema.getFields().forEach(f -> {
			if(f.getType() instanceof StructFieldType structType) {
				StructSchema subStruct = structSchemas.stream()
						.filter(s -> s.getName().equals(structType.getStructName()))
						.findFirst()
						.orElse(null);
				
				if(subStruct == null) return;
				
				if(visited.contains(subStruct.getName())) {
					String first = visited.stream().findFirst().get();
					throw new SchemaValidationException("Cicle reference of structs in begin " + first + " and end " + subStruct.getName());
				}else {
					validateNoCicle(subStruct, structSchemas, visited);
				}
			}
		});
	}

}
