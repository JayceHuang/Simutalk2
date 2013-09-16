package com.ztspeech.simutalk2.data;

public class DataObject {
	
	public long  id = 0;
	public int type	= -1;
	public String name 	= "";
	
	public void setData(DataObject data){
		id 		= data.id;
		type 	= data.type;
		name 	= data.name;	
	}
}
