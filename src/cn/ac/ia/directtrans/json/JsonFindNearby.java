package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;

public class JsonFindNearby extends JsonRequest{

public static class ReusltFlag{
		
		public static final String	DB_ERROR 	= "����ʧ��(300)";
		public static final String	DATA_ERROR 	= "����ʧ��(301)";
	};
	
	public static final String TYPE_NAME		= "name";
	public static final String TYPE_EMAIL 		= "email";

	public String 	 text;
	public String 	 type;
	public String    longitude;//����
	public String    latitude;//γ��
	public String    radius;//���Ұ뾶
	public String    from; 	// �����Χ
	public String    to;	// �����Χ
	
	
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
