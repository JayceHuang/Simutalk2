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
package com.ztspeech.simutalk2.weibo;

/**
 * 此类定义了组件使用的常量、异步任务返回消息的类型、进度对话框ID
 */
public class Constant {
	/**
	 * 异步任务返回结果类型
	 */
	public static final int RESULT_USER_CANCEL_PROCESS = -8;
	public static final int RESULT_FAILED_REQUEST_ERR = -7;
	public static final int RESULT_FAILED_ENCODER_ERR = -6;
	public static final int RESULT_CLASS_CAST_ERR = -5;
	public static final int RESULT_FAILED_MALFORMEDURL_ERR = -4;
	public static final int RESULT_FAILED_JSON_PARSE_ERR = -3;
	public static final int RESULT_FAILED_ARG_ERR = -2;
	public static final int RESULT_FAILED_NETWORK_ERR = -1;
	public static final int RESULT_FAILED = 0;
	public static final int RESULT_OK = 1;
	public static final int RESULT_GET_FRIENDS_OK = 2;
	public static final int RESULT_GET_USERINFO_OK = 3;
	public static final int RESULT_POST_RECORD_OK = 4;
	public static final int RESULT_POST_RECORD_FAILED = 5;

	public static final String packageName = "com.kaixin.demo";

	/**
	 * 进度对话框ID
	 */
	public static final int DIALOG_ID_DOWNLOADING = 100;
	public static final int DIALOG_ID_UPLOADING = 101;
	public static final int DIALOG_ID_GET_LOGGEDIN_USER = 105;
	public static final int DIALOG_ID_SEND_NEWSFEED = 106;
}
