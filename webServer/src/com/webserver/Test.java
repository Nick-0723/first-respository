package com.webserver;

import java.io.File;
import java.io.FileOutputStream;

public class Test {
	private Request res;

	public Test(Request res) {
		this.res = res;
	}

	public void exe() {
		try {
			String rootpath = "src/com/webserver/";
			String filename = res.getParameter().get("uploadfile");
			byte[] b = res.getFile().get(filename);
			System.out.println(filename.contains(File.separator));
			if(filename.contains(File.separator)){
				String[] split = filename.split("\\\\");
				filename = split[split.length-1];
			}
			System.out.println(filename);
			FileOutputStream fos = new FileOutputStream(rootpath+filename);
			fos.write(b);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
