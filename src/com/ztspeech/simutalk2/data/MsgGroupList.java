package com.ztspeech.simutalk2.data;

import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MsgGroupList extends DataListObejct {

	private MsgGroupTable mTable = MsgGroupTable.getInstance();

	public static final String PARAM_TYPE = "type";
	public static final String PARAM_ID = "id";

	private boolean mIsChanged = true;

	private static MsgGroupList mInstance = null;

	public static MsgGroupList getInstance() {

		if (mInstance == null) {
			mInstance = new MsgGroupList();
		}

		return mInstance;
	}

	public void addMsgToDB(MsgInfoData data) {

		addToDB(data);

	}

	public MsgDataList addMsg(MsgInfoData data) {

		MsgDataList list = findAndNewItem(data.linkId, data.type);
		list.add(data);
		return list;
	}

	private boolean addToDB(MsgInfoData data) {
		return mTable.addToDB(data);
	}

	public boolean setDBLookover(long id) {
		return mTable.setDBLookover(id);
	}

	public boolean delete(long id) {
		synchronized(items) {
			int nSize = items.size();
			for (int i = 0; i < nSize; i++) {
	
				MsgDataList data = (MsgDataList) items.get(i);
				if (data.id == id) {
					int nItems = data.size();
					boolean remove = true;
					for (int n = 0; n < nItems; n++) {
						MsgInfoData info = (MsgInfoData) data.get(n);
						if (info.look_over != MsgInfoData.Define.LOOK_OVER) {
							remove = false;
						}
					}
					mTable.deleteLookOverMsg(id);
					if (remove) {
						items.remove(i);
					}
					return true;
				}
			}
		}

		return false;
	}

	public void load() {

		clear();
		SQLiteDatabase reader = mTable.getReadableDatabase();
		int nMaxId = 1;
		Cursor cursor = reader.rawQuery("select max(dataid) from " + mTable.tableName, null);
		if (cursor.moveToNext()) {
			nMaxId = cursor.getInt(0);
		}
		MsgInfoData.initId(nMaxId);
		cursor.close();

		cursor = reader.rawQuery("select dataid,function, link_id, type,sender_id,"
				+ "sender_name,look_over,msg_time,text,sound_length,sound, state from " + mTable.tableName
				+ " order by msg_time asc", null);

		while (cursor.moveToNext()) {

			int i = 0;
			MsgInfoData data = new MsgInfoData();
			data.id = cursor.getInt(i++);
			data.setCmd(cursor.getString(i++));
			data.linkId = cursor.getLong(i++);
			data.type = cursor.getInt(i++);
			data.senderId = cursor.getInt(i++);
			data.name = cursor.getString(i++);
			data.look_over = cursor.getInt(i++);
			data.time = new Date(cursor.getLong(i++));
			data.text = cursor.getString(i++);
			data.vLen = cursor.getInt(i++);
			data.vId = cursor.getString(i++);
			data.state = cursor.getInt(i++);
			addMsg(data);
		}
		cursor.close();
	}

	public MsgDataList findAndNewItem(long linkId, int type) {

		MsgDataList list = findItem(linkId, type);
		if (list != null) {
			return list;
		}

		return newItem(linkId, type);
	}

	public MsgDataList findItem(long linkId, int type) {
		synchronized(items) {
			int nCount = items.size();
			for (int i = 0; i < nCount; i++) {
	
				MsgDataList list = (MsgDataList) items.get(i);
				if (list.id == linkId && list.type == type) {
					return list;
				}
			}
		}
		return null;
	}

	public void setMsgChanged(boolean b) {

		mIsChanged = b;
	}

	public boolean isMsgChanged() {

		return mIsChanged;
	}

	private MsgDataList findItemByLinkId(long linkId) {
		
		synchronized(items) {
			int nCount = items.size();
			for (int i = 0; i < nCount; i++) {
	
				MsgDataList list = (MsgDataList) items.get(i);
				if (list.id == linkId) {
	
					return list;
				}
			}
		}

		return null;
	}

	public MsgDataList newItem(long linkId, int type) {
		MsgDataList list = new MsgDataList();
		items.add(list);
		list.id = linkId;
		list.type = type;
		return list;
	}

	public void clearMsg() {

		mIsChanged = true;
		mTable.clearMsg();
		this.clear();
	}

	public boolean setItemLookOver(int msgId) {
		synchronized(items) {
			
			int nSize = items.size();	
			for (int i = 0; i < nSize; i++) {
	
				MsgDataList list = (MsgDataList) items.get(i);
				if (list.setItemLookOver(msgId)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean setState(long linkId, int state) {
		
		synchronized(items) {
			
			int nSize = items.size();	
			for (int i = 0; i < nSize; i++) {
	
				MsgDataList list = (MsgDataList) items.get(i);
				if (list.id == linkId) {
					return list.setState(linkId, state);
				}
			}
		}
		return false;
	}

	public void clearMsgBySenderId(long linkId) {
		
		mTable.clearMsgBySenderId(linkId);
		MsgDataList list = this.findItemByLinkId(linkId);
		if (list != null) {
			list.clear();
		}
	}

	public MsgDataList findItemBySenderId(int mSendToId) {
		
		synchronized(items) {
			int nCount = items.size();
	
			for (int i = 0; i < nCount; i++) {
	
				MsgDataList list = (MsgDataList) items.get(i);
				if (list.senderId == mSendToId) {
	
					return list;
				}
			}
		}

		return null;
	}

	public boolean setDBState(long linkId, int state) {
		return mTable.setState(linkId, state);
	}

	public int getNewsQACount() {
		int nCount = 0;
		synchronized(items) {

			int nSize = items.size();
			for (int i = 0; i < nSize; i++) {
	
				MsgDataList data = (MsgDataList) items.get(i);
				if (data.type == MsgInfoData.Define.TYPE_QA) {
					if (data.getNewsCount() > 0) {
						nCount++;
					}
				}
			}
		}
		return nCount;
	}

	public int getFriendNewsCount() {
		
		int nCount = 0;		
		synchronized(items) {
			int nSize = items.size();
			for (int i = 0; i < nSize; i++) {
	
				MsgDataList data = (MsgDataList) items.get(i);
				if (data.type == MsgInfoData.Define.TYPE_MSG) {
					if (data.getNewsCount() > 0) {
						nCount++;
					}
				}
			}
		}

		return nCount;
	}

	public int getNewsCount() {
		int nCount = getNewsQACount() + getFriendNewsCount();
		return nCount;
	}

	/**
	 * 查找消息是否存在
	 * @param linkId
	 * @param time
	 * @param type
	 * @return
	 */
	public MsgInfoData findMessage(int senderId, long linkId, Date time, int type) {
	
		int nCount = items.size();
		MsgInfoData data = null;
		for (int i = 0; i < nCount; i++) {

			MsgDataList list = (MsgDataList) items.get(i);
			
			data = list.findMessage(senderId, linkId, time, type);
			if (data != null) {
				return data;
			}
		}
		
		return null;
	}

}
