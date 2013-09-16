package cn.ac.ia.directtrans.json;


import com.google.gson.Gson;


public class JsonRequest extends JsonFunction {
	
	public long handkey = 0;//验证消息是否重复发送
	public static JsonRequest fromJson(String json) {

		Gson gson = new Gson();
		JsonRequest user = gson.fromJson(json,JsonRequest.class);
		
		return user;
	}
	
	
}
