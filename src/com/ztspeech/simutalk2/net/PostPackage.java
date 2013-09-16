package com.ztspeech.simutalk2.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import cn.ac.ia.directtrans.json.DefineType;
import cn.ac.ia.directtrans.json.JsonFunction;

public class PostPackage {

	public interface IHttpPostListener {
		public void onNetPostResult(PostPackage owner, ResultPackage result);

		public void isShowTipDialog(String msg);
	}

	private static String mDefaultHost;
	private ResultPackage mResult = new ResultPackage();
	private IHttpPostListener mListener;
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private ThreadMsgHandler mThreadMsgHandler = null;
	private boolean mNewThread = false;
	public String cmd = "";
	public String type = "";

	public static void setDefaultHost(String host) {
		mDefaultHost = host;
	}

	private void threadSendResultMsg() {

		if (mNewThread) {
			// 多线
			Message msg = mThreadMsgHandler.obtainMessage();
			Bundle b = new Bundle();
			msg.setData(b);
			msg.sendToTarget();
		} else if (mListener != null) {
			// 单线程
			mListener.onNetPostResult(this, mResult);
		}
	}

	private class ThreadMsgHandler extends Handler {

		// 使用looper来使Handler从另外一个线程中的消息队列中取得数据
		public ThreadMsgHandler(Looper lp) {
			super(lp);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (false == mResult.isNetSucceed()) {
				if (mListener != null) {
					mListener.isShowTipDialog(NetResultFlag.getString(mResult.netFlag));
				}
			}
			if (mListener != null) {
				mListener.onNetPostResult(PostPackage.this, mResult);
			}
		}
	}

	public ByteArrayOutputStream getStream() {
		return stream;
	}

	public PostPackage(Context context, IHttpPostListener lisenter) {

		mThreadMsgHandler = new ThreadMsgHandler(context.getMainLooper());

		mListener = lisenter;
	}

	public void cancel() {

		mListener = null;
	}

	public void doNetError(int nError) {

		if (mListener != null) {
			mResult.netFlag = nError;
			threadSendResultMsg();
		}
	}

	public void doNetResult(ByteArrayOutputStream result) {
		if (mListener != null) {
			mResult.netFlag = NetResultFlag.POST_DATA_SUCCEED;
			mResult.cmd = cmd;
			mResult.result = result;
			threadSendResultMsg();
		}
	}

	public boolean post(RequestPackage info, String host, boolean newThread) {

		mNewThread = newThread;
		mResult.valueLong = info.valueLong;
		mResult.valueString = info.valueString;
		cmd = info.request.function;
		type = DefineType.POST_TYPE_STR;

		return post(info.request.toJson(), host, newThread);
	}

	public boolean post(JsonFunction info, String host, boolean newThread) {

		mNewThread = newThread;
		cmd = info.function;
		type = DefineType.POST_TYPE_STR;

		return post(info.toJson(), host, newThread);
	}

	public boolean post(JsonFunction info, boolean newThread) {

		mNewThread = newThread;
		cmd = info.function;
		type = DefineType.POST_TYPE_STR;

		return post(info.toJson(), mDefaultHost, newThread);
	}

	public boolean post(JsonFunction info, String host, boolean newThread, int connectTimeout, int readTimeout) {

		mNewThread = newThread;
		cmd = info.function;
		type = DefineType.POST_TYPE_STR;

		return post(info.toJson(), host, newThread, connectTimeout, readTimeout);
	}

	public boolean post2(ByteArrayOutputStream data, String cmd, String host, boolean newThread) {
		if (data == null) {
			return false;
		}
		if (data.size() == 0) {
			return false;
		}

		this.type = DefineType.POST_TYPE_BIN;
		this.cmd = cmd;
		try {
			stream.reset();
			stream.write(data.toByteArray());
		} catch (IOException e) {

			e.printStackTrace();
		}

		mNewThread = newThread;
		HttpPostData p = new HttpPostData();
		p.connectTimeout = NetDefine.HTTP_CONNECT_TIMEOUT;
		p.readTimeout = NetDefine.HTTP_READ_TIMEOUT;

		p.setHost(host);
		if (newThread) {
			p.threadPost(this);
		} else {
			p.post(this);
		}
		return true;
	}

	/**
	 * 提交到后台服务器数据
	 * 
	 * @param json
	 * @param host
	 * @param newThread
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 */
	public boolean post(String json, String host, boolean newThread, int connectTimeout, int readTimeout) {

		if (json == null) {
			return false;
		}
		if (json.length() == 0) {
			return false;
		}

		try {
			json = java.net.URLEncoder.encode(json, "UTF-8");
			stream.reset();
			stream.write(json.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		HttpPostData p = new HttpPostData();
		p.connectTimeout = connectTimeout;
		p.readTimeout = readTimeout;

		p.setHost(host);
		if (newThread) {
			p.threadPost(this);
		} else {
			p.post(this);
		}
		return true;
	}

	public boolean post(String json, String host, boolean newThread) {

		return post(json, host, newThread, NetDefine.HTTP_CONNECT_TIMEOUT, NetDefine.HTTP_READ_TIMEOUT);
	}

	public void setValueLong(long value) {

		mResult.valueLong = value;
	}

}
