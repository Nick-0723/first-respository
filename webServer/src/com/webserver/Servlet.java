package com.webserver;

public interface Servlet {
	public void service(Request req,Response res)throws RuntimeException;
}
