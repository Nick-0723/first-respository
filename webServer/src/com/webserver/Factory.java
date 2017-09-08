package com.webserver;

import java.io.FileInputStream;
import java.util.Properties;

public class Factory {
	private static  Properties properties;
	static { //使用静态代码块初始化Properties
		properties = new Properties();
		try {
			properties.load(new FileInputStream("webroot/conf/servlet.properties"));
		} catch (Exception e) {
			System.out.println("BeanFactory初始化Properties出错" + e);
			System.exit(-1);
		}
	}
	
	public static Object getObject(String name) {
		//对name属性进行判空处理
		if (name == null || name.equals("")) {
			return null;
		}
		//获取properties文件中类的全类名
//		String newName = name.split("[=]")[0];
		String classString = properties.getProperty(name);
		if (classString != null) {
			try {
				Object object = Class.forName(classString).newInstance();
				return object;
			} catch (Exception e) {
				System.out.println("通过反射去获取类的实例出错" + e);
				return null;
			}
		}
		return null;
	}
}
