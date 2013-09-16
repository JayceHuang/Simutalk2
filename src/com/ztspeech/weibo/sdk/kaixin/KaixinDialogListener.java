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
 * KaixinDialog事件监听器
 */
public interface KaixinDialogListener {

	/**
	 * 未处理
	 */
	public final static int UNPROCCESS = 0;

	/**
	 * 已处理
	 */
	public final static int PROCCESSED = 1;

	/**
	 * 由Dialog处理
	 */
	public final static int DIALOG_PROCCESS = 2;

	/**
	 * 页面加载之前调用。
	 * 
	 * @param url
	 * @return 0:未处理，1:已经处理，2:由Dialog处理
	 */
	public int onPageBegin(String url);

	/**
	 * 页面开始加载时调用
	 * 
	 * @param url
	 * @return
	 */
	public boolean onPageStart(String url);

	/**
	 * 页面加载结束后调用
	 * 
	 * @param url
	 */
	public void onPageFinished(String url);

	/**
	 * 出现错误调用
	 * 
	 * @param errorCode
	 * @param description
	 * @param failingUrl
	 */
	public void onReceivedError(int errorCode, String description,
			String failingUrl);
}
