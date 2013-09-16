package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonGetMessage extends JsonRequest {
	
	/**
	 * ��ʧ��ϢID
	 * ע���ڿͻ�������Ϣ���ݵ�û�������ϢIDʱΪ��ʧ״̬
	 * 2012.11.22
	 */
	public static final long LOST_MAX_ID = -1;
	
	/**
	 * ��С��ϢID
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
