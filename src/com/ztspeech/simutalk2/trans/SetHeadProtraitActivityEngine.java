package com.ztspeech.simutalk2.trans;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.net.HttpBaseEngine;

public class SetHeadProtraitActivityEngine extends HttpBaseEngine {
	private Handler handler;

	public SetHeadProtraitActivityEngine(Context context, Handler handler) {
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
			if (!isCancel) {
				handler.sendMessage(Message.obtain(handler, 404, context.getString(R.string.qa_msg_download_error)));
				// Toast.makeText(context,
				// context.getString(R.string.qa_msg_download_error),
				// Toast.LENGTH_SHORT).show();
				// return;
			} else {
				handler.sendMessage(Message.obtain(handler, 2));
			}
		} else if (result != null && !isCancel) {
			handler.sendMessage(Message.obtain(handler, 0, result));
		}
	}

}
