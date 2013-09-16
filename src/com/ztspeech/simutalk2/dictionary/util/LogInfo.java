package com.ztspeech.simutalk2.dictionary.util;

import android.util.Log;

/**
 * 日志文件输出类
 * 
 * @author haitian
 * 
 */
public class LogInfo {
	private static final String TAG = "ztspeech";
	private static final boolean isDebug = false;

	public static void LogOut(String msg) {
		if (isDebug) {
			Log.i(TAG, msg);
		}
	}

	public static void LogOutD(String msg) {
		if (isDebug) {
			Log.d(TAG, msg);
		}
	}

	public static void LogOutE(String msg) {
		if (isDebug) {
			Log.e(TAG, msg);
		}
	}

	public static void LogOut(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	public static void LogOutD(String tag, String msg) {
		if (isDebug) {
			Log.d(tag, msg);
		}
	}

	public static void LogOutE(String tag, String msg) {
		if (isDebug) {
			Log.e(tag, msg);
		}
	}
}
