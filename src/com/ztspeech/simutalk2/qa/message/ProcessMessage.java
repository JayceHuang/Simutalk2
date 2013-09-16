package com.ztspeech.simutalk2.qa.message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import cn.ac.ia.directtrans.json.AskTask;
import cn.ac.ia.directtrans.json.DefineRequestFlag;
import cn.ac.ia.directtrans.json.DefineType;
import cn.ac.ia.directtrans.json.Json;
import cn.ac.ia.directtrans.json.JsonData;
import cn.ac.ia.directtrans.json.JsonFunction;
import cn.ac.ia.directtrans.json.JsonGetMessage;
import cn.ac.ia.directtrans.json.JsonLogin;
import cn.ac.ia.directtrans.json.JsonMessage;
import cn.ac.ia.directtrans.json.JsonRequest;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.MeetingInfo;
import cn.ac.ia.directtrans.json.QuestionInfo;
import cn.ac.ia.directtrans.json.ResultString;
import cn.ac.ia.directtrans.json.UserState;

import com.ztspeech.recognizer.PhoneInfo;
import com.ztspeech.recognizer.speak.OnTTSPlayerListener;
import com.ztspeech.simutalk2.data.AskTaskList;
import com.ztspeech.simutalk2.data.FriendData;
import com.ztspeech.simutalk2.data.FriendDataList;
import com.ztspeech.simutalk2.data.GlobalData;
import com.ztspeech.simutalk2.data.MeetingData;
import com.ztspeech.simutalk2.data.MeetingDataList;
import com.ztspeech.simutalk2.data.MsgDataList;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.MsgGroupTable;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.TransDataBase;
import com.ztspeech.simutalk2.data.TransTextTable;
import com.ztspeech.simutalk2.data.UserInfoList;
import com.ztspeech.simutalk2.data.MsgInfoData.Define;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.PostPackage;
import com.ztspeech.simutalk2.net.PostPackage.IHttpPostListener;
import com.ztspeech.simutalk2.net.RequestPackage;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.qa.MainActivity;

public class ProcessMessage implements IHttpPostListener, Runnable, OnTTSPlayerListener {


	
	public static boolean isAutoTTS = false;
	private static ProcessMessage instance;
	private UserInfo mUser = UserInfo.getInstanse();
	private FriendDataList mFriends = FriendDataList.getInstance();;
	private JsonGetMessage jsonGetMessage = new JsonGetMessage();
	private RequestPackage message = new RequestPackage(jsonGetMessage);
	private MsgGroupList mMsgGroupList = MsgGroupList.getInstance();
	private UserInfoList mUserList = UserInfoList.getInstanse();
	private PostPackage mPostPackage;
	private Thread mTread = new Thread(this);
	private boolean isStop = false;
	private boolean isRuning = false;
	private String host;

//	private Context context;
//	private static TransDataBase mDatabase;
	//public static TransTextTable mTableTransText;
//	友缘问答消息通知check时间：
//	聊天界面：5秒；
//	非聊天界面：30秒；
//	后台：3分钟；
	
	private final static int SLEEP_ACTIVE_TIME = 1000 * 30 ;
	private final static int SLEEP_NO_ACTIVE_TIME = 1000 * 180 ;
	private final static int SLEEP_TALKING_TIME = 1000 * 5;
	
	private final static int RELOGINCOUNTTOSTOP=5;//重新登录次数
	private static boolean mSleepStateChanged = true;
	private int mReloginCount = 0;
	private boolean isTalking = false;
	private static boolean isAppActive = true;
	
	public boolean getIsRunning(){
		return isRuning;
	}
	
	public static void setActiveState(boolean  state){
		mSleepStateChanged = true;
		isAppActive = state;
	}
	
	public void setTalking(boolean  state){
		mSleepStateChanged = true;
		isTalking = state;
	}
	
	public static ProcessMessage getInstance() {
		if (instance == null) {
			instance = new ProcessMessage();
		}
		return instance;
	}

	public void stop() {

		isStop = true;
		mTread.stop();
		// mMessageLoop.removeCallbacks(mMessageRunnable);
	}

	public void set(Context context, String host) {

		// mTtsPlayer = new TTSPlayer(context,"", this);
		mPostPackage = new PostPackage(context, this);
		this.host = host;
		//this.context = context;
	}

