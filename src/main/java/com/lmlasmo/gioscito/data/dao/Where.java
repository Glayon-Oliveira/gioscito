package com.lmlasmo.gioscito.data.dao;

public interface Where {
	public String getField();
	public Object getValue();
	public WhereType getType();
	
	public default Where and() {return null;}
	public default Where or() {return null;}
	
	public static WhereFindControlBuilder builder(String field) {
		return WhereFindControlBuilder.newInstance(field);
	}
	
	public static enum WhereType {
		INCLUDES,
		EQUALS
	}
}


