package com.ztspeech.simutalk2.dictionary.util;

import android.content.Context;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

public class Util {
	public final static String ZTSPEECH_PATH = getSdcardPath();

	public final static String DATABASE_PATH = ZTSPEECH_PATH + "/dictionary";
	public final static String DATABASE_PATH2 = ZTSPEECH_PATH + "/dictionary";
	public final static String VOICE_CACHE_PATH = ZTSPEECH_PATH + "/voiceCache/";
	public final static String IMG_CACHE_PATH = ZTSPEECH_PATH + "/imgCache/";
	public final static String DATABASE_FILENAME = "dictionary.db";

	public final static String DATABASE_FILENAME2 = "directtrans.db";

	public final static int REQUESTCODE_AUTOCOMPLETEDWORDS = 1001;// 自动完成edittext的RequestCode
	public final static int COUNTINONEPAGE = 20;// 一页显示几条数据
	public final static int COUNTOFUSERINPUTHUANCUN = 30;// 可容纳多少条缓存数据

	// public final static String
	public final static String ACTION_POMENU = "com.action.popmenu";// 弹出pop菜单的actionname
	public final static String ACTION_SENDMSG = "com.action.getsearchcontentfromwaca";

	// 表字段_words
	public final static String WORDS_ID = "words_id";
	public final static String WORDS_CHILDID = "child_id";
	public final static String WORDS_CHINESE = "chinese";
	public final static String WORDS_ENGLISH = "english";
	public final static String WORDS_HEAT = "words_heat";
	// 表字段_categroy
	public final static String CATEGROY_ID = "categroy_id";
	public final static String CATEGROY_NAME = "categroy_name";
	public final static String CATEGROY_HEAT = "categroy_heat";
	// 表字段_childCategroy
	public final static String CHILD_ID = "child_id";
	public final static String CHILD_CATEGROYID = "categroy_id";
	public final static String CHILD_NAME = "child_name";
	public final static String CHILD_HEAT = "child_heat";
	// 表字段_collecter
	public final static String COLLECTER_ID = "id";
	public final static String COLLECTER_CHILDID = "child_id";
	public final static String COLLECTER_TEXT1 = "text1";
	public final static String COLLECTER_TEXT2 = "text2";
	public final static String COLLECTER_DATETIME = "dateTime";
	// 表字段_kouyiRecord
	public final static String KOUYIRECORD_ID = "record_id";
	public final static String KOUYIRECORD_SAID = "said";
	public final static String KOUYIRECORD_TRANSLATED = "translated";
	public final static String KOUYIRECORD_DATETIME = "datetime";
	public final static String KOUYIRECORD_IDS = "id";
	public final static String KOUYIRECORD_TYPE = "type";
	public final static String KOUYIRECORD_COMMENT = "comment";
	// 表字段_userinput
	public final static String USERINPUT_ID = "id";
	public final static String USERINPUT_STR = "str";

	// 表名
	public final static String WORDS = "words";
	public final static String CATEGROY = "categroy";
	public final static String CHILD = "childCategroy";
	public final static String COLLECTER = "collecter";
	public final static String KOUYIRECORD = "kouyiRecord";
	public final static String USERINPUT = "userinput";

	// 服务器相关
	// public static String HOST_WAN_NET = "s2s.simutalk.com";
	public static String HOST_IP = "app.simutalk.com:8081";
	public static String FILE_HOST_IP = "app.simutalk.com:8081";
	public static String BTNTOHOST = "http://www.ztspeech.com";
	public static String HOST_CH_UPDATE = "http://app.simutalk.com/speechtrans/apk_version2.txt";
	public static String HELP_URL = "http://app.simutalk.com/speechtrans/help2_mobile.html";

	public static int handkey = 1;
	// =============================================================================================================
	public final static int ON_RECORD_END = 10;
	public final static int ON_RECORD_BEGIN = 11;
	public final static int ON_RECORDER_ERROR = 12;
	public final static int ON_WAIT_BEGIN = 13;
	public final static int ON_WAIT_END = 14;
	public final static int ON_RECOGNIZER_ERROR = 15;
	public final static int ON_VOICE_VALUE = 16;
	public final static int SET_LIST_VIEW = 17;
	public final static int SELECT_RESULT = 18;

	// =====================================================================
	public class R_String {
		public static final String btn_record_cancel = "取消";
		public static final String btn_record_stop = "结束录音";
		public static final String btn_record_ing = "等待中...";
		public static final String btn_record_say = "重新说";
		public static final String lbl_record_say = "正在录音";
		public static final String lbl_record_wait = "获取内容";
		public static final String lbl_net_error = "网络错误！";
		public static final String lbl_speak_nothing = "请再说一遍";
	}

	/**
	 * 获取数据存储路径
	 * 
	 * @return
	 */
	private static String getSdcardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/ztspeech/Simutalk";
	}

	private static Toast mToast;

	public static boolean isTmpFile = false;
	public static String tmpFilePath = null;

	public static void showToast(Context context, String text) {
		if (mToast != null) {
		} else {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		mToast.setText(text);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.setDuration(1);
		mToast.show();
	}

	public static void showToast(Context context, int strId) {
		String txtString = context.getString(strId);
		if (mToast != null) {
		} else {
			mToast = Toast.makeText(context, txtString, Toast.LENGTH_SHORT);
		}
		mToast.setText(txtString);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.setDuration(1);
		mToast.show();
	}
}
