package com.ztspeech.simutalk2.data;

import java.io.InputStream;
import java.util.Date;

import cn.ac.ia.directtrans.json.JsonMessage;

public class MsgInfoData extends DataObject {

	public class Define {
		public static final int TYPE_QA = 1;
		public static final int TYPE_MSG = 2;

		public static final String MSG_ID = "dataid";
		public static final int NEWS = 0;
		public static final int LOOK_OVER = 1;
	}

	private static long msgId = 0;
	public String text = "";
	public long linkId = 0;
	public int senderId = 0;
	public int sendToId = 0;
	public Date time;
	private String cmd = "";
	public String vId = "";
	public int vLen = 0;
	public InputStream spx = null;
	public int look_over = Define.NEWS;
	public String playText = "";
	public int state = 0;

	public boolean hasVoice() {
		return (vLen > 140);
	}

	public MsgInfoData() {
	}

	public InputStream getSpxStream(String text) {
		if (text.equals(playText)) {
			return spx;
		}
		return null;
	}

	public boolean isLookover() {
		return (Define.LOOK_OVER == this.look_over);
	}

	public void setLookover() {

		this.look_over = Define.LOOK_OVER;
	}

	public static void initId(int max) {
		msgId = max;
	}

	public static long getNewId() {
		msgId++;
		return msgId;
	}

	public MsgInfoData(JsonMessage msg) {

		setData(msg);
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
		type = Define.TYPE_QA;
		if (JsonMessage.Function.MSG.equals(cmd) || JsonMessage.Function.INVITE.equals(cmd)) {
			type = Define.TYPE_MSG;
		}
	}

	public String getCmd() {
		return cmd;
	}

	public void setData(JsonMessage msg) {

		text = msg.text;
		senderId = msg.senderId;
		name = msg.senderName;
		vLen = msg.vLen;
		vId = msg.vId;
		linkId = msg.linkId;
		time = msg.time; 
		setCmd(msg.function);

		this.look_over = Define.NEWS;
	}

}