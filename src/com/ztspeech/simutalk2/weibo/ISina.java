package com.ztspeech.simutalk2.weibo;

import java.io.IOException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.weibo.sdk.android.Oauth2AccessToken;
import com.ztspeech.weibo.sdk.android.Weibo;
import com.ztspeech.weibo.sdk.android.WeiboAuthListener;
import com.ztspeech.weibo.sdk.android.WeiboDialogError;
import com.ztspeech.weibo.sdk.android.WeiboException;
import com.ztspeech.weibo.sdk.android.api.StatusesAPI;
import com.ztspeech.weibo.sdk.android.net.RequestListener;

public class ISina {

	private static ISina iSina = null;
	// private static String APP_KEY = "117304354";
	private static String APP_KEY = "1459196177";
	private static String APP_SECRET = "728edc660a6377c80cf7c1945de99ccd";
	private static String REDIRECT_URL = "http://www.sina.com";

	private SharedPreferences sp;

	private Context mContext;
	private Weibo mWeibo = null;
	private Oauth2AccessToken o2at;
	private Handler handler;
	private Handler mHandler;
	private String contentStr = null;

	public ISina(Context context) {
		mContext = context;
		setHandler(mContext);
	}

	private void setHandler(Context context) {
		Looper mainLooper = context.getMainLooper();
		handler = new Handler(mainLooper) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 200:
					Util.showToast(mContext, "授权成功");
					break;
				case 201:
					Util.showToast(mContext, (String) msg.obj);
					break;
				case 202:
					if(contentStr==null){
						Util.showToast(mContext, (String) msg.obj);
					}else{
						sendWeibo();
					}
					break;
				case 400:
					Util.showToast(mContext, "授权失败");
					break;
				case 401:
					Util.showToast(mContext, (String) msg.obj);
					break;
				default:
					break;
				}
			}
		};
	}

	public synchronized static ISina getInstance(Context context) {
		if (iSina == null) {
			iSina = new ISina(context);
		}
		return iSina;
	}

	public void setClass() {
		iSina = null;
	}

	public void setO2at() {
		o2at = null;
	}

	public void init() {
		sp = mContext.getSharedPreferences("sina", 0);
		String isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			try {
				o2at = new Oauth2AccessToken(sp.getString("ACCESS_TOKEN", ""), sp.getString("EXPIRES_IN", ""));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			o2at = null;
		}
	}

	public void bindSina(Context context, Handler _mHandler) {
		mContext = context;
		setHandler(mContext);
		mHandler = _mHandler;
		mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URL);
		mWeibo.authorize(mContext, new AuthDialogListener() {
			@Override
			public void onComplete(Bundle arg0) {
				String token = arg0.getString("access_token");
				String expires_in = arg0.getString("expires_in");
				o2at = new Oauth2AccessToken(token, expires_in);
				if (o2at.isSessionValid()) {
					@SuppressWarnings("unused")
					String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(o2at
							.getExpiresTime()));
					sp.edit().putString("ACCESS_TOKEN", token).putString("EXPIRES_IN", expires_in)
							.putString("isBind", "yes").commit();
				}
				mHandler.sendMessage(Message.obtain(mHandler, 201, "授权成功"));
				mHandler = null;
			}
		}, false);
	}

	class AuthDialogListener implements WeiboAuthListener {
		@Override
		public void onCancel() {

		}

		@Override
		public void onComplete(Bundle arg0) {
			String token = arg0.getString("access_token");
			String expires_in = arg0.getString("expires_in");
			o2at = new Oauth2AccessToken(token, expires_in);
			if (o2at.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(o2at
						.getExpiresTime()));
				LogInfo.LogOut("haitian", "认证成功！\raccess_token:" + token + "\rexpires_in:" + expires_in + "/\r有效期至："
						+ date);
				sp.edit().putString("ACCESS_TOKEN", token).putString("EXPIRES_IN", expires_in)
						.putString("isBind", "yes").commit();
				handler.sendMessage(Message.obtain(handler, 201, "授权成功"));
			}

		}

		@Override
		public void onError(WeiboDialogError arg0) {
			handler.sendMessage(Message.obtain(handler, 401, "授权失败"));
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}
	}

	public void sendWeibo() {

		StatusesAPI api = new StatusesAPI(o2at);
		api.update(contentStr, "", "", new RequestListener() {

			@Override
			public void onIOException(IOException arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(WeiboException arg0) {
				handler.sendMessage(Message.obtain(handler, 401, "发表失败"));
				LogInfo.LogOut("haitian",
						"Util.showToast(context, \"发表失败\", Toast.LENGTH_SHORT).show()    " + arg0.getMessage());
			}

			@Override
			public void onComplete(String arg0) {
				handler.sendMessage(Message.obtain(handler, 201, "发表成功"));
				LogInfo.LogOut("haitian", "Util.showToast(context, \"发表成功\", Toast.LENGTH_SHORT).show()    " + arg0);
				contentStr = null;
			}
		});

	}

	public void sendWeibo(String mcontentStr, Context context) {
		mContext = context;
		setHandler(mContext);
		contentStr = mcontentStr;
		if (o2at == null) {
			mWeibo = Weibo.getInstance(APP_KEY, REDIRECT_URL);
			mWeibo.authorize(mContext, new AuthDialogListener() {
				@Override
				public void onComplete(Bundle arg0) {
					String token = arg0.getString("access_token");
					String expires_in = arg0.getString("expires_in");
					o2at = new Oauth2AccessToken(token, expires_in);
					if (o2at.isSessionValid()) {
						@SuppressWarnings("unused")
						String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(o2at
								.getExpiresTime()));
						sp.edit().putString("ACCESS_TOKEN", token).putString("EXPIRES_IN", expires_in)
								.putString("isBind", "yes").commit();
					}
					handler.sendMessage(Message.obtain(handler, 202, "授权成功"));
				}
			}, false);
		} else {
			StatusesAPI api = new StatusesAPI(o2at);
			handler.sendMessage(Message.obtain(handler, 201, "微博已发送"));
			api.update(contentStr, "", "", new RequestListener() {

				@Override
				public void onIOException(IOException arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onError(WeiboException arg0) {
					handler.sendMessage(Message.obtain(handler, 401, "发表失败"));
					LogInfo.LogOut("haitian",
							"Util.showToast(context, \"发表失败\", Toast.LENGTH_SHORT).show()    " + arg0.getMessage());
				}

				@Override
				public void onComplete(String arg0) {
					handler.sendMessage(Message.obtain(handler, 201, "发表成功"));
					LogInfo.LogOut("haitian", "Util.showToast(context, \"发表成功\", Toast.LENGTH_SHORT).show()    " + arg0);
				}
			});
		}
	}

	public boolean isBind(){
		sp = mContext.getSharedPreferences("sina", 0);
		String isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			return true;
		}else{
			return false;
		}
	}
}
