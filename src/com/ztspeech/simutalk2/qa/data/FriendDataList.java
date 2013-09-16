package com.ztspeech.simutalk2.qa.data;

import com.ztspeech.simutalk2.data.DataListObejct;
import com.ztspeech.simutalk2.data.DataObject;

public class FriendDataList extends DataListObejct {	
	
	private static FriendDataList mInstance = null;
	public static FriendDataList getInstance(){
		
		if(mInstance == null){
			mInstance = new FriendDataList();
		}
		
		return mInstance;
	}
	
	public long getNewId(){
		int nCount = this.size();
		long nId = 1;
		for(int i=0; i < nCount; i ++) {
			DataObject obj = this.get(i);
			if(obj.id >= nId){
				nId = obj.id;
			}
		}
		nId++;
		return nId;
	}
}
