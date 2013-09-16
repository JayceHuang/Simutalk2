package com.ztspeech.simutalk2.data;




public class MeetingDataList extends DataListObejct {

	private static MeetingDataList mInstance = null;
	private boolean mIsMeetingChanged = false;
	private boolean mIsFriendChanged = false; 
	
	public static MeetingDataList getInstance() {
		// TODO Auto-generated method stub
		
		if(mInstance == null){
			mInstance = new MeetingDataList();
		}
		
		return mInstance;
	}
	

	
	public MeetingData get(int nIndex){
		
		return (MeetingData) super.get(nIndex);
	}
	
	public MeetingData findById(long id) {   	
    	
    	return (MeetingData)super.findById(id);
    }

	public boolean isMeetingChanged() {
		// TODO Auto-generated method stub
		return mIsMeetingChanged;
	}
	
	public boolean isFriendChanged(){
		
		return mIsFriendChanged;
	}
	
	public void add(MeetingData data) {

		mIsMeetingChanged = true;

		super.add(data);
	}
	

	
	public void setFriendChanged(boolean b) {
		// TODO Auto-generated method stub
		mIsFriendChanged = b;

	}
	
	public void setMeetingChanged(boolean b) {
		// TODO Auto-generated method stub
		mIsMeetingChanged = b;

	}

	public void setChanged(boolean b) {
		// TODO Auto-generated method stub
		mIsFriendChanged = b;
		mIsMeetingChanged = b;

	}  
}