	//private File file = null;//日志文件
	public void start() {

		if (isRuning == false) {
			//reLogin();
			//file = createLog();//创建日志文件
			isStop = false;
			mTread.start();
		}
	}

	public void onNetPostResult(PostPackage owner, ResultPackage result) {

		if (result.isNetSucceed()) {
			if (DefineType.POST_TYPE_STR.equals(owner.type)) {

				String json = result.getJson();
				ResultString rs = Json.fromJson(json, ResultString.class);
				if (rs != null) {
					if (rs.succeed) {
						processString(rs.function, json);
						return;
					} else {
						if (rs.flag == DefineRequestFlag.NO_LOGIN) {
							if (mReloginCount > 1) {
								threadSleep(3000);
							}
							mReloginCount++;
							/*printLog(file, "\n mReloginCountinPostResult():"+mReloginCount+"\n");*/
							reLogin();
						}
					}
				}
			} else if (DefineType.POST_TYPE_BIN.equals(owner.type)) {

				process(result);
			}
		} else {
			mUser.setLogin(false);
		}
		// mMessageLoop.postDelayed(mMessageRunnable, 2000);
	}

	private void reLogin() {

		JsonRequest request = new JsonRequest();
		JsonLogin login = new JsonLogin();
		request.function = JsonFunction.LOGIN;
		login.version =UserInfo.version;  // 程序版本
		login.imei = PhoneInfo.getInstance().getIMEI();
		login.name = mUser.getUserName();
		request.json = Json.toJson(login);
		mUser.setLogin(false);
		mPostPackage.post(request, host, false);
	}

	private void processString(String fun, String json) {

		if (JsonFunction.LOGIN.equals(fun)) {
			OnLogin(json);
			mReloginCount = 1;//将重登录次数设置为1
		} else {

			JsonRequestResult messages = JsonRequestResult.fromJson(json, JsonRequestResult.class);
			if (messages == null) {
				return;
			}
			process(messages, true);
		}
	}

	/**
	 * 处理二进制结果数据
	 * 
	 * @param result
	 */
	private void process(ResultPackage result) {

		if (JsonFunction.GET_PHOTO.equals(result.cmd)) {
			//byte photo[] = result.getBytes();

		}
	}

	private void OnLogin(String json) {
		JsonRequestResult ret = JsonRequestResult.fromJson(json);
		AskTaskList list = GlobalData.getAskInstance();
		list.clear();
		if (ret != null) {
			if (ret.succeed == true) {
				UserState state = Json.fromJson(ret.json, UserState.class);
				mUser.setLogin(true);
				mUser.setInfo(state);
				process(ret,true);
			}
		}
	}

	/**
	 * 处理消息
	 * @param data
	 * @param updateId  是否更新 消息最大 ID（2012.11.27 添加）
	 */
	public void process(JsonData data,boolean updateId) {
		
		if (data.json != null) {
			
			if (data.json.length() > 0) {
				
				if (JsonFunction.NO_LOGIN.equals(data.function)) {

				} else if (JsonFunction.GET_MSG.equals(data.function)) {
					getMessage(data,updateId);
				} else if (JsonFunction.GET_MSG.equals(data.function)) {

				} else if (JsonFunction.GET_MEETING.equals(data.function)) {
					updateMeetings(data);
				} else if (JsonFunction.DELETE_MEETING.equals(data.function)) {
					deleteMeeting(data);
				} else if (JsonFunction.EDIT_LINKMAN.equals(data.function)) {

				} else if (JsonFunction.ONLINE_LINKMAN.equals(data.function)) {
					updateOnLineState(data);
				} else if (JsonMessage.Function.QUESTION.equals(data.function)) {
					updateQusetion(data);
				}
			}
		}

		int nSize = data.count();
		for (int i = 0; i < nSize; i++) {
			process(data.get(i),updateId);
		}
	}

