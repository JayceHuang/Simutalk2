package cn.ac.ia.directtrans.json;

import java.io.UnsupportedEncodingException;

public class JsonByteArray extends Json{

	public byte[] byteArray = null;

	public static String toString( byte[] bytes){
		
		try {
			String str = new String(bytes);
			return java.net.URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public byte[] toByteArray() {
		// TODO Auto-generated method stub
		return byteArray;
	}

	public void setData(byte[] bytes) {
		// TODO Auto-generated method stub
		byteArray = bytes;
	}

	public String getJson() {
		// TODO Auto-generated method stub
		return new String(byteArray);
	}
	
}
