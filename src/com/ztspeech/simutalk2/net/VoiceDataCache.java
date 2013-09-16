package com.ztspeech.simutalk2.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import cn.ac.ia.files.RequestParam;

import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class VoiceDataCache {
	private static String cachePath = Util.VOICE_CACHE_PATH;

	class VoiceData {
		public byte[] data;
		public String id = "";

		public byte[] getVoice(String id) {
			if (id == null) {
				return null;
			}
			if (id.equals(this.id)) {
				return data;
			}
			return null;
		}

		public void setData(String id, byte[] s) {

			this.id = id;
			data = s;
		}
	}

	@SuppressWarnings("unused")
	private ArrayList<VoiceData> mList = new ArrayList<VoiceData>();
	private static VoiceDataCache mInstance = null;

	public static VoiceDataCache getInstance() {

		if (mInstance == null) {
			mInstance = new VoiceDataCache();
		}

		return mInstance;
	}

	public void add(String id, byte[] voice, String type) {

		synchronized (this) {
			// int nSize = mList.size();
			// VoiceData data = null;
			// if(nSize > 100) {
			// data = mList.remove(0);
			// }
			// else {
			// data = new VoiceData();
			// }
			// data.setData(id, voice);
			// mList.add(data);
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
		LogInfo.LogOut("haitian", "saveVoiceData>>>>>>>>>>>>>> id = " + id);
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

	private byte[] getVoiceData(String id, String type) {
		LogInfo.LogOut("haitian", "getVoiceData>>>>>>>>>>>>>>>>>>>>----id =" + id);
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

	public byte[] findVoice(String id, String type) {

		byte[] data = null;
		synchronized (this) {
			// int nSize = mList.size();
			// for (int i = 0; i < nSize; i++) {
			// VoiceData tts = mList.get(i);
			// data = tts.getVoice(id);
			// if (data != null) {
			// mList.remove(i);
			// mList.add(tts);
			// break;
			// }
			// }
			data = getVoiceData(id, type);
		}
		return data;
	}

}
