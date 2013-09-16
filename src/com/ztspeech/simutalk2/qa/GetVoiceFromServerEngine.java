package com.ztspeech.simutalk2.qa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ztspeech.recognizer.speak.VoicePlayer;
import com.ztspeech.simutalk2.data.VoiceData;
import com.ztspeech.simutalk2.net.HttpBaseEngine;

public class GetVoiceFromServerEngine extends HttpBaseEngine {
	private Handler handler;
	private VoicePlayer mPlayer = null;

	public GetVoiceFromServerEngine(Context context, VoicePlayer mPlayer, Handler handler) {
		super(context);
		this.handler = handler;
		this.mPlayer = mPlayer;
	}

	@Override
	public void onPreHttp() {
		showLoading();
	}

	@Override
	public void onParseHttp(byte[] response) {
	}

	@Override
	public void onPostHttp(Object result) {
		dismissLoading();

		if (result == null) {
			if (!isCancel) {
				handler.sendMessage(Message.obtain(handler, 2));// 返回结果为空
			} else {
				handler.sendMessage(Message.obtain(handler, 0));// 用户取消网络
			}
		} else if (result != null && !isCancel) {
			byte[] tmpResult = (byte[]) result;
			boolean flag = VoiceData.isVoice(tmpResult.length);
			if (flag) {
				InputStream s = new ByteArrayInputStream(tmpResult);
				if (mPlayer != null) {
					mPlayer.play(s);
					handler.sendMessage(Message.obtain(handler, 3));
				} else {
					handler.sendMessage(Message.obtain(handler, 1));// 播放器未初始化（播放出错）
				}
			} else {
				handler.sendMessage(Message.obtain(handler, 2));// 数据下载错误
			}
		}

	}
}
