package com.ztspeech.simutalk2.qa.data;

import com.ztspeech.simutalk2.data.DataObject;


public class FriendData extends DataObject{
	
	public static final int TYPE_ASK = 1;
	public static final int TYPE_SOLVE = 2;

	public FriendData(long id, String name){
		this.id = id;
		this.name = name;
	}
}