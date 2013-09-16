package cn.ac.ia.directtrans.json;

import java.util.ArrayList;

import com.google.gson.Gson;


public class JsonGetMeetingsResult extends JsonRequestResult{

	public static final String EXPLAIN_INFO_NULL 	= "获取失败(401)";
	public static final String EXPLAIN_DB_ERROR 	= "获取失败(402)";
	
	public String cmd;
	public String linkId;
	
	public ArrayList<MeetingInfo> meetings = new ArrayList<MeetingInfo>();
	public ArrayList<UserState> items = new ArrayList<UserState>();
	
	public JsonGetMeetingsResult(){
		
		function = GET_MEETING;
		cmd = JsonGetMeetings.GET_MEETINGS;
		explain = "";
	}
	
	public static JsonGetMeetingsResult fromJson(String json) {
		Gson gson = new Gson();
		return  gson.fromJson(json, JsonGetMeetingsResult.class);
	}

	public void add(UserState info) {
		// TODO Auto-generated method stub
		items.add(info);
	}
	
	public void add(MeetingInfo info) {
		// TODO Auto-generated method stub
		meetings.add(info);
	}	
}
