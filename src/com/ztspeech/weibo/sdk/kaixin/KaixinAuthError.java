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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 封装授权错误信息的类
 *
 */
public class KaixinAuthError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String mError;

	private String mErrorDescription;

	private String mErrorUri;

	public KaixinAuthError(String error, String errorDescription,
			String errorUri) {
		super(errorDescription);
		mError = error;
		mErrorDescription = errorDescription;
		mErrorUri = errorUri;
	}

	public JSONObject getJSONObjectError() {
		return genJSONObjectError(mError, mErrorDescription, mErrorUri);
	}

	public static JSONObject genJSONObjectError(String error,
			String errorDescription, String errorUri) {
		if (error == null)
			error = "";
		if (errorUri == null)
			errorUri = "";
		if (errorDescription == null)
			errorDescription = "";
		JSONObject errorObj = new JSONObject();
		try {
			errorObj.put("error", error);
			errorObj.put("error_uri", errorUri);
			errorObj.put("error_description", errorDescription);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return errorObj;
	}

	public String getError() {
		return mError;
	}

	public String getErrorDescription() {
		return mErrorDescription;
	}

	public String getErrorUri() {
		return mErrorUri;
	}
}
