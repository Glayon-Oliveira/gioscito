package com.lmlasmo.gioscito.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentProperties {

	public static Map<String, Map<String, Object>> successCreater() {
		Map<String, Object> reference = new HashMap<>();
	    reference.put("name", "Albert");

	    Map<String, Object> article = new HashMap<>();
	    article.put("title", "Hello World");
	    article.put("category", "News");
	    article.put("views", 10);
	    article.put("likes", 1000);
	    article.put("references", List.of(reference));

	    Map<String, Map<String, Object>> root = new HashMap<>();
	    root.put("article", article);

	    return root;
	}
	
	public static Map<String, Map<String, Object>> successUpdate() {
		Map<String, Map<String, Object>> root = successCreater();
		root.get("article").put("title", "New world");
		
		return root;
	}
	
	public static Map<String, Map<String, Object>> failCreater() {
		Map<String, Object> invalidReference = new HashMap<>();
	    invalidReference.put("name", null);

	    Map<String, Object> article = new HashMap<>();
	    article.put("title", null);
	    article.put("category", "InvalidEnum");
	    article.put("views", -15);
	    article.put("likes", Long.MAX_VALUE);
	    article.put("references", List.of(invalidReference));

	    Map<String, Map<String, Object>> root = new HashMap<>();
	    root.put("article", article);

	    return root;
	}
	
	public static Map<String, Map<String, Object>> failUpdate() {
		Map<String, Map<String, Object>> root = successCreater();
		root.get("article").put("title", 1);
		
		return root;
	}
	
}
