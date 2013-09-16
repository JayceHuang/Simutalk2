package com.ztspeech.simutalk2.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.simutalk2.dictionary.util.LogInfo;

public class AsyncHttpDownloader extends AsyncTask<String, String, byte[]> {

	public interface OnAsyncHttpDownloaderLisenter {
		public void onData(byte[] data);

		public void onBegin();

		public void onEnd();
	}

	private VoiceDataCache voiceDataCache = VoiceDataCache.getInstance();

	private OnAsyncHttpDownloaderLisenter mLisenter = null;
	private String downloadId = "";
	private String host = "";
	private String userId = "";
	private String app = "";
	private String type = "";

	// 根据url下载文件，前提是这个文件是文本文件，函数的返回值是文件中的内容

	public AsyncHttpDownloader(OnAsyncHttpDownloaderLisenter lisenter) {

		mLisenter = lisenter;

	}

	public void setParam(String sHost, String app, String userId) {
		// host = "http://" + sHost + "/filesservlet";
		host = "http://" + sHost + "/FilesServer/filesservlet";
		this.userId = userId;
		this.app = app;
	}

	public void download(String id, String type) {

		byte[] data = voiceDataCache.findVoice(id, type);
		if (data != null) {
			mLisenter.onData(data);
			return;
		}
		downloadId = id;
		this.type = type;
		String url = host + "?" + RequestParam.APP + "=" + this.app + "&" + RequestParam.FILE_ID + "=" + id + "&"
				+ RequestParam.TYPE + "=" + type + "&" + RequestParam.USER_ID + "=" + this.userId;

		mLisenter.onBegin();
		this.execute(url);
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		mLisenter.onEnd();
	}

	@Override
	protected void onPostExecute(byte[] result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		mLisenter.onData(result);
		mLisenter.onEnd();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	private byte[] download(String urlStr) {

		ByteArrayOutputStream out = new ByteArrayOutputStream(10240);
		int BUFFER_SIZE = 1024;
		byte[] ret = null;
		byte[] buf = new byte[BUFFER_SIZE];
		HttpURLConnection urlConn = null;
		InputStream is = null;

		try {

			// 使用IO流读取数据
			// 创建url
			LogInfo.LogOut(">>>>>>>>>>>>>>>...urlStr=" + urlStr);
			URL url = new URL(urlStr);
			// 创建http
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(NetDefine.HTTP_CONNECT_TIMEOUT);
			urlConn.setReadTimeout(NetDefine.HTTP_READ_TIMEOUT);

			// 读取数据
			is = urlConn.getInputStream();
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
			ret = out.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (urlConn != null) {
			urlConn.disconnect();
			urlConn = null;
		}

		return ret;
	}

	@SuppressWarnings("unused")
	private InputStream getInputStream(String urlStr) throws IOException {
		// 创建url
		URL url = new URL(urlStr);
		// 创建http
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setConnectTimeout(NetDefine.HTTP_CONNECT_TIMEOUT);
		urlConn.setReadTimeout(NetDefine.HTTP_READ_TIMEOUT);

		// 读取数据
		InputStream inputStream = urlConn.getInputStream();

		return inputStream;
	}

	@Override
	protected byte[] doInBackground(String... params) {
		// TODO Auto-generated method stub
		String url = (String) params[0];
		byte[] result = download(url);
		if (result != null) {
			if (result.length > 0) {
				voiceDataCache.add(downloadId, result, type);
			}
		}
		return result;
	}
}
