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
	private final Object value;
	@NonNull private WhereType type;
	
	private final Where and;
	private final Where or;
	
	private WhereFindControlBuilder(Where where) {
		this(
			where.getField(),
			where.getValue(),
			where.getType(),
			where.and(),
			where.or()
			);
	}
	
	public static WhereFindControlBuilder newInstance(String field) {
		return newInstance(field, WhereType.EQUALS);
	}
	
	public static WhereFindControlBuilder newInstance(String field, Object value) {
		return newInstance(field, value, WhereType.EQUALS);
	}
	
	public static WhereFindControlBuilder newInstance(String field, WhereType whereType) {
		return new WhereFindControlBuilder(
				field, null,
				whereType != null ? whereType : WhereType.EQUALS,
				null, null
				);
	}
	
	public static WhereFindControlBuilder newInstance(String field, Object value, WhereType whereType) {
		return new WhereFindControlBuilder(
				field, value,
				whereType != null ? whereType : WhereType.EQUALS,
				null, null
				);
	}
	
	public WhereFindControlBuilder withField(String field) {
		return new WhereFindControlBuilder(field, value, type, and, or);
	}
	
	public WhereFindControlBuilder withValue(Object value) {
		return new WhereFindControlBuilder(field, value, type, and, or);
	}
	
	public WhereFindControlBuilder withType(WhereType whereType) {
		return new WhereFindControlBuilder(
				field, value,
				whereType != null ? whereType : WhereType.EQUALS,
				and, or);
	}
	
	public WhereFindControlBuilder withWhere(String field, Object value, WhereType type) {
		return new WhereFindControlBuilder(new WhereImpl(field, value, type, and, or));
	}
	
	public WhereFindControlBuilder withWhere(Where where) {
		return new WhereFindControlBuilder(where);
	}
	
	public WhereFindControlBuilder andWhere(String field, Object value, WhereType type) {
		Where andWhere = new WhereImpl(field, value, type, null, null);
		Where where = new WhereImpl(this.field, this.value, this.type, andWhere, this.or);
		
		return new WhereFindControlBuilder(where);
	}
	
	public WhereFindControlBuilder andWhere(Where where) {
		return new WhereFindControlBuilder(
				new WhereImpl(
						this.field,
						this.value,
						this.type,
						where,
						this.or
						)
				);
	}
	
	public WhereFindControlBuilder orWhere(String field, Object value, WhereType type) {
		Where orWhere = new WhereImpl(field, value, type, null, null);
		Where where = new WhereImpl(this.field, this.value, this.type, this.and, orWhere);
		
		return new WhereFindControlBuilder(where);
	}
	
	public WhereFindControlBuilder orWhere(Where where) {
		return new WhereFindControlBuilder(
				new WhereImpl(
						this.field,
						this.value,
						this.type,
						this.and,
						where)
				); 
	}

	public Where build() {
		return new WhereImpl(field, value, type, and, or);
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
