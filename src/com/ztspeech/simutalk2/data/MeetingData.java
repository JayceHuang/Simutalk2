package com.ztspeech.simutalk2.data;



public class MeetingData extends DataObject{
	
	public FriendDataList friendList = new FriendDataList();
	public long owner = 0;
	public boolean isEdit = false;
	
	public MeetingData(long id, int type, long owner, String name){
		
		this.owner = owner;
		this.name = name;
		this.type = type;
		this.id = id;		
	}


	public void setSelect(MeetingData data){
		super.setData(data);
		
		FriendDataList list = data.friendList;
		int nCount = friendList.size();;
		
		for( int i = 0; i < nCount; i ++) {
			
			FriendData f = friendList.get(i);
			FriendData d = list.findById(f.id);
			if(d != null) {
				f.selected = true;
			}
			else {
				f.selected = false;
			}
		}
	}	
	
	public void showSelect(boolean show){
		int nCount = friendList.size();;
		
		for( int i = 0; i < nCount; i ++) {
			
			FriendData f = friendList.get(i);
			f.showSelected = show;
		}
	}	
	
	public void clear(){
		friendList.clear();
	}
	
	public void add(FriendData data){

		friendList.add(data);
	}
	
	public void updateFriendList() {
		
		FriendDataList list = FriendDataList.getInstance();
		friendList.clear();
		for( int i =0; i < list.size(); i ++ ){
			FriendData data = list.get(i);
			data.selected = false;
   			friendList.add(data);
		}		
	}

	public void addSelectedData(MeetingData data) {
		// TODO Auto-generated method stub
		super.setData(data);
		
		FriendDataList list = data.friendList;
		int nCount = data.friendList.size();
		friendList.clear();
		for( int i = 0; i < nCount; i ++) {

			FriendData f = list.get(i);
			if(f.selected){
				friendList.add(f);
			}
		}		
	}


	public void edit(boolean b) {
		// TODO Auto-generated method stub
		int nCount = friendList.size();
		for( int i = 0; i < nCount; i ++) {
			
			FriendData f = friendList.get(i);
			f.showSelected = b;
		}		
	}
}