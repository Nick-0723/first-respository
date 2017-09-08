package com.testWebServer;

import java.util.Map.Entry;
import java.util.Set;

//import java.util.UUID;

import com.webserver.*;

public class submitServlet implements Servlet {
	
	private Request req;
	private Response res;

	@Override
	public void service(Request req, Response res) throws RuntimeException {
		this.req = req;
		this.res = res;
		check();
	}
	
	private void check(){
		
		String str = req.getHeader().get("Cookie");
		int indexOf = str.indexOf("SID=");
		String UID = str.substring(indexOf+4, indexOf+24);
		
		System.out.println(UID);
		
		Set<Entry<String, String>> entrySet = res.getSmap().get(UID).getSessionMap().entrySet();
		System.out.println("------");
		for (Entry<String, String> entry : entrySet) {
			System.out.println(entry.getKey()+": "+entry.getValue());
		}
		
		
		String name = req.getParameter().get("name");
		String passwd = req.getParameter().get("passwd");
//		System.out.println(name+": "+passwd);//------------------------
		if(name==null||passwd==null){
			res.forwrod("/test1/fail.html");
		}else if(!(name.equals("tom"))||!(passwd.equals("123"))){
			res.forwrod("/test1/fail.html");
		}
		else {
			
//			if(!req.getHeader().containsKey("Cookie")){
//				
//				res.addResHead("Set-Cookie", UUID.randomUUID().getLeastSignificantBits()+"");
//			}
			
			res.forwrod("/test1/success.html");
		}
	}
}


