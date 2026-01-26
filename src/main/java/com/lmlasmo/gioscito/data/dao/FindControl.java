package com.lmlasmo.gioscito.data.dao;

import java.util.Collection;

import org.springframework.data.domain.Pageable;

public interface FindControl {
	
	public Where getWhere();	
	public Pageable getPageable();
	public Collection<String> getFields();	
	
	public static FindControlBuilder builder() {
		return FindControlBuilder.newInstance();
	}
	
}
