package com.ztspeech.simutalk2.net;

import android.content.Context;

public class ServerConnect {

	@SuppressWarnings("unused")
	private String mHost;
	@SuppressWarnings("unused")
	private Context mContext;
	private static ServerConnect mInstance;

	public static ServerConnect getInstance() {
		if (mInstance == null) {
			mInstance = new ServerConnect();
		}
		return mInstance;
	}

	public void init(Context context, String host) {
		mHost = host;
		mContext = context;
	}

	public boolean setUserPhotoId(String id) {

		return false;
	}

	public boolean setUserName(String name) {

		return false;
	}

}
