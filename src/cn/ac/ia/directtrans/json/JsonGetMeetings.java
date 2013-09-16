package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonGetMeetings extends JsonRequest {
	
	public static final String GET_MEETINGS	=	"meetings";
	public static final String GET_ITEMS	=	"items";
	
	public String cmd;
	public long  linkId;
	
	public JsonGetMeetings(){
		function = GET_MEETING;
		cmd = GET_MEETINGS;
	}
	
	public static JsonGetMeetings fromJson(String json) {

		Gson gson = new Gson();
		JsonGetMeetings user = gson.fromJson(json,JsonGetMeetings.class);
		
		return user;
	}
}
