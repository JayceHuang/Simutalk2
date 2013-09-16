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

	public final static int REQUESTCODE_AUTOCOMPLETEDWORDS = 1001;// �Զ����edittext��RequestCode
	public final static int COUNTINONEPAGE = 20;// һҳ��ʾ��������
	public final static int COUNTOFUSERINPUTHUANCUN = 30;// �����ɶ�������������

	// public final static String
	public final static String ACTION_POMENU = "com.action.popmenu";// ����pop�˵���actionname
	public final static String ACTION_SENDMSG = "com.action.getsearchcontentfromwaca";

	// ���ֶ�_words
	public final static String WORDS_ID = "words_id";
	public final static String WORDS_CHILDID = "child_id";
	public final static String WORDS_CHINESE = "chinese";
	public final static String WORDS_ENGLISH = "english";
	public final static String WORDS_HEAT = "words_heat";
	// ���ֶ�_categroy
	public final static String CATEGROY_ID = "categroy_id";
	public final static String CATEGROY_NAME = "categroy_name";
	public final static String CATEGROY_HEAT = "categroy_heat";
	// ���ֶ�_childCategroy
	public final static String CHILD_ID = "child_id";
	public final static String CHILD_CATEGROYID = "categroy_id";
	public final static String CHILD_NAME = "child_name";
	public final static String CHILD_HEAT = "child_heat";
	// ���ֶ�_collecter
	public final static String COLLECTER_ID = "id";
	public final static String COLLECTER_CHILDID = "child_id";
	public final static String COLLECTER_TEXT1 = "text1";
	public final static String COLLECTER_TEXT2 = "text2";
	public final static String COLLECTER_DATETIME = "dateTime";
	// ���ֶ�_kouyiRecord
	public final static String KOUYIRECORD_ID = "record_id";
	public final static String KOUYIRECORD_SAID = "said";
	public final static String KOUYIRECORD_TRANSLATED = "translated";
	public final static String KOUYIRECORD_DATETIME = "datetime";
	public final static String KOUYIRECORD_IDS = "id";
	public final static String KOUYIRECORD_TYPE = "type";
	public final static String KOUYIRECORD_COMMENT = "comment";
	// ���ֶ�_userinput
	public final static String USERINPUT_ID = "id";
	public final static String USERINPUT_STR = "str";

	// ����
	public final static String WORDS = "words";
	public final static String CATEGROY = "categroy";
	public final static String CHILD = "childCategroy";
	public final static String COLLECTER = "collecter";
	public final static String KOUYIRECORD = "kouyiRecord";
	public final static String USERINPUT = "userinput";

	// ���������
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
		public static final String btn_record_cancel = "ȡ��";
		public static final String btn_record_stop = "����¼��";
		public static final String btn_record_ing = "�ȴ���...";
		public static final String btn_record_say = "����˵";
		public static final String lbl_record_say = "����¼��";
		public static final String lbl_record_wait = "��ȡ����";
		public static final String lbl_net_error = "�������";
		public static final String lbl_speak_nothing = "����˵һ��";
	}

	/**
	 * ��ȡ���ݴ洢·��
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
