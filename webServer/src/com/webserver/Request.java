package com.webserver;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Request {
	private Reader in;
	private String method;//请求方法
	private String resource;//请求资源
	private String version;//协议版本号
	private Map<String, String> header;//请求头
	private Map<String, String>parameter;//请求参数
	private Map<String, byte[]>file;//文件
	
	public String getMethod() {
		return method;
	}
	public String getResource() {
		return resource;
	}
	public String getVersion() {
		return version;
	}
	public Map<String, String> getHeader() {
		return header;
	}
	public Map<String, String> getParameter() {
		return parameter;
	}
	public Map<String, byte[]> getFile() {
		return file;
	}

	public Request(Reader in) {
		this.in = in;
		header = new HashMap<>();
		parameter = new HashMap<>();
		file = new HashMap<>();
		resolver();
	}
	
	public void resolver(){
		//解析请求行
		try {
			String requestLine = in.readLine();
			String[] split = requestLine.split(" ");
			method = split[0];
			version = split[2];
			String[] split1 = split[1].split("[?]");
			resource = split1[0];
			if(split1.length==2){
				String[] para = split1[1].split("[&]");
				for (String s : para) {
					String[] spKV = s.split("[=]");
					if(spKV.length<2)parameter.put(spKV[0], null);
					else parameter.put(spKV[0], URLDecoder.decode(spKV[1], "UTF-8"));
				}
			}
			//解析请求头
			parseHeader();
			//解析请求体
			if("GET".equals(method)&&in.available()>0)in.readLine();
			else if("POST".equals(method))postParse();
			
		} catch (Exception e) {
		}
	}
	
	public void parseHeader(){
		try {
			String strHead = "";
			while((strHead = in.readLine()) != null && !(strHead).equals("")){
				String[] split2 = strHead.split("[:][ ]");
				header.put(split2[0], split2[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void postParse(){
		String boundary = getBoundary();
		try {
			if(boundary==null){
				String con = in.readLine();
				String[] spCont = con.split("[&]");
				for (String s : spCont) {
					String[] spKV = s.split("[=]");
					if(spKV.length<2)parameter.put(spKV[0], null);
					else parameter.put(spKV[0], URLDecoder.decode(spKV[1], "UTF-8"));
				}
			}else{
				while ((in.available()>0)) {
					while (true) {
						
						String line1 = in.readLine();
						
						if(line1.equals("--"+getBoundary()))continue;//跳过分隔符
						
						if((line1 ).contains("filename=")){//含有文件的参数解析
							String[] split = line1.split("[;][ ]");
							String[] name = split[1].split("\"");
							String[] filename = split[2].split("\"");
							if(filename.length<2){
								parameter.put(name[1], null);
								readData();
								file.put(null, null);
							}
							else {
								parameter.put(name[1], filename[1]);
								in.readLine();in.readLine();
								file.put(filename[1], readData());
							}
							break;
						}else{//参数解析
							String[] split = line1.split("\"");
							in.readLine();
							byte[] value = readData();
							String pv = null;
							if(value!=null){pv = new String(value,"UTF-8");}
							else pv = null;
							parameter.put(split[1], pv);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private byte[] readData(){
		byte[] by = null;
		try {
			List<Integer> list = new ArrayList<>();
			int data = -1;
			int next = -1;
a:			while(true){
				List<Integer> part = new ArrayList<>();
				while (true) {
					data = in.read();
					if (data == '\r') {
						if ((next = in.read()) == '\n') break;
						part.add(data);
						if (next == -1) break;
						part.add(next);
					} else {
						part.add(data);
					}
				}
				if(part.size()==0)part.add(0);
				by = new byte[part.size()];
				for (int i = 0; i < by.length; i++) {
					int temp = part.get(i);
					by[i] = (byte) temp;
				}
				String s = new String(by);
				if(s.equals("--"+getBoundary())||s.equals("--"+getBoundary()+"--")) break a;
				part.add(13);
				part.add(10);
				list.addAll(part);
				
			}
			
			if (list.size()>2) {
				by = new byte[list.size()-2];
				for (int i = 0; i < by.length; i++) {
					int temp = list.get(i);
					by[i] = (byte) temp;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return by;
	}
	
	//获取分隔符
	private String getBoundary(){
		String content_type = header.get("Content-Type");
		String[] split = content_type.split("[;][ ]");
		if(split.length==2){
			String[] split4 = split[1].split("[=]");
			return split4[1];
		}else return null;
	}
}


