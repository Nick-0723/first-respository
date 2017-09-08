package com.scl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ResolveRequest {
	private static String requestMethod;
	private static String requestURL;
	private static String HTTP_Version;
	private static Map<String, String> requestHeaderMap = new TreeMap<>((s1,s2)->s1.compareTo(s2));
	private static Map<String, String> contentMap = new TreeMap<>((s1,s2)->s1.compareTo(s2));
	private static Map<String, TreeMap<String,byte[]>> bodyMap = new TreeMap<>((s1,s2)->s1.compareTo(s2));
	private static String boundary;
	
	private boolean check(Predicate<Integer> i,int a){
		return i.test(a);
	}
	
	public void resolve(File f1){
		Reader in = null;
		try {
			in = new Reader(new FileInputStream(f1));
			String strLine = in.readLine();
			String[] split1 = strLine.split("[ ]");
			if(check(a->a!=3,split1.length))throw new Exception("格式错误，不能解析");
			requestMethod = split1[0];
			String[] spUrl = split1[1].split("[?]");
			if(check(a->a>2,spUrl.length))throw new Exception("格式错误，不能解析");
			else if(check(a->a==2,spUrl.length)){
				String[] spCont = spUrl[1].split("[&]");
				for (String s : spCont) {
					String[] spKV = s.split("[=]");
					contentMap.put(spKV[0], spKV[1]);
				}
			}
			requestURL = spUrl[0];
			HTTP_Version = split1[2];
			
			String strHead = "";
			while((strHead = in.readLine()) != null && !(strHead).equals("")){
				String[] split2 = strHead.split("[:][ ]");
				if(check(a->a!=2,split2.length))throw new Exception("格式错误，不能解析");
				if(split2[0].equals("Content-Type")){
					String[] split3 = split2[1].split("[;][ ]");
					if(check(a->a>2,split3.length))throw new Exception("格式错误，不能解析");
					if(split3.length==2){
						String[] split4 = split3[1].split("[=]");
						if(check(a->a!=2,split4.length))throw new Exception("格式错误，不能解析");
						boundary = split4[1];
						requestHeaderMap.put(split2[0], split3[0]);
					}else {
						requestHeaderMap.put(split2[0], split2[1]);
					}
				}else {
					requestHeaderMap.put(split2[0], split2[1]);
				}
			}
			
			
			
			if(requestMethod.equals("POST")){
				if(boundary==null){
					String con = in.readLine();
					String[] spCont = con.split("[&]");
					for (String s : spCont) {
						String[] spKV = s.split("[=]");
						contentMap.put(spKV[0], spKV[1]);
					}
				}else{
					String filename = "";
					String name = "";
					String s = "";
					while (!(s = in.readLine()).equals(boundary+"--")) {
						String str = "";
						while (!(str = in.readLine()).equals("")) {
							if (str.equals(boundary))continue;
							String[] split = str.split("[;][ ]");
							if (split.length>1) {
								String[] ss = split[1].split("\"");
								name = ss[1];
								filename = "";
								if (split.length == 3) {
									String[] sss = split[2].split("\"");
									filename = sss[1];
								} 
							}
						}
							
						List<Integer> list = new ArrayList<>();
						int data = -1;
						int next = -1;
						while ((data = in.read()) != -1) {
							if (data == '-') {
								if ((next = in.read()) == '-') {
									break;
								}
								list.add(data);
								if (next == -1)
									break;
								list.add(next);
							} else {
								list.add(data);
							}
						}
						byte[] by = new byte[list.size()-2];
						for(int i = 0; i < by.length; i++){
							int temp = list.get(i);
							by[i] = (byte) temp;
						}
						TreeMap<String,byte[]> fileMap = new TreeMap<String, byte[]>();
						fileMap.put(filename, by);
						bodyMap.put(name, fileMap);
					}
					
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null)in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	public static void main(String[] args) {
		String rootPath = "src/com/scl/";
		File f1 = new File(rootPath+"请求报文.txt");
		new ResolveRequest().resolve(f1);
		System.out.println(requestMethod);
		System.out.println(requestURL);
		System.out.println(HTTP_Version);
		System.out.println(boundary);
//		System.out.println(requestHederMap);
		Set<Entry<String, String>> entrySet = requestHeaderMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			System.out.println(entry.getKey()+": "+entry.getValue());
		}
		Set<Entry<String, String>> entrySet1 = contentMap.entrySet();
		for (Entry<String, String> entry : entrySet1) {
			System.out.println("-------");
			System.out.println(entry.getKey()+": "+entry.getValue());
		}
		Set<Entry<String, TreeMap<String, byte[]>>> entrySet2 = bodyMap.entrySet();
		for (Entry<String, TreeMap<String, byte[]>> entry : entrySet2) {
			System.out.println(entry.getKey()+": "+entry.getValue());
		}
		System.out.println(Arrays.toString(bodyMap.get("name").get("")));
		System.out.println(Arrays.toString(bodyMap.get("passwd").get("")));
		test();
	}
	
	public static void test(){
		
		
		byte[] buf = bodyMap.get("uploadfile").get("umlåç».xlsx");
		System.out.println(buf.length);
		try {
			OutputStream out = new FileOutputStream("src/com/scl/request/a.xlsx");
			out.write(buf);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
