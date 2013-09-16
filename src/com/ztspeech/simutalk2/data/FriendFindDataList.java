package com.ztspeech.simutalk2.data;


public class FriendFindDataList extends DataListObejct {	
	
	private static FriendFindDataList mInstance = null;
	public static FriendFindDataList getInstance(){
		
		if(mInstance == null){
			mInstance = new FriendFindDataList();
		}
		
		return mInstance;
	}

	public void add(FriendFindData data) {
		
		super.add(data);
	}
	
	public FriendFindData get(int n) {
		
		return (FriendFindData)super.get(n);
	}
	
	public FriendFindData findById(long id) {   
		return (FriendFindData) super.findById(id);
    }   
}
