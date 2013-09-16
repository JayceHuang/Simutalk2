package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonGetMessage extends JsonRequest {
	
	/**
	 * 丢失消息ID
	 * 注：在客户端有消息数据但没有最大消息ID时为丢失状态
	 * 2012.11.22
	 */
	public static final long LOST_MAX_ID = -1;
	
	/**
	 * 最小消息ID
	 * 2012.11.22
	 */
	public static final long MIN_ID =0;
	
	
	public JsonGetMessage(){
		function = GET_MSG;
		handkey = MIN_ID;
	}
	
	public static JsonGetMessage fromJson(String json) {

		Gson gson = new Gson();
		JsonGetMessage user = gson.fromJson(json,JsonGetMessage.class);
		
		return user;
	}
}
