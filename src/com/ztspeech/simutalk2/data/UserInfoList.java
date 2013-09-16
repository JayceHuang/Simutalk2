package com.ztspeech.simutalk2.data;

import java.util.ArrayList;

import cn.ac.ia.directtrans.json.QuestionInfo;
import cn.ac.ia.directtrans.json.UserInfo;
import cn.ac.ia.directtrans.json.UserState;

/**
 * �û���Ϣ������
 *
 */
public class UserInfoList{

	protected ArrayList<UserInfo> items = new ArrayList<UserInfo>(); 

	private static UserInfoList mInstanse = null;
	
	/**
	 * �õ��û�ȫ���û���Ϣ�б�
	 * @return UserInfoList
	 */
	public static UserInfoList getInstanse() {

		if (mInstanse == null) {
			mInstanse = new UserInfoList();
		}

		return mInstanse;
	}
	
	
	
	/**
	 * ͨ��ID�����û���Ϣ
	 * @param id �û�ID
	 * @return �û���Ϣ  cn.ac.ia.directtrans.json.UserInfo
	 */
	public UserInfo findById(int id){
		
		synchronized (this){
			int nSize = items.size();
			for( int i=0; i< nSize; i++){
				UserInfo info = items.get(i);
				if(info.id == id){
					return info;
				}
			}
		}
		return null;
	}
	
	/**
	 * �����û����û���½�һ��
	 * @param id �û�id
	 * @return   �û���Ϣ
	 */
	public UserInfo findAndNewById( int id){
		
		UserInfo user = findById(id);
		if(user == null){
			user = new UserInfo();
			user.id = id;
			this.add(user);
		}
		
		return user;
	}
	
	private void add(UserInfo info){
		synchronized (this){
			items.add(info);
		}		
	}
	
	public void updateUserName(int id, String name){
		
		UserInfo user = findAndNewById(id);
		user.name = name;
	}
	
	public void updateUseInfo(int id, String name, String sPhoto){
		
		UserInfo user = findAndNewById(id);
		user.name = name;
		user.photo= sPhoto;
	}
	
	
	/**
	 * �����û���Ϣ 
	 * @param data UserState
	 */
	public void update(UserState data){
		
		UserInfo user = findAndNewById((int) data.id);
		user.photo = data.photo;
		user.name = data.name;
	}
	
	/**
	 * �����û���Ϣ 
	 * @param data FriendData
	 */
	public void update(FriendData data){
		
		UserInfo user = findAndNewById((int) data.id);
		user.photo = data.photoId;
		user.name = data.name;
	}
	
	/**
	 * �����û���Ϣ 
	 * @param data QuestionInfo
	 */	
	public void update(QuestionInfo data){
		
		UserInfo user = findAndNewById(data.senderId);
		user.photo = data.photo;
		user.name = data.senderName;
		
		if( data.sId != 0) {
			updateUseInfo(data.sId, data.sName, data.sPhoto);
		}
	}
	
	/**
	 * �����û���Ƭ��Ϣ 
	 * @param id      �û�id
	 * @param photoId ��Ƭid
	 */	
	public void updatePhoto(int id, String photoId){
		
		UserInfo user = findAndNewById(id);
		user.photo = photoId;
	}
}
