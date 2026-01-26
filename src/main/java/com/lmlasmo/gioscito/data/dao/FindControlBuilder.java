package com.lmlasmo.gioscito.data.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FindControlBuilder {	
	
	private Collection<String> fields;
	private Pageable pageable;
	private Where where;
	
	public static FindControlBuilder newInstance() {
		return new FindControlBuilder(null, null, null);
	}
	
	public FindControlBuilder includeFields(String... fields) {		
		return new FindControlBuilder(List.of(fields), pageable, where);
	}
	
	public FindControlBuilder withPageable(Pageable pageable) {
		return new FindControlBuilder(fields, pageable, where);
	}
	
	public FindControlBuilder withWhere(Where where) {
		return new FindControlBuilder(fields, pageable, where);
	}
	
	public FindControl build() {
		return new FindControlImpl(where, pageable, fields);
	}
	
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	private class FindControlImpl implements FindControl {
		
		private Where where;
		private Pageable pageable;
		private Collection<String> fields;
		
	}
	
}