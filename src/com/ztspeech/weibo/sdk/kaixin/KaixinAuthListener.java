/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ztspeech.weibo.sdk.kaixin;

import android.os.Bundle;


/**
 * 认证和授权动作监听器
 */
public interface KaixinAuthListener {
	/**
	 * 授权完成
	 * 
	 * @param values
	 *            授权服务器返回的key-value形式的参数集，key为参数名，value为参数值
	 */
	public void onAuthComplete(Bundle values);

	/**
	 * 授权服务器返回错误
	 * 
	 * @param kaixinAuthError
	 *            封装服务器返回的错误信息
	 */
	public void onAuthError(KaixinAuthError kaixinAuthError);

	/**
	 * 用户取消登录
	 */
	public void onAuthCancelLogin();

	/**
	 * 用户取消授权
	 * 
	 * @param values
	 *            授权服务器返回的key-value形式的参数集，key为参数名，value为参数值
	 */
	public void onAuthCancel(Bundle values);
}
