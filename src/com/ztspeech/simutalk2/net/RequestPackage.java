package com.ztspeech.simutalk2.net;

import cn.ac.ia.directtrans.json.JsonRequest;


public class RequestPackage  {
	
	public JsonRequest request;
	public String valueString = "";
	public long   valueLong = 0;
	
	public RequestPackage(JsonRequest req){
		request = req;
	}
}
