package com.testWebServer;

import java.util.Set;
import java.util.Map.Entry;

import com.webserver.Request;
import com.webserver.Response;
import com.webserver.Servlet;

public class CartServlet implements Servlet{

	@Override
	public void service(Request req, Response res) throws RuntimeException {
		String value = req.getParameter().get("goodsName");
		res.setCookie("goodsName="+value);
//		res.forwrod("/test1/index.html");
		
		
		String str = req.getHeader().get("Cookie");
		int indexOf = str.indexOf("SID=");
		String UID = str.substring(indexOf+4, indexOf+24);
		
		System.out.println(UID);
		
		Set<Entry<String, String>> entrySet = res.getSmap().get(UID).getSessionMap().entrySet();
		System.out.println("------");
		for (Entry<String, String> entry : entrySet) {
			System.out.println(entry.getKey()+": "+entry.getValue());
		}
		
		
		String username = req.getParameter().get("Cookie");
		
		
		String str1 = "<html>"
				+ "<head>"
				+ "<meta charset=\"UTF-8\">"
				+ "<title>购物车</title>"
				+ "</head>"
				+ "<body>"
				+ "<center>"
				+ "<h1>welcome "+username+" !</h1>"
				+ "<br><br>"
				+ "<a href=\"index.html\">返回</a>"
				+ "</center></body></html>";
		
		res.writebyte(str1.getBytes());
		
		
	}

}