	private void updateQusetion(JsonData data) {

		QuestionInfo question = Json.fromJson(data.json, QuestionInfo.class);
		MsgDataList msgList = mMsgGroupList.findItem(question.id, MsgInfoData.Define.TYPE_QA);
		if (msgList == null) {
			MsgInfoData info = new MsgInfoData();
			info.text = question.text;
			info.time = question.time;
			info.senderId = question.senderId;
			info.name = question.senderName;
			info.setCmd(JsonMessage.Function.SOLVED);
			info.vLen = question.vLen;
			info.vId = question.vId;
			info.state = question.state;
			info.linkId = question.id;
			info.look_over = Define.LOOK_OVER;

			msgList = mMsgGroupList.addMsg(info);
			mMsgGroupList.addMsgToDB(info);
			mMsgGroupList.setMsgChanged(true);
		}

		mUserList.update(question);
		msgList.updateState(question);
	}

	private void updateOnLineState(JsonData data) {

		// UserInfo linkman = Json.fromJson(data.json, UserInfo.class);

	}

	/**
	 * 更新用户照片
	 * 
	 * @param id
	 * @param photo
	 */

	private void deleteMeeting(JsonData data) {

		long linkmanId = Long.valueOf(Json.fromJson(data.json, String.class));
		// MeetingDataList list = MeetingDataList.getInstance();
		// 会议
		// list.delete(meetingId);
		// list.setChanged(true);
		mFriends.delete(linkmanId);
		mFriends.setChanged(true);
	}

	/**
	 * 解析并执行消息内容
	 * @param data
	 */
	private void getMessage(JsonData data, boolean updateId) {
		
		JsonMessage msg = Json.fromJson(data.json, JsonMessage.class);
		MsgInfoData d = new MsgInfoData(msg);
		
		if (isShowMessage(msg)) {
			
			if(updateId){
				mUser.setMaxMsgId(msg.id); // 保存本地最大
			}
			
			// 去除重复数据 2012.11.26
			if( null != mMsgGroupList.findMessage(d.senderId, d.linkId, d.time, d.type)){
				return;
			}
			
			MsgDataList list = mMsgGroupList.findItem(d.linkId, d.type);
			if (list == null) {

				// 舍弃没有接收对象的消息
				if (MsgInfoData.Define.TYPE_MSG == d.type) {
					if (null == mFriends.findByLinkId(d.linkId)) {
						return;
					}
				}
				else {
					return;
				}
			}

			if (msg.senderId != UserInfo.state.id) {
				if (JsonMessage.Function.SOLVED.equals(msg.function)) {

					if (list != null) {
						if (list.enabled() == false) {
							list.setState(msg.linkId, QuestionInfo.STATE_MARK);
						}
					}
				}
			} else {
				d.look_over = MsgInfoData.Define.LOOK_OVER;
			}
			
			if(msg.state == JsonMessage.State.RECEIVE){
				d.look_over = MsgInfoData.Define.LOOK_OVER;
			}
			
			d.state = QuestionInfo.STATE_MARK;

			list = mMsgGroupList.addMsg(d);
			mMsgGroupList.addMsgToDB(d);
			mMsgGroupList.setMsgChanged(true);

			if (d.look_over != MsgInfoData.Define.LOOK_OVER) {

				MainActivity.getInstance().showNotification(list, d);
				
			} else {
				// MainActivity.getInstance().showNotification(list,d);
			}
		}
		doMessage(msg);
	}

	private boolean isShowMessage(JsonMessage a) {
		String fun = a.function;
		if (JsonMessage.Function.MEETING_ADD.equals(fun) || JsonMessage.Function.MEETING_DELETE.equals(fun)
				|| JsonMessage.Function.MEETING_EDIT.equals(fun) || JsonMessage.Function.INVITE_ADD.equals(fun)
				|| JsonMessage.Function.LINKMAN_DEL.equals(fun) || JsonMessage.Function.INVITE.equals(fun)
				|| JsonMessage.Function.SOLVED.equals(fun) || JsonMessage.Function.SOLVED_QUIT.equals(fun)
				|| JsonMessage.Function.SOLVED_CLOSE.equals(fun) || JsonMessage.Function.MSG.equals(fun)) {

			return true;
		}

		return false;
	}

	/**
	 * 执行消息内容
	 * 
	 * @param a
	 */

