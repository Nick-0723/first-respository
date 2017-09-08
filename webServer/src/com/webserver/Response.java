package com.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class Response {
	private String resVersion;//响应协议版本号
	private int state;//响应状态码
	private String msg;//响应消息
	private Map<String,String> resHead;
	private String resource;
	private OutputStream out;
	
	private static volatile boolean flag;
	private String SID;
	private Session session;
	private static Map<String,Session> smap;
	
	
	private Request req;
	private String rootpath = Configue.getRootPath();
	
	public Response(Request req,Socket socket) {
		this.req = req;
		resHead = new HashMap<>();
		resource = req.getResource();
		try {
			out = socket.getOutputStream();
			initDefultResponce();
			mainHandle();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Map<String,Session> getSmap(){
		return smap;
	}
	
	public void setCookie(String cookies){
		resHead.put("Set-Cookie", cookies);
	}
	
	public void addResHead(String k, String v){
		resHead.put(k, v);
	}
	
	public void forwrod(String url){
		if(url==null||url=="/")showIndexHtml();
		else{
			write(new File(rootpath, url),out);
		}
	}
	
	public void setReource(String str){
		this.resource = str;
	}
	
	public synchronized void  mark(){
		if(smap==null)smap = new HashMap<>();
		if(!flag&&(!req.getHeader().containsKey("Cookie")||!(req.getHeader().get("Cookie").contains("SID=")))){
			flag = true;
			SID = UUID.randomUUID().getLeastSignificantBits()+"";
			addResHead("Set-Cookie", "SID="+SID);
			System.out.println("---------");
		}else {
			String str = req.getHeader().get("Cookie");
			if(str==null)return;
			int indexOf = str.indexOf("SID=");
			SID = str.substring(indexOf+4, indexOf+24);
			
			if(smap.isEmpty() || !smap.containsKey(SID)){
				session = new Session();
				Set<Entry<String, String>> entrySet = req.getParameter().entrySet();
				for (Entry<String, String> entry : entrySet) {
					session.put(entry.getKey(), entry.getValue());
				}
				smap.put(SID, session);
			}else{
				Set<Entry<String, String>> entrySet = req.getParameter().entrySet();
				for (Entry<String, String> entry : entrySet) {
					smap.get(SID).getSessionMap().put(entry.getKey(), entry.getValue());
				}
			}
			flag = false;
		}
	}
	
	public void mainHandle(){
		//这里处理标识浏览器
		mark();
		
//		System.out.println(resource+"1122");//-----------------
		if(resource==null)return;
		if(resource.equals("/")){
			showIndexHtml();//如果请求的资源为"/"则显示主界面
			return;
		}
		if(resource.contains(".action")){
			Servlet servlet = (Servlet) Factory.getObject(resource.split("[.]")[0]);
//			System.out.println(resource.split("[.]")[0]);//------------
//			System.out.println(servlet==null);//------------
			if(servlet==null)showErrorHtml();
			else servlet.service(req, this);
			return;
		}
//		System.out.println(resource);//------------
		File file;
		try {
			file = new File(rootpath,resource);
			if(file.exists()) write(file,out);
			else showErrorHtml();
		} catch (Exception e) {
			showErrorHtml();
			return;
		}
	}
	
	public void write(File file,OutputStream out){
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			out.write((resVersion+" "+state+" "+msg+"\r\n").getBytes());
			Set<Entry<String, String>> entrySet = resHead.entrySet();
			for (Entry<String, String> entry : entrySet) {
				out.write((entry.getKey()+": "+entry.getValue()+"\r\n").getBytes());
			}
			
			out.write("\r\n".getBytes());
			
			int len = -1;
			byte[] b = new byte[1024*100];
			while((len = in.read(b))!=-1){
				out.write(b, 0, len);
			}
			out.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void writebyte(byte[] b){
		try {
			out.write((resVersion+" "+state+" "+msg+"\r\n").getBytes());
			Set<Entry<String, String>> entrySet = resHead.entrySet();
			for (Entry<String, String> entry : entrySet) {
				out.write((entry.getKey()+": "+entry.getValue()+"\r\n").getBytes());
			}
			out.write("\r\n".getBytes());
			
			out.write(b);
			out.flush();
			
		} catch (Exception e) {
			showErrorHtml();
		} finally {
			try {
				if(out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showIndexHtml(){
		write(new File(Configue.getIndexHtml()),out);
	}
	private void showErrorHtml(){
		state = 404;
		msg = "Not Found";
		write(new File(Configue.getErrorHtml()),out);
	}
	
	public void initDefultResponce(){
		resVersion = "HTTP/1.1";
		state = 200;
		msg = "OK";
		resHead.put("Date", (new Date()).toString());
		resHead.put("Server", "NICK/1.0");
		
	}
	
}
