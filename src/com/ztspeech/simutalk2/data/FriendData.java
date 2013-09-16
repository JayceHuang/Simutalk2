package com.ztspeech.simutalk2.data;


import cn.ac.ia.directtrans.json.UserState;


public class FriendData extends DataObject{
	
	public UserState user = new UserState(); 
	public long linkId = 0;
	public String photoId = "";
	public boolean selected = false;
	public boolean showSelected = true;
	public FriendData(UserState info) {
		
		user.setInfo(info);
		this.photoId = info.photo;
		this.id = info.id;
		this.name = info.name;
	}


}