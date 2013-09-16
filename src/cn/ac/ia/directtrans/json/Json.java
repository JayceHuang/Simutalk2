package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;

public class Json  {
	
	public String toJson() {

		return toJson(this);
	}	

	public static String toJson( Object o) {		

		Gson gson = new Gson();		
		return gson.toJson(o);
	}
	public static  <T> T fromJson(String json, Class<T> classOfT){
		Gson gson = new Gson();
		return gson.fromJson(json, classOfT);
	}

	public static Json fromJson(String json) {
		Gson gson = new Gson();
		return  gson.fromJson(json, Json.class);
	}	
}
