package com.ztspeech.weibo.sdk.android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.CookieSyncManager;

import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.weibo.sdk.android.util.Utility;

/**
 * 
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public class Weibo {
	private static final String WEIBO_SDK_VERSION = "2.0";
	public static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";// "https://open.weibo.cn/oauth2/authorize";

	private static Weibo mWeiboInstance = null;
	private static String app_key = "1459196177";
	private static String app_secret = "728edc660a6377c80cf7c1945de99ccd";

	public static String redirecturl = "http://www.sina.com";// "ztspeechAndroid://OAuthActivity";//
																// 重定向url

	public Oauth2AccessToken accessToken = null;// AccessToken实例

	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expires_in";
	public static final String KEY_REFRESHTOKEN = "refresh_token";
	public static boolean isWifi = false;// 当前是否为wifi

	/**
	 * 
	 * @param appKey
	 *            第三方应用的appkey
	 * @param redirectUrl
	 *            第三方应用的回调页
	 * @return Weibo的实例
	 */
	public synchronized static Weibo getInstance(String appKey, String redirectUrl) {
		if (mWeiboInstance == null) {
			mWeiboInstance = new Weibo();
		}
		app_key = appKey;
		Weibo.redirecturl = redirectUrl;
		return mWeiboInstance;
	}

	/**
	 * 设定第三方使用者的appkey和重定向url
	 * 
	 * @param appKey
	 *            第三方应用的appkey
	 * @param redirectUrl
	 *            第三方应用的回调页
	 */
	public void setupConsumerConfig(String appKey, String redirectUrl) {
		app_key = appKey;
		redirecturl = redirectUrl;
	}

	/**
	 * 
	 * 进行微博认证
	 * 
	 * @param activity
	 *            调用认证功能的Context实例
	 * @param listener
	 *            WeiboAuthListener 微博认证的回调接口
	 */
	public void authorize(Context context, WeiboAuthListener listener, boolean isBinder) {
		isWifi = Utility.isWifi(context);
		startAuthDialog(context, listener, isBinder);
	}

	public void startAuthDialog(Context context, final WeiboAuthListener listener, boolean isBinder) {
		WeiboParameters params = new WeiboParameters();
		// CookieSyncManager.createInstance(context);
		startDialog(context, params, new WeiboAuthListener() {
			@Override
			public void onComplete(Bundle values) {
				// ensure any cookies set by the dialog are saved
				CookieSyncManager.getInstance().sync();
				if (null == accessToken) {
					accessToken = new Oauth2AccessToken();
				}
				accessToken.setToken(values.getString(KEY_TOKEN));
				accessToken.setExpiresIn(values.getString(KEY_EXPIRES));
				accessToken.setRefreshToken(values.getString(KEY_REFRESHTOKEN));
				if (accessToken.isSessionValid()) {
					LogInfo.LogOut(
							"Weibo-authorize",
							"Login Success! access_token=" + accessToken.getToken() + " expires="
									+ accessToken.getExpiresTime() + " refresh_token=" + accessToken.getRefreshToken());
					listener.onComplete(values);
				} else {
					LogInfo.LogOut("Weibo-authorize", "Failed to receive access token");
					listener.onWeiboException(new WeiboException("Failed to receive access token."));
				}
			}

			@Override
			public void onError(WeiboDialogError error) {
				LogInfo.LogOut("Weibo-authorize", "Login failed: " + error);
				listener.onError(error);
			}

			@Override
			public void onWeiboException(WeiboException error) {
				LogInfo.LogOut("Weibo-authorize", "Login failed: " + error);
				listener.onWeiboException(error);
			}

			@Override
			public void onCancel() {
				LogInfo.LogOut("Weibo-authorize", "Login canceled");
				listener.onCancel();
			}
		}, isBinder);
	}

	public void startDialog(Context context, WeiboParameters parameters, final WeiboAuthListener listener,
			boolean isBinder) {
		parameters.add("client_id", app_key);
		parameters.add("response_type", "token");
		parameters.add("redirect_uri", redirecturl);
		parameters.add("display", "mobile");

		if (accessToken != null && accessToken.isSessionValid()) {
			parameters.add(KEY_TOKEN, accessToken.getToken());
		}
		String url = URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Utility.showAlert(context, "Error", "Application requires permission to access the Internet");
		} else {
			new WeiboDialog(context, url, listener).show();

		}
	}

}
