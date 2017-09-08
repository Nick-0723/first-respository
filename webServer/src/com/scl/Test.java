package com.scl;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Test {
	public static void main(String[] args) {
		new Test().test2();
	}
	
	public void test2(){
		FileInputStream fis;
		try {
			fis = new FileInputStream("src/com/scl/a.txt");
			Reader in = new Reader(fis);
			String str = "";
			while(!(str = in.readLine()).equals("")){
				System.out.println(str);
			}
			System.out.println(in.read());
			in.close();
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void test1(){
		try {
			FileOutputStream out = new FileOutputStream("src/com/scl/a.txt");
			byte[] b = new byte[]{14,93,44,10,13,13,66,13,10,13,10,96,95,95,95,95,95};
			out.write(b);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
