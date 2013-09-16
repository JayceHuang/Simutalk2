package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonFindLinkman extends JsonRequest {
	
	public static class ReusltFlag{
		
		public static final String	DB_ERROR 	= "查找失败(300)";
		public static final String	DATA_ERROR 	= "查找失败(301)";
	};
	
	public static final String TYPE_NAME		= "name";
	public static final String TYPE_EMAIL 		= "email";

	public String 	 text;
	public String 	 type;
	
	public String    from; 	// 结果范围
	public String    to;	// 结果范围
	
	
	public JsonFindLinkman(){
		function = FIND_LINKMAN;
		type = TYPE_EMAIL;
		from = "0";
		to = "10";		
	}
	
	public static JsonFindLinkman fromJson(String json) {

		Gson gson = new Gson();
		JsonFindLinkman user = gson.fromJson(json,JsonFindLinkman.class);		
		return user;
	}
}
