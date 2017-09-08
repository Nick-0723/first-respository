package com.testWebServer;

import java.util.Set;
import java.util.Map.Entry;

import com.webserver.Request;
import com.webserver.Response;
import com.webserver.Servlet;

public class ShopServlet implements Servlet{

	@Override
	public void service(Request req, Response res) throws RuntimeException {
		
		String str = req.getHeader().get("Cookie");
		int indexOf = str.indexOf("SID=");
		String UID = str.substring(indexOf+4, indexOf+24);
		
		System.out.println(UID);
		
		Set<Entry<String, String>> entrySet = res.getSmap().get(UID).getSessionMap().entrySet();
		System.out.println("------");
		for (Entry<String, String> entry : entrySet) {
			System.out.println(entry.getKey()+": "+entry.getValue());
		}
		
		String name = res.getSmap().get(UID).getSessionMap().get("name");
		String lastGoodsName = res.getSmap().get(UID).getSessionMap().get("goodsName");
		System.out.println(lastGoodsName+"........");
		
		String str1 = "<html>"
				+ "<head>"
				+ "<meta charset=\"UTF-8\">"
				+ "<title>Shop</title>"
				+ "</head>"
				+ "<body>"
				+ "<center><h1>welcome "+name+" !</h1>"
				+ "<br>"
				+ "apple------<a href=\"submit1.action?goodsName=apple\">加入购物车</a><br>"
				+ "banner-----<a href=\"submit1.action?goodsName=banner\">加入购物车</a><br>"
				+ "mango------<a href=\"submit1.action?goodsName=mango\">加入购物车</a><br>"
				+ "pear-------<a href=\"submit1.action?goodsName=pear\">加入购物车</a><br>"
				+ "<br>"
				+ "<a href=\"index.html\">返回</a>"
				+ "</center>"
				+ "</body>"
				+ "</html>";
		
		res.setCookie("username="+name);
		res.writebyte(str1.getBytes());
	}

}
