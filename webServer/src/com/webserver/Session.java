package com.webserver;

import java.util.HashMap;
import java.util.Map;

public final class Session {
	private Map<String, String> sessionMap;
	
	Session() {
		setSessionMap(new HashMap<>());
	}
	
	public void put(String key, String value){
		sessionMap.put(key, value);
	}
	
	public Map<String, String> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(Map<String, String> sessionMap) {
		this.sessionMap = sessionMap;
	}
}