	private void doMessage(JsonMessage msg) {

		String fun = msg.function;
		if (JsonMessage.Function.MEETING_ADD.equals(fun) || JsonMessage.Function.MEETING_DELETE.equals(fun)
				|| JsonMessage.Function.MEETING_EDIT.equals(fun) || JsonMessage.Function.INVITE_ADD.equals(fun)
				|| JsonMessage.Function.INVITE.equals(fun)) {
			long time = msg.time.getTime();
			if (time >= mUser.loginTime) {

				JsonRequest requset = new JsonRequest();
				requset.function = JsonFunction.GET_MEETING;
				requset.json = msg.linkId + "";
				mPostPackage.post(requset, host, false);
			}
		}
		// 捞问题数量
		else if(JsonMessage.Function.FISH.equals(fun)){
			long time = msg.time.getTime();
			if (time >= mUser.loginTime) {

				updateFishList(msg);
			}			
		} else if (JsonMessage.Function.ASK.equals(fun)) {
			long time = msg.time.getTime();
			if (time >= mUser.loginTime) {

				updateAskList(msg);
			}
		} else if (JsonMessage.Function.SOLVED_QUIT.equals(fun)) {
			// 放弃问题

			MsgDataList list = mMsgGroupList.findItem(msg.linkId, MsgInfoData.Define.TYPE_QA);
			if (list != null) {
				list.setState(msg.linkId, QuestionInfo.STATE_UNSOLVED);
				list.setChanged(true);
			}
		} else if (JsonMessage.Function.SOLVED_CLOSE.equals(fun)) {
			// 关闭问题
			MsgDataList list = mMsgGroupList.findItem(msg.linkId, MsgInfoData.Define.TYPE_QA);
			if (list != null) {
				list.setState(msg.linkId, QuestionInfo.STATE_SOLVED);
				list.setChanged(true);
			}
		}

		else if (JsonMessage.Function.LINKMAN_DEL.equals(fun)) {
			// 关闭问题
			MsgDataList list = mMsgGroupList.findItem(msg.linkId, MsgInfoData.Define.TYPE_MSG);
			if (list != null) {
				list.setState(msg.linkId, QuestionInfo.STATE_CLOSE);
				list.setChanged(true);
			}
		}
	}

	private void updateAskList(JsonMessage msg) {

		AskTaskList list = GlobalData.getAskInstance();
		list.clear();
		int nCount = msg.items.size();
		for (int i = 0; i < nCount; i++) {
			JsonData data = msg.items.get(i);
			AskTask task = Json.fromJson(data.json, AskTask.class);
			if (task != null) {

				list.AddTask(task);
			}
		}
	}

	private void updateFishList(JsonMessage msg) {

		AskTaskList list = GlobalData.getFishInstance();

		list.clear();
		int nCount = msg.items.size();
		for (int i = 0; i < nCount; i++) {
			JsonData data = msg.items.get(i);
			AskTask task = Json.fromJson(data.json, AskTask.class);
			if (task != null) {

				list.AddTask(task);
			}
		}
	}
	/**
	 * 创建日志文件
	 */
	/*private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public File createLog(){
		File fos = null;
		long timestamp = System.currentTimeMillis();
		formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = formatter.format(new Date());
		String fileName = "log"+time + "-" + timestamp + ".zts";
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = Util.ZTSPEECH_PATH+"/log/";
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			fos = new File(path + fileName);
		}
		return fos;
	}*/
	
