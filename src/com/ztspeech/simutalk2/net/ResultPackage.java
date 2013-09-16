package com.ztspeech.simutalk2.net;

import java.io.ByteArrayOutputStream;

public class ResultPackage {

	public int netFlag;
	public String error;
	public String cmd = "";
	public String valueString = "";
	public long valueLong = 0;
	public ByteArrayOutputStream result;
	
	public boolean isNetSucceed(){
		
		return (netFlag == NetResultFlag.POST_DATA_SUCCEED );
	}

	public byte[] getBytes(){
		if(result == null){
			return null;
		}
		return result.toByteArray();
	}
	
	public String getJson() {

		if(result == null){
			return "";
		}		
		return result.toString();
	}
}
