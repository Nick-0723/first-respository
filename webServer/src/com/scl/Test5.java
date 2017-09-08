package com.scl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("all")
public class Test5 {

	public static void main(String[] args) throws IOException {
		System.out.println("start---");
		ServerSocket ss=new ServerSocket(8080);
		int n=0;
		while(true){
			new Handle(ss.accept()).start();
			System.out.println("请求"+(++n)+"次");
			ResolveRequest r = new ResolveRequest();
		}
	}
}
@SuppressWarnings("unused")
class Handle extends Thread{
	private String rootpath="src/com/scl/";
	private InputStream is;
	private OutputStream os;
	private Socket socket;
	public Handle(Socket s){
		try {
			is = s.getInputStream();
			os = s.getOutputStream();
			socket=s;
			System.out.println("----");
		} catch (IOException e) {
		}
	}
	@Override
	public void run() {
		byte[] buf = new byte[10000000];
		try {
			FileOutputStream fos = 
					new FileOutputStream(rootpath+"请求报文.txt",true);
//			fos.write("\r\n\r\n---------分割线---------\r\n".getBytes());
//			fos.flush();
			int len=is.read(buf);
			fos.write(buf,0,len);
			fos.flush();
			while((len=is.available())>0){
				is.read(buf, 0, len);
				fos.write(buf,0,len);
				fos.flush();
			}
			
			os.write("HTTP/1.1 200 OK\r\n".getBytes());
			os.flush();
			os.write("Date: Mon, 07 Jan 2013 14:31:36 GMT\r\n".getBytes());
			os.flush();
			os.write("Server: BWS/1.0\r\n".getBytes());
			os.flush();
			os.write("\r\n\r\n".getBytes());
			os.flush();
			FileInputStream fis = 
					new FileInputStream(rootpath+"index.html");
			byte[] b = new byte[1024];
			int n=-1;
			while((n=fis.read(b))>=0){
				os.write(b, 0, n);
				os.flush();
			}
			
			fos.close();
			fis.close();
			os.close();
			is.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}