	/**
	 * 输出日志信息
	 */
	/*public void printLog(File file,String content){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file,true);
			fos.write(content.getBytes());
			Log.e("@@@@@@@@@@@@@@@@@@@", content);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	public void run() {

		isRuning = true;
		
		while (false == isStop) {

			RequestPackage request = getRequset();
			if (request != null) {
				mPostPackage.post(request, host, false);
			}else{
				//isStop = true;
				/*printLog(file, "\n mReloginCountinRun():"+mReloginCount+"\n");*/
				if(mReloginCount>=RELOGINCOUNTTOSTOP){
					/*printLog(file, "\n"+"\n"+"\n"+"\n"+"!!!!!!!!!!程序异常user被回收，即将停止服务!!!!!!!!!!"+"\n"+"\n"+"\n"+"\n");*/
					isStop = true;
				}
			}
	
			/*StringBuffer sb = new StringBuffer();
			sb.append("\n"+"\n"+"\n"+"-----------------------"+formatter.format(new Date())+"-----------------------"+"\n"+"\n");
			sb.append("isregister:"+isRegister()+"\n");
			sb.append("user:"+mUser.getUserName()+"\n");
			sb.append("islogin:"+mUser.isLogin()+"\n");
			sb.append("logintime:"+mUser.loginTime+"\n");
			sb.append("online:"+mUser.state.online+"\n");
			sb.append("isappactive:"+isAppActive+"\n");
			sb.append("istalking:"+isTalking+"\n");
			printLog(file, sb.toString());*/
		}

		isRuning = false;
		
		
	}

	private RequestPackage getRequset() {

		// 更新Sleep时间
		mSleepStateChanged = false;
		if (false == isRegister()) {
			mUser.load();
			/*StringBuffer sb = new StringBuffer();
			sb.append("\n"+"\n"+"++++++++++"+formatter.format(new Date())+"++++++++++"+"\n");
			sb.append("reload:");*/
			if(false == isRegister()){
				/*sb.append("reload失败"+"\n");
				sb.append("++++++++++++++++++++++++++++++++++++++++++++");
				printLog(file, sb.toString());*/
				threadSleep(2000);
				return null;
			}
			/*sb.append("reload成功"+"\n");
			sb.append("++++++++++++++++++++++++++++++++++++++++++++");
			printLog(file, sb.toString());*/
		}

		
		if( isAppActive){
			if(isTalking) {
				threadSleep(SLEEP_TALKING_TIME);
			}
			else {
				threadSleep(SLEEP_ACTIVE_TIME);
			}		
		}
		else {
			threadSleep(SLEEP_NO_ACTIVE_TIME);
		}

		
		// 设置最大消息ID
		jsonGetMessage.handkey = mUser.getMaxMsgId();
		message.request = jsonGetMessage;
		
		return message;
	}

	private boolean isRegister() {

		if (mUser.getUserName().length() > 0) {
			return true;
		}
		return false;
	}


	private void updateMeetings(JsonData data) {

		MeetingInfo info = Json.fromJson(data.json, MeetingInfo.class);
		MeetingDataList list = MeetingDataList.getInstance();
		// 会议
		MeetingData meeting = list.findById(info.id);
		if (meeting == null) {
			meeting = new MeetingData(info.id, info.type, info.owner, info.name);
			list.add(meeting);
		} else {

			if (MeetingInfo.TYPE_FRIEND == (meeting.type)) {

				FriendDataList mtFriend = meeting.friendList;
				int nFriend = mtFriend.size();

				for (int i = 0; i < nFriend; i++) {
					FriendData f = mtFriend.get(i);
					mFriends.delete(f.id);
				}
				mFriends.setChanged(true);
			}
		}

		int nCount = info.items.size();
		if (nCount == 0) {
			// 删除会议
			FriendDataList mtFriend = meeting.friendList;
			int nFriend = mtFriend.size();

			for (int i = 0; i < nFriend; i++) {
				FriendData f = mtFriend.get(i);
				mFriends.delete(f.id);
			}
			mFriends.setChanged(true);

			list.delete(info.id);
		} else {

			synchronized (meeting) {

				meeting.clear();

				for (int i = 0; i < nCount; i++) {

					UserState user = info.items.get(i);
					if (UserInfo.state.id != user.id) {

						FriendData friend = mFriends.findByUserId(user.id);
						if (friend == null) {
							friend = new FriendData(user);

							mFriends.add(friend);
							friend.linkId = meeting.id;
						}

						meeting.add(friend);
					}
				}
			}
		}

		list.setChanged(true);
	}


	private void threadSleep(int nTimer) {
		
		try {
			//输出sleep时间
			/*printLog(file,  "sleeptime:"+nTimer+"\n");*/
			while (false == isStop && nTimer > 0 && (false == mSleepStateChanged)) {
				Thread.sleep(180);
				nTimer -= 200;
			}
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	public InputStream getPlayData(String arg0) {

		return null;
	}

	public void onTtsPlayEnd() {

	}

	public void onTtsPlayError(int arg0) {

	}

	public void onTtsPlayStart() {

	}

	@Override
	public void onTtsPlayLoadDataEnd() {

	}

	@Override
	public void onTtsPlayLoadDataStart() {

	}

	@Override
	public void isShowTipDialog(String msg) {
		// TODO Auto-generated method stub

	}
}
