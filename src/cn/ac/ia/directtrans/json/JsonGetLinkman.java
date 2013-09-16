package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonGetLinkman extends JsonRequest {
	
	public long linkmanId = 0;
	
	public JsonGetLinkman(){
		function = GET_LINKMAN;
	}
	
	public static JsonGetLinkman fromJson(String json) {

		Gson gson = new Gson();
		JsonGetLinkman user = gson.fromJson(json,JsonGetLinkman.class);
		
		return user;
	}
}
