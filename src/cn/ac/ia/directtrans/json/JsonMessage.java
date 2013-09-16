package cn.ac.ia.directtrans.json;

import java.util.Date;
import com.google.gson.Gson;

public class JsonMessage extends JsonFunction {

	public class State {
		
		/**
		 * 消息状态收到消息
		 *  2012.11.26
		 */
		public static final int RECEIVE = 1;
		
		/**
		 * 消息状态 没有下发
		 * 2012.11.26
		 */	
		public static final int SENDING= 0;
	}
	
	/**
	 * 消息类型
	 *
	 */
	public static class Type {
		/**
		 * 
		 */
		public static int MSG = 0;
		/**
		 * 
		 */		
		public static int QA = 2;
	}

	public static class Function {

		public static final String INVITE = "invite";
		public static final String INVITE_ADD = "invite_add";
		public static final String LINKMAN_DEL = "linkman_del";
		public static final String ASK = "ask";
		/**
		 * 捞问题 2012.11-21
		 */
		public static final String FISH						= "fish";		
		public static final String MSG = "msg";
		public static final String QUESTION = "question";
		public static final String MEETING_ADD = "meeting_add";
		public static final String MEETING_DELETE = "meeting_del";
		public static final String MEETING_EDIT = "meeting_edit";
		public static final String LINKMAN_ONLINE = "linkman_online";
		public static final String LINKMAN_PHOTO = "linkman_photo";
		public static final String SOLVED = "solved";
		public static final String SOLVED_QUIT = "solved_quit";
		public static final String SOLVED_CLOSE = "solved_close";

	}

	public String text = "";
	public int 		senderId = 0;
	public String senderName = "";
	public long 	linkId = 0;
	public Date	 time;
	public String	 vId = "";
	public int		 vLen = 0;
	public long 	id = 0;
	public String photo = "";

	/**
	 * 消息下发状态
	 * 2012.11.26
	 */
	public int state = State.SENDING;
	
	public JsonMessage() {
		function = SEND_MSG;
	}

	public static JsonMessage fromJson(String json) {

		Gson gson = new Gson();
		JsonMessage user = gson.fromJson(json, JsonMessage.class);
		return user;
	}

	public void setData(JsonMessage msg) {

		text = msg.text;
		time = msg.time;
		senderId = msg.senderId;
		senderName = msg.senderName;
		function = msg.function;
		linkId = msg.linkId;
		photo = msg.photo;
		// sendToId = msg.sendToId;
	}
}
