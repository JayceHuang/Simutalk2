package com.ztspeech.simutalk2.dictionary.util;

import java.security.MessageDigest;

public class MD5 {
	/**
	 * ��������MD5��Ĺ��߷���,���ش�дMD5ֵ
	 * 
	 * @param request
	 *            ��������MD5���ַ���
	 * @return MD5��
	 */
	public static String md5Upper(String request) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byte2hexUpper(md.digest(request.getBytes("utf-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ��������MD5��Ĺ��߷���,����ԭ��MD5ֵ
	 * 
	 * @param request
	 *            ��������MD5���ַ���
	 * @return MD5��
	 */
	public static String md5Lower(String request) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byte2hexLower(md.digest(request.getBytes("utf-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * ������ת�ַ���
	 */
	private static String byte2hexUpper(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}

	/**
	 * ������ת�ַ���
	 */
	private static String byte2hexLower(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString();
	}

}
