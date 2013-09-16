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


/**
 * 异步网络请求动作监听器
 */
public interface AsyncKaixinListener {
	/**
	 * 请求完成
	 * 
	 * @param response
	 *            服务器返回的JSON串
	 * @param obj
	 *            调用异步请求时设置的关联的数据
	 * @see AsyncKaixin
	 */
	public void onRequestComplete(String response, Object obj);

	/**
	 * 服务器返回错误信息
	 * 
	 * @param kaixinError
	 *            封装服务器回的错误信息
	 * @param obj
	 *            调用异步请求时设置的关联的数据
	 * @see AsyncKaixin
	 */
	public void onRequestError(KaixinError kaixinError, Object obj);

	/**
	 * 请求过程中发生了错误
	 * 
	 * @param fault
	 *            请求过程中抛出的异常
	 * @param obj
	 *            调用异步请求时设置的关联的数据
	 * @see AsyncKaixin
	 */
	public void onRequestNetError(Throwable fault, Object obj);
}
