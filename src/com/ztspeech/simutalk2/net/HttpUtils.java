package com.ztspeech.simutalk2.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.webkit.URLUtil;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class HttpUtils {

	private static String host = "http://" + Util.FILE_HOST_IP + "/FilesServer/filesservlet";
	private static String userId = UserInfo.state.id + "";
	private static String downId = "";
	private static String app = UserInfo.appId;
	private static String fileType = RequestParam.FILE_TYPE_VOICE;
	private static String url;
	private static String cachePath = Util.VOICE_CACHE_PATH;
	private static String _Cookie = "";
	private static Object _synch = new Object();

	public HttpUtils() {

	}

	/**
	 * 设置请求url
	 * 
	 * @param id
	 * @return
	 */
	private static String setUrl(String id, String type) {
		if (id == null || id.trim().length() <= 0) {
			return null;
		}
		host = "http://" + Util.FILE_HOST_IP + "/FilesServer/filesservlet";
		userId = UserInfo.state.id + "";
		app = UserInfo.appId;
		url = "";
		downId = id;
		fileType = type;
		url = host + "?" + RequestParam.APP + "=" + app + "&" + RequestParam.FILE_ID + "=" + id + "&"
				+ RequestParam.TYPE + "=" + fileType + "&" + RequestParam.USER_ID + "=" + userId;
		return url;
	}

	private static String setIdUrl(String type, String id) {
		host = "http://" + Util.FILE_HOST_IP + "/FilesServer/filesservlet";
		if ("xxx".equals(id)) {
			userId = UserInfo.state.id + "";
		} else {
			userId = id;
		}
		app = UserInfo.appId;
		url = "";
		fileType = type;
		return host;
	}

	/**
	 * 默认30秒超时，最多服务器重试请求两次 网址错误或者网络连接失败，返回null
	 * 
	 * @return
	 */
	public static byte[] getServerString(Context context, String id, String type) {
		String newAurl;
		byte[] ret = null;
		newAurl = setUrl(id, type);
		ret = getVoiceData(downId, fileType);
		if (ret != null) {
			return ret;
		}
		for (int i = 0; i < 2; i++) {// 服务器请求两次
			ret = getServerString(context, newAurl, true);
			if (ret == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
		return ret;
	}

	/**
	 * 默认30秒超时，最多服务器重试请求两次 网址错误或者网络连接失败，返回null
	 * 
	 * @return
	 */
	public static String postServerString(Context context, String id, String type, byte[] data) {
		String newAurl;
		String ret = null;
		newAurl = setIdUrl(type, id);
		for (int i = 0; i < 2; i++) {// 服务器请求两次
			ret = postServerString(context, newAurl, true, data);
			if (ret == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}
		return ret;
	}

	/**
	 * 根据URL获取服务器的返回字符串,并保存在本地缓存中
	 * 
	 * @author haitian
	 */
	public static byte[] getServerString(Context context, String aurl, boolean flag) {
		byte[] ret = null;
		URL url = null;
		HttpURLConnection cn = null;

		ByteArrayOutputStream out = new ByteArrayOutputStream(10240);
		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];

		LogInfo.LogOut("request:  " + aurl);
		if (!URLUtil.isHttpUrl(aurl)) {
			return ret;
		}
		try {
			url = new URL(aurl);
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (isWifi(context)) {
				cn = (HttpURLConnection) url.openConnection();
			} else {
				if (proxyHost != null) {
					java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
							android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
					cn = (HttpURLConnection) url.openConnection(p);
				} else {
					cn = (HttpURLConnection) url.openConnection();
				}
			}
			cn.setRequestProperty("Accept", "*/*");
			cn.setRequestProperty("Accept-Language", "zh-cn");
			cn.setRequestProperty("Accept-Encoding", "");
			cn.setAllowUserInteraction(false);
			cn.setConnectTimeout(NetDefine.HTTP_CONNECT_TIMEOUT);
			cn.setReadTimeout(NetDefine.HTTP_READ_TIMEOUT);
			cn.setRequestMethod("GET");
			cn.setDoInput(true);
			cn.connect();
			InputStream is = cn.getInputStream();

			int nRead = 0;
			while ((nRead = is.read(buf, 0, BUFFER_SIZE)) != -1) {
				if (nRead > 0) {
					out.write(buf, 0, nRead);
				}
			}
			out.flush();
			// 有的android手机支持此种替换方式
			ret = out.toByteArray();
			if (flag) {
				saveVoiceData(downId, ret, fileType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogInfo.LogOut("dataException:" + e.getMessage());
		} finally {
			if (cn != null) {
				cn.disconnect();
			}
			cn = null;
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			out = null;
		}
		return ret;
	}

	/**
	 * 根据URL获取服务器的返回字符串,并保存在本地缓存中
	 * 
	 * @author haitian
	 */
	public static String postServerString(Context context, String aurl, boolean flag, byte[] data) {
		URL url = null;
		HttpURLConnection httpConn = null;
		String result = null;

		LogInfo.LogOut("request:  " + aurl);
		if (!URLUtil.isHttpUrl(aurl)) {
			return result;
		}
		try {

			url = new URL(aurl);
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (isWifi(context)) {
				httpConn = (HttpURLConnection) url.openConnection();
			} else {
				if (proxyHost != null) {
					java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
							android.net.Proxy.getDefaultHost(), android.net.Proxy.getDefaultPort()));
					httpConn = (HttpURLConnection) url.openConnection(p);
				} else {
					httpConn = (HttpURLConnection) url.openConnection();
				}
			}
			if (httpConn == null) {
				return result;
			}

			// 设置连接属性
			httpConn.setConnectTimeout(NetDefine.HTTP_CONNECT_TIMEOUT);
			httpConn.setReadTimeout(NetDefine.HTTP_READ_TIMEOUT);
			httpConn.setDoOutput(true); // 使用 URL 连接进行输出
			httpConn.setDoInput(true); // 使用 URL 连接进行输入
			httpConn.setUseCaches(false); // 忽略缓存
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty(RequestParam.APP, app);
			httpConn.setRequestProperty(RequestParam.TYPE, fileType);
			httpConn.setRequestProperty(RequestParam.USER_ID, userId);
			httpConn.setRequestProperty("Charset", "UTF-8");
			httpConn.setRequestProperty("Cookie", getCookie());

			httpConn.connect();
			httpConn.getOutputStream().write(data);
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
				if (result != null && result.trim().length() > 0) {
					if (!Util.isTmpFile) {
						saveVoiceData(result, data, fileType);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogInfo.LogOut("dataException:" + e.getMessage());
		} finally {
			if (httpConn != null) {
				httpConn.disconnect();
			}
			httpConn = null;
		}
		return result;
	}

	private static String getCookie() {
		synchronized (_synch) {
			return _Cookie;
		}
	}

	private static void setCookie(String cookie) {
		synchronized (_synch) {
			_Cookie = cookie;
		}
	}

	public static boolean isWifi(Context context) {
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // mobile
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi == State.CONNECTED) {
			LogInfo.LogOut("isWifi=true");
			return true;
		} else {
			LogInfo.LogOut("isWifi=false");
			return false;
		}
	}

	/**
	 * 生成URL
	 * */
	public static URL weaveUrl(String aUrl) {
		URL requestUrl = null;
		try {
			aUrl = aUrl.replaceAll(" ", "");
			if (aUrl.startsWith("http://")) {
				aUrl = aUrl.substring(7);
			}
			requestUrl = new URL("http://" + aUrl);
		} catch (Exception e) {
			e.printStackTrace();
			LogInfo.LogOut("weaveUrl-Exception:" + e.toString());
		}
		return requestUrl;
	}

	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlStr
	 */
	public static InputStream getInputStreamFromUrl(String urlStr) {
		LogInfo.LogOut("request for stream:" + urlStr);
		try {
			// 创建一个URL对象；
			URL url = new URL(urlStr);
			// 创建一个Tcp连接；
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setConnectTimeout(30000);
			urlConn.setReadTimeout(30000);
			if (urlConn.getResponseCode() != 200) {
				return null;
			}
			// 得到输入流；
			InputStream inputStream = urlConn.getInputStream();
			return inputStream;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将语音流数据存入本地文件
	 * 
	 * @param id
	 * @param voice
	 * @return
	 */
	private static boolean saveVoiceData(String id, byte[] voice, String type) {
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

	/**
	 * 获取本地缓冲数据
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	private static byte[] getVoiceData(String id, String type) {
		if (id == null || "".equals(id.trim())) {
			return null;
		}
		FileInputStream fileInputStream = null;
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
				return null;
			}
			temp = new File(cachePath + id + fileExt);

			if (!temp.exists()) {
				return null;
			}
			fileInputStream = new FileInputStream(temp);
			return getData(fileInputStream);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
					fileInputStream = null;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	/**
	 * 将InputStream转化为byte[]
	 * 
	 * @param in
	 *            输入流
	 * @return 数组
	 */
	public static byte[] getData(InputStream in) {
		if (in == null) {
			return null;
		}
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = 0;
		try {
			while ((len = in.read(b, 0, b.length)) != -1) {
				bs.write(b, 0, len);
			}
			return bs.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
