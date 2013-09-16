package com.ztspeech.simutalk2.weibo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.MD5;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class Json {
	JSONObject root = null;

	/**
	 * 构建带默认参数的json
	 * 
	 * @param apiVersion
	 */
	public Json(int apiVersion) {
		try {
			JSONObject common = new JSONObject(Util.COLLECTER_DATETIME);
			common.put("apiversion", apiVersion);
			root = new JSONObject();
			root.put("common", common);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建指定json
	 * 
	 * @param jsonString
	 */
	public Json(String jsonString) {
		try {
			root = new JSONObject(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析json时,构建指定json
	 * 
	 * @param jsonObject
	 */
	public Json(JSONObject jsonObject) {
		root = jsonObject;
	}

	/**
	 * 构建无数据的json
	 */
	public Json() {
		root = new JSONObject();
	}

	public boolean put(String key, int value) {
		try {
			root.put(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean put(String key, Object value) {
		try {
			root.put(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String toString() {
		if (root == null) {
			return null;
		}
		String rootString = root.toString();
		LogInfo.LogOut("before encode json=" + rootString);
		rootString = XmlBase64.encode(rootString.getBytes());
		// return
		// "?request="+rootString+"&sign="+MD5.md5Lower(rootString+"xinhuashe")+"&type=xhs";
		return "?request=" + rootString + "&sign="
				+ MD5.md5Lower(rootString + "5b889421cb1e4889bc56461336a1c440fd56f7fd811b4108a96eb40b3b258aa8")
				+ "&type=006739a373b9490a";
	}

	public String toTestString() {
		if (root == null) {
			return null;
		}
		String rootString = root.toString();
		LogInfo.LogOut("before encode json=" + rootString);
		rootString = XmlBase64.encode(rootString.getBytes());
		return "?request=" + rootString + "&sign="
				+ MD5.md5Lower(rootString + "1731c73ef747457e8ac6f2ddb7de9227087e337ee96b4545b71edd50ea79d367")
				+ "&type=52f78ffbda1e416e";
	}

	public String toNormalString() {
		if (root == null) {
			return null;
		}
		return root.toString();
	}

	public String getString(String key) {
		try {
			return root.get(key).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getInt(String key) {
		try {
			return root.getInt(key);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean getBoolean(String key) {
		try {
			return root.getBoolean(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Json getJson(String key) {
		try {
			return new Json(root.getJSONObject(key));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Json[] getJsonArray(String key) {
		Json[] jsons;
		try {
			JSONArray a = root.getJSONArray(key);
			jsons = new Json[a.length()];
			for (int i = 0; i < jsons.length; i++) {
				jsons[i] = new Json(a.getJSONObject(i));
			}
			return jsons;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String toStringForTest() {
		if (root == null) {
			return null;
		}
		String rootString = root.toString();
		LogInfo.LogOut("before encode json=" + rootString);
		rootString = XmlBase64.encode(rootString.getBytes());
		return "?request=" + rootString + "&sign="
				+ MD5.md5Lower(rootString + "1731c73ef747457e8ac6f2ddb7de9227087e337ee96b4545b71edd50ea79d367")
				+ "&type=52f78ffbda1e416e";
	}
}
