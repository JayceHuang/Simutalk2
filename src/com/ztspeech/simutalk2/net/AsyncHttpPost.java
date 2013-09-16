package com.ztspeech.simutalk2.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.os.AsyncTask;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class AsyncHttpPost extends AsyncTask<String, String, String> {
	private static String cachePath = Util.VOICE_CACHE_PATH;
	private AsyncHttpPostLisenter mLisenter = null;
	private String host = "";
	private String app = "";
	private String userId = "";
	private static String _Cookie = "";
	private static Object _synch = new Object();
	private byte[] voice;

	@Override
	protected void onPostExecute(String result) {

		super.onPostExecute(result);

		mLisenter.onEnd();
		mLisenter.onData(result);

	}

	public interface AsyncHttpPostLisenter {
		public void onData(String fileId);

		public void onBegin();

		public void onEnd();
	}

	public AsyncHttpPost(AsyncHttpPostLisenter lisenter) {
		mLisenter = lisenter;
	}

	public void setHost(String sHost, String app, String userId) {
		// host = "http://" + sHost + "/filesservlet";
		host = "http://" + sHost + "/FilesServer/filesservlet";

		this.userId = userId;
		this.app = app;
	}

	public void postVoice(byte[] bytes, String type) {

		voice = bytes;
		mLisenter.onBegin();
		this.execute(type);
	}

	private void writeLog(String sLog) {
		LogInfo.LogOut("trans", "post " + sLog);
	}

	private String postData(String type) {

		HttpURLConnection httpConn = null;
		String result = null;
		try {

			URL url = new URL(host);
			httpConn = (HttpURLConnection) url.openConnection();
			if (httpConn == null) {
				return null;
			}

			// 设置连接属性
			httpConn.setConnectTimeout(NetDefine.HTTP_CONNECT_TIMEOUT);
			httpConn.setReadTimeout(NetDefine.HTTP_READ_TIMEOUT);
			httpConn.setDoOutput(true); // 使用 URL 连接进行输出
			httpConn.setDoInput(true); // 使用 URL 连接进行输入
			httpConn.setUseCaches(false); // 忽略缓存
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty(RequestParam.APP, app);
			httpConn.setRequestProperty(RequestParam.TYPE, type);
			httpConn.setRequestProperty(RequestParam.USER_ID, userId);
			httpConn.setRequestProperty("Charset", "UTF-8");
			httpConn.setRequestProperty("Cookie", getCookie());

			httpConn.connect();
			writeLog("post data size=" + voice.length);
			httpConn.getOutputStream().write(voice);
			httpConn.getOutputStream().flush();

			int nResponse = httpConn.getResponseCode();
			if (nResponse == HttpStatus.SC_OK) {

				String key = "";
				String cookie = "";
				for (int i = 1; (key = httpConn.getHeaderFieldKey(i)) != null; i++) {
					if (key.equalsIgnoreCase("set-cookie")) {
						cookie = httpConn.getHeaderField(key);
						cookie = cookie.substring(0, cookie.indexOf(";"));
					}
				}

				if (cookie.length() > 10) {
					setCookie(cookie);
				}

				InputStream is = httpConn.getInputStream();
				if (is != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					result = "";
					String s = null;

					while ((s = br.readLine()) != null) {
						result += s;
					}
					is.close();
					is = null;
					br = null;
				}
			}
		} catch (ProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (httpConn != null) {
			httpConn.disconnect();
			httpConn = null;
		}

		return result;
	}

	private String getCookie() {
		synchronized (_synch) {
			return _Cookie;
		}
	}

	private void setCookie(String cookie) {
		synchronized (_synch) {
			_Cookie = cookie;
		}
	}

	@Override
	protected String doInBackground(String... params) {
		String result = postData((String) params[0]);
		if (result != null) {
			add(result, voice, (String) params[0]);
		}
		return result;
	}

	private void add(String id, byte[] voice, String type) {
		synchronized (this) {
			saveVoiceData(id, voice, type);
		}
	}

	/**
	 * 将语音流数据存入本地文件
	 * 
	 * @param id
	 * @param voice
	 * @return
	 */
	private boolean saveVoiceData(String id, byte[] voice, String type) {
		if (id == null || "".equals(id.trim()) || voice.length <= 0) {
			return false;
		}
		FileOutputStream fileOutputStream = null;
		try {
			File dir = null;
			File temp = null;
			String fileExt = ".dat";
			if (RequestParam.FILE_TYPE_VOICE.equals(type)) {
				cachePath = Util.VOICE_CACHE_PATH;
				fileExt = ".dat";
			} else if (RequestParam.FILE_TYPE_PHOTO.equals(type)) {
				cachePath = Util.IMG_CACHE_PATH;
				fileExt = ".png";
			}
			dir = new File(cachePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			temp = new File(cachePath + id + fileExt);
			if (temp.exists()) {
				return true;
			} else {
				temp.createNewFile();
			}
			fileOutputStream = new FileOutputStream(temp);
			fileOutputStream.write(voice);
			fileOutputStream.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
					fileOutputStream = null;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
