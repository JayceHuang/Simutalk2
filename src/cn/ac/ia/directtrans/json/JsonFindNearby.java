package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;

public class JsonFindNearby extends JsonRequest{

public static class ReusltFlag{
		
		public static final String	DB_ERROR 	= "查找失败(300)";
		public static final String	DATA_ERROR 	= "查找失败(301)";
	};
	
	public static final String TYPE_NAME		= "name";
	public static final String TYPE_EMAIL 		= "email";

	public String 	 text;
	public String 	 type;
	public String    longitude;//经度
	public String    latitude;//纬度
	public String    radius;//查找半径
	public String    from; 	// 结果范围
	public String    to;	// 结果范围
	
	
	public JsonFindNearby(){
		function = FIND_NEARBY;
		type = TYPE_EMAIL;
		from = "0";
		to = "10";	
		radius = "1000";
	}
	
	public static JsonFindNearby fromJson(String json) {

		Gson gson = new Gson();
		JsonFindNearby user = gson.fromJson(json,JsonFindNearby.class);		
		return user;
	}
}
