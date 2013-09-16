package com.ztspeech.simutalk2.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpStatus;

import cn.ac.ia.directtrans.json.DefineType;

import com.ztspeech.recognizer.PhoneInfo;
import com.ztspeech.recognizer.net.NetCheck;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;

public class HttpPostData implements Runnable {

	private static boolean mIsSendPhoneInfo = false;
	private PostPackage mPackage = null;
	private String host = "";
	private String httpParam = "";
	private String httpUrl = "";
	private static String _Cookie = "";
	private static Object _synch = new Object();
	public int connectTimeout = NetDefine.HTTP_CONNECT_TIMEOUT;
	public int readTimeout = NetDefine.HTTP_READ_TIMEOUT;

	private void updateUrl() {
		// httpUrl = "http://" + host + "/clientmsgservlet?" + httpParam;
		httpUrl = "http://" + host + "/DirectTrans/clientmsgservlet?" + httpParam;
		//httpUrl = "http://" + host + "/TransApp_test/clientmsgservlet?" + httpParam;
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

	public void setParam(String param) {
		httpParam = param;
	}

	public void setHost(String sHost) {
		host = sHost;
		updateUrl();
	}

	private String getPostUrl() {

		String sRet = httpUrl;

		if (mIsSendPhoneInfo == false) {
			mIsSendPhoneInfo = true;

			if (NetCheck.isWifiActive())
				sRet += "&nt=wifi";
			else
				sRet += "&nt=" + PhoneInfo.simOperatorName;
		}

		return sRet;
	}

	private void writeLog(String sLog) {
		LogInfo.LogOut("trans", "post " + sLog);
	}

	public int postData() {

		int nRet = NetResultFlag.POST_DATA_ERROR;
		HttpURLConnection httpConn = null;

		try {
			URL url = new URL(getPostUrl());
			httpConn = (HttpURLConnection) url.openConnection();
			if (httpConn == null) {
				return nRet;
			}

			ByteArrayOutputStream data = mPackage.getStream();
			// 设置连接属性
			httpConn.setConnectTimeout(connectTimeout);
			httpConn.setReadTimeout(readTimeout);
			httpConn.setDoOutput(true); // 使用 URL 连接进行输出
			httpConn.setDoInput(true); // 使用 URL 连接进行输入
			httpConn.setUseCaches(false); // 忽略缓存
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "text/json");
			httpConn.setRequestProperty(DefineType.POST_CMD, mPackage.cmd);
			httpConn.setRequestProperty(DefineType.POST_TYPE, mPackage.type);

			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			// mHttpConn.setRequestProperty("Content-length", "" + count);
			httpConn.setRequestProperty("Charset", "UTF-8");
			httpConn.setRequestProperty("Cookie", getCookie());

			httpConn.connect();
			if (data != null) {
				writeLog("post data size=" + data.size());
				httpConn.getOutputStream().write(data.toByteArray());
				httpConn.getOutputStream().flush();
			}

			nRet = httpConn.getResponseCode();
			if (nRet == HttpStatus.SC_OK) {

				String key = "";
				String cookie = "";
				for (int i = 1; (key = httpConn.getHeaderFieldKey(i)) != null; i++) {
					if (key.equalsIgnoreCase("set-cookie")) {
						cookie = httpConn.getHeaderField(key);
						cookie = cookie.substring(0, cookie.indexOf(";"));
					} else if (key.equalsIgnoreCase(DefineType.POST_TYPE)) {
						mPackage.type = httpConn.getHeaderField(key);
					}
				}

				if (cookie.length() > 10) {
					setCookie(cookie);
				}

				InputStream is = httpConn.getInputStream();

				if (is == null) {
					throw new IOException("postData.getInputStream == null");
				}

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int BUFFER_SIZE = 1024;
				byte[] buf = new byte[BUFFER_SIZE];
				int nRead = 0;
				while (true) {

					nRead = is.read(buf, 0, BUFFER_SIZE);
					if (nRead == -1) {
						break;
					}
					if (nRead > 0) {
						out.write(buf, 0, nRead);
					}
				}
				mPackage.doNetResult(out);
				is.close();
				out.close();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();

		} catch (ProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (nRet != HttpStatus.SC_OK) {
			mPackage.doNetError(nRet);
		}

		if (httpConn != null) {
			httpConn.disconnect();
			httpConn = null;
		}

		return nRet;
	}

	/**
	 * 调用线程提交
	 * 
	 * @param pack
	 * @return
	 */
	public boolean post(PostPackage pack) {

		synchronized (this) {

			mPackage = pack;
		}

		if (NetCheck.isNetActive()) {

			postData();
			return true;
		} else {

			mPackage.doNetError(NetResultFlag.POST_CONNECT_ERROR);
		}

		return false;
	}

	/**
	 * 多线程提交
	 * 
	 * @param pack
	 * @return
	 */
	public boolean threadPost(PostPackage pack) {

		synchronized (this) {

			mPackage = pack;
		}
		if (NetCheck.isNetActive()) {

			Thread t = new Thread(this);
			t.start();
			return true;
		} else {

			mPackage.doNetError(NetResultFlag.POST_CONNECT_ERROR);
		}

		return false;
	}

	public void run() {

		postData();
	}
}
