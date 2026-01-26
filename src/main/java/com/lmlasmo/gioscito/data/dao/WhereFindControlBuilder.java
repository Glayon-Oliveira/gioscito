package com.lmlasmo.gioscito.data.dao;

import com.lmlasmo.gioscito.data.dao.Where.WhereType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;		

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WhereFindControlBuilder {
	
	@NonNull private String field;
	@NonNull private Object value;
	@NonNull private WhereType type;
	
	private Where where;
	
	private WhereFindControlBuilder(Where where) {
		this.where = where;
	}
	
	public static WhereFindControlBuilder newInstance(String field) {
		return newInstance(field, WhereType.EQUALS);
	}
	
	public static WhereFindControlBuilder newInstance(String field, Object value) {
		return newInstance(field, value, WhereType.EQUALS);
	}
	
	public static WhereFindControlBuilder newInstance(String field, WhereType whereType) {
		if(whereType == null) throw new IllegalArgumentException("Where type cannot be null");
		
		return new WhereFindControlBuilder(field, null, whereType != null ? whereType : WhereType.EQUALS);
	}
	
	public static WhereFindControlBuilder newInstance(String field, Object value, WhereType whereType) {
		if(whereType == null) throw new IllegalArgumentException("Where type cannot be null");
		
		return new WhereFindControlBuilder(field, null, whereType != null ? whereType : WhereType.EQUALS);
	}
	
	public WhereFindControlBuilder withField(String field) {
		return new WhereFindControlBuilder(field, value, type);
	}
	
	public WhereFindControlBuilder withValue(Object value) {
		return new WhereFindControlBuilder(field, value, type);
	}
	
	public WhereFindControlBuilder withType(WhereType whereType) {
		if(whereType == null) throw new IllegalArgumentException("Where type cannot be null");
		
		return new WhereFindControlBuilder(field, value, type);
	}
	
	public WhereFindControlBuilder withWhere(String field, Object value, WhereType type) {
		return new WhereFindControlBuilder(new WhereImpl(field, value, type, null, null));
	}
	
	public WhereFindControlBuilder withWhere(Where where) {
		return new WhereFindControlBuilder(where);
	}
	
	public WhereFindControlBuilder andWhere(String field, Object value, WhereType type) {
		Where andWhere = new WhereImpl(field, value, type, null, null);
		Where where = new WhereImpl(this.where.getField(), this.where.getValue(), this.where.getType(), andWhere, this.where.or());
		
		return new WhereFindControlBuilder(where);
	}
	
	public WhereFindControlBuilder andWhere(Where where) {
		return new WhereFindControlBuilder(
				new WhereImpl(
						this.where.getField(),
						this.where.getValue(),
						this.where.getType(),
						where,
						this.where.or())
				); 
	}
	
	public WhereFindControlBuilder orWhere(String field, Object value, WhereType type) {
		Where orWhere = new WhereImpl(field, value, type, null, null);
		Where where = new WhereImpl(this.where.getField(), this.where.getValue(), this.where.getType(), this.where.or(), orWhere);
		
		return new WhereFindControlBuilder(where);
	}
	
	public WhereFindControlBuilder orWhere(Where where) {
		return new WhereFindControlBuilder(
				new WhereImpl(
						this.where.getField(),
						this.where.getValue(),
						this.where.getType(),
						this.where.and(),
						where)
				); 
	}

	public Where build() {
		return this.where;
	}
	
	@AllArgsConstructor
	private class WhereImpl implements Where {
		
		@Getter private String field;
		@Getter private Object value;
		@Getter private WhereType type;
		
		private Where and;
		private Where or;
		
		@Override
		public Where and() {
			return this.and;
		}
		
		@Override
		public Where or() {
			return this.or;
		}
	}
	
}
