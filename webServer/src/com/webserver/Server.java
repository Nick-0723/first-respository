package com.webserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings("unused")
public class Server {
	private static ServerSocket server;
	private static Socket socket;
	private static int port = 8080;
	
	private static void startServer(){
		try {
			server = new ServerSocket(port);
			while(true){
				socket = server.accept();
				ServerThread t = new ServerThread(socket);
				t.setDaemon(true);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		startServer();
	}
	
}

class ServerThread extends Thread{
	private Socket socket;
	private Reader in;
	private OutputStream os;
	
	
	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	
	@Override
	public void run() {
		try {
			in = new Reader(socket.getInputStream());
			os = socket.getOutputStream();
			new Response(new Request(in), socket);
			
			//测试代码块
//			{
//				os.write("HTTP/1.1 200 OK\r\n".getBytes());
//				os.flush();
//				os.write("Date: Mon, 07 Jan 2013 14:31:36 GMT\r\n".getBytes());
//				os.flush();
//				os.write("Server: BWS/1.0\r\n".getBytes());
//				os.flush();
//				os.write("\r\n\r\n".getBytes());
//				os.flush();
//				FileInputStream fis = 
//						new FileInputStream("src/com/webserver/index.html");
//				byte[] b = new byte[1024];
//				int n=-1;
//				while((n=fis.read(b))>=0){
//					os.write(b, 0, n);
//					os.flush();
//				}
//				
//				Test t = new Test(req);
//				t.exe();
//				System.out.println(req.getMethod());
//				System.out.println(req.getResource());
//				System.out.println(req.getVersion());
//				
//				
//				Map<String, String> header = req.getHeader();
//				Set<Entry<String, String>> entrySet = header.entrySet();
//				for (Entry<String, String> entry : entrySet) {
//					System.out.println(entry.getKey()+": "+entry.getValue());
//				}
//				
//				System.out.println("-------------参数----------");
//				Map<String, String> para = req.getParameter();
//				Set<Entry<String, String>> entrySet1 = para.entrySet();
//				for (Entry<String, String> entry : entrySet1) {
//					System.out.println(entry.getKey()+": "+entry.getValue());
//				}
//				
//				
//				System.out.println("-------------文件----------");
//				Map<String, byte[]> file = req.getFile();
//				Set<Entry<String, byte[]>> entrySet2 = file.entrySet();
//				for (Entry<String, byte[]> entry : entrySet2) {
//					System.out.println(entry.getKey()+": "+entry.getValue());
//				}
//				fis.close();
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null)in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(os != null)os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}