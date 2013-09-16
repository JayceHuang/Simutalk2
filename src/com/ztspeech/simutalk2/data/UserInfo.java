package com.ztspeech.simutalk2.data;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import cn.ac.ia.directtrans.json.Json;
import cn.ac.ia.directtrans.json.JsonGetMessage;
import cn.ac.ia.directtrans.json.JsonUserConfig;
import cn.ac.ia.directtrans.json.UserState;

public class UserInfo extends DBTable {

	public static final String S2T_CH2EN = "CH2EN";
	public static final String S2T_EN2CH = "EN2CH";
	public static final String S2T_LETTER = "LETTER";
	public static final String appId = "drt";

	public UserInfo() {

		tableName = " user_info ";
	}

	private static UserInfo mInstanse = null;

	public String getUserName() {
		synchronized (this) {
			return state.name;
		}
	}

	public void setUserName(String userName) {
		synchronized (this) {
			state.name = userName;
		}
		UserInfoList.getInstanse().update(state);
	}

	/**
	 * 保存当前应用程序版本号 2012-11-20 kjzhang
	 */
	public static String version = "";

	public boolean autoTTS = true;
	public String s2sType = S2T_CH2EN;
	private String hostVersion = "";
	private JsonUserConfig jsonData;

	public BitmapDrawable photo;
	private boolean isLoginState = false;
	public String param0 = "";
	public static UserState state = new UserState();
	public boolean isChange = false;
	public long loginTime = 0;

	public static UserInfo getInstanse() {

		if (mInstanse == null) {

			mInstanse = new UserInfo();
			state.time = new Date();
		}

		return mInstanse;
	}

	public boolean isLogin() {
		return isLoginState;
	}

	public void setBackRun(boolean parseBoolean) {

		jsonData.bRun = parseBoolean;
		save();
	}

	public void setLClickRecord(boolean parseBoolean){
		jsonData.lClickRecord = parseBoolean;
		save();
	}
	
	/**
	 * 是否长按录音
	 * @return
	 */
	public boolean isLClickRecord(){
		return jsonData.lClickRecord;
	}
	/**
	 * 程序是否后台运行
	 * 
	 * @return
	 */
	public boolean isBackRun() {

		return jsonData.bRun;
	}

	/**
	 * 程序启动时是否直接打开翻译界面
	 * 
	 * @return
	 */
	public boolean isOpenTransView() {
		return (jsonData.openTrans == 1);
	}

	/**
	 * 设置程序启动时直接打开翻译界面
	 * 
	 * @param open
	 */
	public void setOpenTransView(boolean open) {
		jsonData.openTrans = open ? 1 : 0;
		save();
	}

	public void setLogin(boolean b) {
		isLoginState = b;
		isChange = true;
	}

	public void setChange(boolean change) {
		isChange = change;
	}

	/**
	 * 最大消息ID
	 * 
	 * @return
	 */
	public long getMaxMsgId() {

		return jsonData.maxMsg;
	}

	/**
	 * 保存最大消息ID
	 */
	public void setMaxMsgId(long maxId) {

		jsonData.maxMsg = maxId;
		save();
	}

	public float getTtsSpeed() {

		return jsonData.TTSSpeed;
	}

