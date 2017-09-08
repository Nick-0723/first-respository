package com.scl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Reader {
	private InputStream in;

	public Reader(InputStream in) {
		this.in = in;
	}
	
	public int read() throws IOException{
		return in.read();
	}
	
	public int read(byte[] b) throws IOException{
		return in.read(b);
	}
	
	public int available() throws IOException{
		return in.available();
	}
	
	public String readLine() throws IOException{
		List<Integer> list = new ArrayList<>();
		int data = -1;
		int next = -1;
		
		while ((data = in.read()) != -1) {
			if (data == '\r') {
				if ((next = in.read()) == '\t') {
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
		return new String(by);
	}
	
	public void close() throws IOException{
		in.close();
	}
	
}
