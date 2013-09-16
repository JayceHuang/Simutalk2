package com.ztspeech.simutalk2.qa;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.net.HttpBaseEngine;

public class PostVoiceDataToServerEngine extends HttpBaseEngine {
	private Handler handler;

	public PostVoiceDataToServerEngine(Context context, Handler handler) {
		super(context);
		this.handler = handler;
	}

	@Override
	public void onPreHttp() {
		showLoading();
	}

	@Override
	public void onParseHttp(String response) {
	}

	@Override
	public void onPostHttp(Object result) {
		// dismissLoading();
		if (result == null) {
			dismissLoading();
			if (!isCancel) {
				handler.sendMessage(Message.obtain(handler, 101));// 返回结果为空
			} else {
				handler.sendMessage(Message.obtain(handler, 100));// 用户取消网络
			}
		} else if (result != null && !isCancel) {
			LogInfo.LogOutE("haitian", ">>>>>>>>>>>>>>>>>>> result =" + (String) result);
			handler.sendMessage(Message.obtain(handler, 102, result));
		}

	}
}
