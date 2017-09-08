package com.webserver;

import java.io.FileInputStream;
import java.util.Properties;

public class Configue {
	private static Properties properties;
	
	static { //使用静态代码块初始化Properties
		properties = new Properties();
		try {
			properties.load(new FileInputStream("webroot/conf/conf.properties"));
		} catch (Exception e) {
			System.out.println("BeanFactory初始化Properties出错" + e);
			System.exit(-1);
		}
	}
	
	public static int getPort(){
		return Integer.parseInt(properties.getProperty("port", "8080"));
	}
	
	public static String getRootPath(){
		return properties.getProperty("RootPath", "webroot/webproject");
	}
	
	public static String getErrorHtml(){
		return properties.getProperty("404Error","webroot/resource/defaultHtml/notfound.html");
	}
	
	public static String getIndexHtml(){
		return properties.getProperty("index","webroot/resource/defaultHtml/index.html");
	}
	
	
	public static String getProperty(String key, String defaultValue){
		return properties.getProperty(key, defaultValue);
	}
	
}
