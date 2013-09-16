package com.ztspeech.simutalk2.dictionary.util;

import java.security.MessageDigest;

public class MD5 {
	/**
	 * 用来生成MD5码的工具方法,返回大写MD5值
	 * 
	 * @param request
	 *            用来生成MD5的字符串
	 * @return MD5码
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
	 * 用来生成MD5码的工具方法,返回原生MD5值
	 * 
	 * @param request
	 *            用来生成MD5的字符串
	 * @return MD5码
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
	 * 二行制转字符串
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
	 * 二行制转字符串
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
