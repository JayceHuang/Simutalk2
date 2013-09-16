package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;

public class JsonRequestResult extends JsonRequest{

	public boolean 	 	succeed = false;
	public int 			flag = DefineRequestFlag.SUCCEED;
	public String 		explain;
	
	public boolean isResultFrom(JsonFunction info){
		
		if(this.function.equals(info.function)) {
			return true;
		}
		
		return false;
	}
	
	public JsonRequestResult(JsonRequest requset) {
		// TODO Auto-generated constructor stub		
		function = requset.function;
	}

	public JsonRequestResult(){
		
	}
	
	public static JsonRequestResult fromJson(String json) {
		Gson gson = new Gson();
		return  gson.fromJson(json, JsonRequestResult.class);
	}


}
