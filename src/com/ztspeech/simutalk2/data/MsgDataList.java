package com.ztspeech.simutalk2.data;

import java.util.Date;

import cn.ac.ia.directtrans.json.JsonMessage;
import cn.ac.ia.directtrans.json.QuestionInfo;

public class MsgDataList extends DataListObejct {

	private MsgGroupTable mTable = MsgGroupTable.getInstance();
	public String text = "";
	public int senderId = 0;
	public Date time;
	public boolean mIsChanged = false;

	public int getOwnerId() {
		if (size() > 0) {
			MsgInfoData data = (MsgInfoData) get(0);
			return data.senderId;
		}
		return 0;
	}

	public MsgInfoData getFirstItem() {
		synchronized (this) {
			if (size() > 0) {
				return (MsgInfoData) get(0);
			}
		}
		return null;
	}

	public MsgInfoData getNewData() {

		synchronized (this) {
			int nSize = size();
			if (nSize > 0) {
				return (MsgInfoData) get(nSize - 1);
			}
		}
		return null;
	}

	public void add(MsgInfoData data) {

		senderId = data.senderId;
		name = data.name;
		type = data.type;
		text = data.text;
		time = data.time;
		id = data.linkId;

		udpateUserName(data);
		synchronized (this) {
			mIsChanged = true;
			super.add(data);
		}
	}

	/**
	 * 更新用户名称
	 * 
	 * @param data
	 */
	public void udpateUserName(MsgInfoData data) {
		boolean update = false;
		synchronized (this) {
			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				if (msg.senderId == data.senderId) {
					if (false == msg.name.equals(data.name)) {
						update = true;
						msg.name = data.name;
					}
				}
			}
		}

		if (update) {
			mTable.setUserName(data.senderId, data.name);
		}

	}

	public boolean isChanged() {
		return mIsChanged;
	}

	public void setChanged(boolean b) {

		mIsChanged = b;
	}

	public int getNewsCount() {
		int nNews = 0;
		synchronized (this) {
			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				if (MsgInfoData.Define.NEWS == msg.look_over) {
					nNews++;
				}
			}
		}
		return nNews;
	}

	public boolean setItemLookOver(long msgId) {

		MsgGroupList group = MsgGroupList.getInstance();
		synchronized (this) {

			int nSize = size();
			for (int i = 0; i < nSize; i++) {

				MsgInfoData msg = (MsgInfoData) get(i);
				if (msgId == msg.id) {
					if (msg.look_over != MsgInfoData.Define.LOOK_OVER) {
						msg.look_over = MsgInfoData.Define.LOOK_OVER;
						group.setDBLookover(msgId);
					}
					return true;
				}
			}
		}
		return false;
	}

	public void setAllLookOver() {

		MsgGroupList group = MsgGroupList.getInstance();
		mIsChanged = true;
		synchronized (this) {

			int nSize = size();

			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				if (false == msg.isLookover()) {
					msg.look_over = MsgInfoData.Define.LOOK_OVER;
					group.setDBLookover(msg.id);
				}
			}
		}
	}

	public void getList(MsgDataList List) {

		List.name = name;
		synchronized (this) {
			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				List.add(msg);
			}
		}
	}

	public MsgInfoData getLastMsg() {

		synchronized (this) {
			int n = size() - 1;
			if (n > -1) {
				return (MsgInfoData) get(n);
			}
		}

		return null;
	}

	public MsgInfoData getLinkman(int id) {

		synchronized (this) {
			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				if (msg.senderId != id) {
					return msg;
				}
			}
		}
		return null;
	}

	private boolean isFunction(String function) {

		synchronized (this) {
			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				if (function.equals(msg.getCmd())) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean setState(long linkId, int state) {

		MsgGroupList group = MsgGroupList.getInstance();
		synchronized (this) {

			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				msg.state = state;
			}
			return group.setDBState(linkId, state);
		}
	}

	public boolean closed() {

		synchronized (this) {
			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				if (msg.state >= QuestionInfo.STATE_SOLVED) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean enabled() {

		synchronized (this) {
			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				if (msg.state != QuestionInfo.STATE_MARK) {
					return false;
				}
			}
		}

		return true;
	}

	public void clearDB() {
		MsgGroupTable table = MsgGroupTable.getInstance();
		if (isFunction(JsonMessage.Function.SOLVED)) {
			MsgInfoData one = getFirstItem();
			synchronized (this) {
				int nSize = size();
				for (int i = 1; i < nSize; i++) {
					MsgInfoData msg = (MsgInfoData) get(i);
					table.deleteMsg(msg.id);
				}
				clear();
			}
			this.add(one);
		} else {
			table.deleteLookOverMsg(id);
			clear();
		}
	}

	public boolean IsSolved() {

		int nSize = size();
		if (nSize == 0) {
			return false;
		}
		if (nSize > 1) {
			return true;
		}

		MsgInfoData data = (MsgInfoData) get(0);
		if (data.state == QuestionInfo.STATE_MARK) {
			return true;
		}

		return false;
	}

	public void updateState(QuestionInfo info) {
		int nSize = size();
		if (nSize == 0) {
			return;
		}
		MsgInfoData data = (MsgInfoData) get(0);
		if (data.state != info.state) {
			data.state = info.state;
			this.setState(id, info.state);
		}
	}

	/**
	 * 查找消息是否存在
	 * 
	 * @param linkId
	 * @param lTime
	 * @param type
	 * @return
	 */
	public MsgInfoData findMessage(int sender, long linkId, Date date, int type) {

		synchronized (this) {
			int nSize = size();
			for (int i = 0; i < nSize; i++) {
				MsgInfoData msg = (MsgInfoData) get(i);
				if (msg.linkId == linkId && msg.type == type
						&& msg.senderId == sender) {
					if (msg.time.equals(date)) {

						return msg;
					}
				}
			}
		}

		return null;
	}

}
