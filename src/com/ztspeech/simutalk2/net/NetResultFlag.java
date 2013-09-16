package com.ztspeech.simutalk2.net;

public class NetResultFlag {

	public static final int POST_CONNECT_ERROR = -1; 
	public static final int POST_DATA_ERROR = -2;
	public static final int POST_DATA_SUCCEED = 0;
	
	public static String getString( int flag){
		
		if(flag == POST_CONNECT_ERROR){
			return "��������ʧ�ܣ�";
		}
		else if(flag == POST_DATA_ERROR){
			
			return "���ӷ�����ʧ�ܣ�";
		}
		else if(flag == POST_DATA_SUCCEED){
			
			return "�ɹ���";
		}
		
		return  "���������أ�" + flag;
	}
}
