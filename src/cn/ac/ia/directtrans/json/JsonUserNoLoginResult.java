package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonUserNoLoginResult extends JsonRequestResult{

	public static final String EXPLAIN_NO_LOGIN 	= "ÇëµÇÂ½¡£";

	
	public JsonUserNoLoginResult(){
		function = NO_LOGIN;
		explain = EXPLAIN_NO_LOGIN;
		flag = DefineRequestFlag.NO_LOGIN;
	}
	
	public static JsonUserNoLoginResult fromJson(String json) {
		Gson gson = new Gson();
		return  gson.fromJson(json, JsonUserNoLoginResult.class);
	}
}