	public void setTtsSpeed(float v) {

		jsonData.TTSSpeed = v;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(dataid integer primary key autoincrement, "
				+ "name varchar(50), autoplay integer," + "language varchar(4), host_version varchar(1024),"
				+ "update_flag varchar(1024), param0 varchar(256),  " + "param11 varchar(256),"
				+ "param1 varchar(1024), param2 varchar(1024))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// db.execSQL("DROP TABLE IF EXISTS "+ tableName);
		// onCreate(db);
	}

	public void load() {

		SQLiteDatabase reader = this.getReadableDatabase();
		Cursor cursor = reader.rawQuery("select name,autoplay,language,host_version,param1 from " + tableName, null);

		if (cursor != null) {
			if (cursor.moveToNext()) {

				int i = 0;
				state.name = cursor.getString(i++);

				autoTTS = true;
				if (cursor.getInt(i++) == 0) {
					autoTTS = false;
				}

				if (UserInfo.S2T_EN2CH.equals(cursor.getString(i++))) {
					s2sType = UserInfo.S2T_EN2CH;
				}

				hostVersion = cursor.getString(i++);
				String json = cursor.getString(i++);
				jsonData = null;
				if (json != null) {
					if (json.length() > 5) {
						jsonData = Json.fromJson(json, JsonUserConfig.class);
						state.id = jsonData.id;
						state.photo = jsonData.photo;
					}
				}
			}
			cursor.close();
		}

		if (jsonData == null) {
			jsonData = new JsonUserConfig();
			jsonData.maxMsg = JsonGetMessage.MIN_ID;
		}
	}

	public boolean isLocaleTTS() {
		return jsonData.localeTTS;
	}

	public float getFontSize() {
		float size = 20;

		switch (jsonData.fontSize - 1) {
		case 0:
			size = 15;
			break;
		case 1:
			size = 18;
			break;
		case 2:
			size = 25;
			break;
		case 3:
			size = 30;
			break;
		}

		return size;
	}

	public boolean getTtsGender() {
		return jsonData.ttsGender;
	}

	public void setTtsGender(boolean gender) {
		jsonData.ttsGender = gender;
	}

	public int getFontIndex() {
		return jsonData.fontSize;
	}

	public void setFontIndex(int fontSize) {
		jsonData.fontSize = fontSize;
	}

	public void setPhoto(String id) {
		state.photo = id;
		jsonData.photo = id;
		UserInfoList.getInstanse().update(state);
	}

	public void setLocaleTTS(boolean localeTTS) {
		jsonData.localeTTS = localeTTS;
	}

	public boolean isOnlyRecoginze() {
		return jsonData.onlyRecoginze;
	}

	public void setOnlyRecoginze(boolean onlyRecoginze) {
		jsonData.onlyRecoginze = onlyRecoginze;
	}
	
	public boolean isTranslatetalk(){
		return jsonData.translatetalk;
	}
	
	public void setTranslatetalk(boolean translatetalk){
		jsonData.translatetalk = translatetalk;
	}
	
	public void save() {

		SQLiteDatabase writer = getWritableDatabase();
		writer.delete(tableName, "1=1", null);
		ContentValues cv = new ContentValues();
		cv.put("name", state.name);
		cv.put("autoplay", autoTTS ? 1 : 0);
		cv.put("language", s2sType);
		cv.put("host_version", hostVersion);
		cv.put("param1", jsonData.toJson());
		writer.insert(tableName, null, cv);
	}

	public String getTimeString1(Date date) {

		Date loginTime = state.time;
		String ret = "";
		// year
		int temp = loginTime.getYear() - date.getYear();
		switch (temp) {
		case 0:
			break;
		case 1:
			ret = "去年  ";
			break;
		case 2:
			ret = "前年  ";
			break;
		default:
			ret = String.format("%02d-", date.getYear());
		}
		// mouth
		if (ret.length() == 0) {
			temp = loginTime.getMonth() - date.getMonth();
			switch (temp) {
			case 0:
				break;
			case 1:
				ret = "上个月  ";
				break;
			default:
				ret = String.format("%02d-", date.getMonth() + 1);
			}
		} else {
			ret += String.format("%02d-", date.getMonth() + 1);
		}

		// day
		if (ret.length() == 0) {
			temp = loginTime.getDay() - date.getDay();
			switch (temp) {
			case 0:
				break;
			case 1:
				ret = "昨天  ";
				break;
			case 2:
				ret = "前天  ";
				break;
			default:
				ret = String.format("%02d-%02d  ", date.getMonth() + 1, date.getDay());
			}
		} else {
			ret += String.format("%02d  ", date.getDay());
		}

		ret += String.format("%02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());
		return ret;
	}

	public static String getTimeString(long time) {
		return getTimeString(new Date(time));
	}

	public static String getTimeString(Date date) {
		if (date == null) {
			return "";
		}
		// Date date = new Date( new Timestamp(time).getTime());
		// Date date = new Date(time);
		Date loginTime = state.time;
		// String sTime = TimeString.getTimeString("yyyyMMddHHmmss", date);
		String ret = "";
		// year
		int temp = loginTime.getYear() - date.getYear();
		switch (temp) {
		case 0:
			break;
		case 1:
			ret = "去年  ";
			break;
		case 2:
			ret = "前年  ";
			break;
		default:
			ret = String.format("%d年", date.getYear());
		}
		// mouth
		if (ret.length() == 0) {
			temp = loginTime.getMonth() - date.getMonth();
			switch (temp) {
			case 0:
				break;
			default:
				ret = String.format("%d月", date.getMonth() + 1);
			}
		} else {
			ret += String.format("%d月", date.getMonth() + 1);
		}

		// day
		if (ret.length() == 0) {
			temp = loginTime.getDate() - date.getDate();
			switch (temp) {
			case 0:
				ret = "今天  ";
				break;
			case 1:
				ret = "昨天  ";
				break;
			case 2:
				ret = "前天  ";
				break;
			default:
				ret = String.format("%d月%d日  ", date.getMonth() + 1, date.getDate());
			}
		} else {
			ret += String.format("%d日  ", date.getDate());
		}

		ret += String.format("%02d:%02d:%02d", date.getHours(), date.getMinutes(), date.getSeconds());
		return ret;
	}

	public void setInfo(UserState state2) {

		isChange = true;
		state.setInfo(state2);
		jsonData.id = state.id;
		loginTime = state.time.getTime();

		UserInfoList.getInstanse().update(state);
		save();
	}

	public static String getSbxLen(int vLen) {

		return String.format("%d秒", vLen / 3500);
	}

	public void setMaxIdToMsg() {

		if (jsonData.maxMsg == JsonGetMessage.MIN_ID) {
			jsonData.maxMsg = JsonGetMessage.LOST_MAX_ID;
		}

		// jsonData.maxMsg =JsonGetMessage.MIN_ID ; // debug
	}

}
