package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonUserLogin extends JsonRequest {
	
	public String 	 imei;
	public String	 name;
	public String 	 info;
	
	public JsonUserLogin(){
		function = LOGIN;
	}
	
	public static JsonUserLogin fromJson(String json) {

		Gson gson = new Gson();
		JsonUserLogin user = gson.fromJson(json,JsonUserLogin.class);
		
		return user;
	}
}
