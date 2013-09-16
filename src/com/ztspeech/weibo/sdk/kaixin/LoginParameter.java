package com.ztspeech.weibo.sdk.kaixin;

import android.os.Bundle;
import android.text.TextUtils;


public class LoginParameter extends BaseParameter {
	/**
	 * 应用的app id
	 */
	private String mAppId;

	/**
	 * 应用的secret key
	 */
	private String mAppSecretKey;

	/**
	 * 用户的用户名
	 */
	private String mUserName;

	/**
	 * 用户的密码，明文
	 */
	private String mPassword;

	/**
	 * 请求的权限
	 */
	private String[] mPermission;

	/**
	 * 
	 * @param userName
	 * @param password
	 * @param permissions
	 */
	public LoginParameter(String userName, String password, String[] permissions) {
		this.mUserName = userName;
		this.mPassword = password;
		this.mPermission = permissions;
		mAppId = Kaixin.API_KEY;
		mAppSecretKey = Kaixin.SECRET_KEY;
	}

	public void setUserName(String sName) {
		mUserName = sName;
	}

	public void setPassword(String sPassword) {
		mPassword = sPassword;
	}

	@Override
	public Bundle getParams() throws KaixinAuthError {
		// TODO Auto-generated method stub
		checkNullParams(mAppId, mAppSecretKey, mUserName, mPassword);

		Bundle param = new Bundle();
		param.putString("grant_type", "password");
		param.putString("username", mUserName);
		param.putString("password", mPassword);
		param.putString("client_id", mAppId);
		param.putString("client_secret", mAppSecretKey);
		if (mPermission != null && mPermission.length > 0) {
			String scope = TextUtils.join(" ", mPermission);
			param.putString("scope", scope);
		}

		return param;
	}
}
