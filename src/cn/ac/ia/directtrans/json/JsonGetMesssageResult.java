package cn.ac.ia.directtrans.json;

import com.google.gson.Gson;


public class JsonGetMesssageResult extends JsonRequestResult{

	public static final String EXPLAIN_INFO_NULL 	= "��ȡʧ��(601)";
	public static final String EXPLAIN_DB_ERROR 	= "��ȡʧ��(602)";
	
	
	public JsonGetMesssageResult(JsonRequest requset) {
		// TODO Auto-generated constructor stub
		super(requset);
	}

	public static JsonGetMesssageResult fromJson(String json) {
		Gson gson = new Gson();
		return  gson.fromJson(json, JsonGetMesssageResult.class);
	}
}
