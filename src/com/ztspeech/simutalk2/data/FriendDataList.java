package com.ztspeech.simutalk2.data;


public class FriendDataList extends DataListObejct {	
	
	private static FriendDataList mInstance = null;
	private UserInfoList mUserList = UserInfoList.getInstanse();
	private boolean mIsChanged = false;
	public static FriendDataList getInstance(){
		
		if(mInstance == null){
			mInstance = new FriendDataList();
		}
		
		return mInstance;
	}

	
	public FriendData findByUserId(long id){		
		synchronized (items){
			int nCount = items.size();
			for( int i=0; i < nCount; i ++ ){
				FriendData data = (FriendData) items.get(i);
				if(data.user.id == id){
					return data;
				}
			}
		}
		return null;
	}

	public FriendData deleteByUserId(int id){		
		mIsChanged = true;
		synchronized (items){
			int nCount = items.size();
			for( int i=0; i < nCount; i ++ ){
				FriendData data = (FriendData) items.get(i);
				if(data.user.id == id){
					items.remove(i);
					return data;
				}
			}
		}
		return null;
	}
	
	public void add(FriendData data) {
		mIsChanged = true;
		mUserList.update(data);
		super.add(data);
	}
	
	public FriendData get(int n) {
		
		return (FriendData)super.get(n);
	}
	
	public FriendData findById(long id) {
		
		synchronized (items){
			return (FriendData) super.findById(id);
		}
    }
	
	public FriendData findByLinkId(long linkId) {   
		
		synchronized (items){
			int nCount = items.size();
			for( int i=0; i < nCount; i ++ ){
				FriendData data = (FriendData) items.get(i);
				if(data.linkId == linkId){
					return data;
				}
			}	
		}
		return null;
    }	

	public boolean isChanged() {
		return mIsChanged;
	}

	public void setChanged(boolean b) {
		mIsChanged = b;
	}   
}
