package com.ztspeech.simutalk2.weibo;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class XmlBase64 {
	static final char[] charTab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_/".toCharArray();

	public static String encode(byte[] data) {
		return encode(data, 0, data.length, null).toString();
	}

	public static String utf8_decode(byte[] bytes, int start, int len) {
		StringBuffer s = new StringBuffer();
		for (int i = start; i < start + len;) {
			byte b = bytes[i++];
			if ((b >> 7) == 0)
				s.append((char) b);
			else if ((b >> 5) == (byte) 0xfe)
				s.append((char) (((b & 0x1f) << 6) | (bytes[i++] & 0x3f)));
			else if ((b >> 4) == (byte) 0xfe)
				s.append((char) (((b & 0xf) << 12) | ((bytes[i++] & 0x3f) << 6) | (bytes[i++] & 0x3f)));
		}
		return new String(s);
	}

	/**
	 * Encodes the part of the given byte array denoted by start and len to the
	 * Base64 format. The encoded data is appended to the given StringBuffer. If
	 * no StringBuffer is given, a new one is created automatically. The
	 * StringBuffer is the return value of this method.
	 */
	public static StringBuffer encode(byte[] data, int start, int len, StringBuffer buf) {
		if (buf == null)
			buf = new StringBuffer(data.length * 3 / 2);

		int end = len - 3;
		int i = start;

		while (i <= end) {
			int d;
			int d1, d2, d3;
			if (data[i] < 0)
				d1 = (int) (data[i] + 256);
			else
				d1 = (int) (data[i]);
			if (data[i + 1] < 0)
				d2 = (int) (data[i + 1] + 256);
			else
				d2 = (int) (data[i + 1]);
			if (data[i + 2] < 0)
				d3 = (int) (data[i + 2] + 256);
			else
				d3 = (int) (data[i + 2]);

			d = (((d1) & 0x0ff) << 16) | (((d2) & 0x0ff) << 8) | ((d3) & 0x0ff);

			buf.append(charTab[(d >> 18) & 63]);
			buf.append(charTab[(d >> 12) & 63]);
			buf.append(charTab[(d >> 6) & 63]);
			buf.append(charTab[d & 63]);

			i += 3;
		}

		if (i == start + len - 2) {
			int d;
			int d1, d2;
			if (data[i] < 0)
				d1 = (int) (data[i] + 256);
			else
				d1 = (int) (data[i]);
			if (data[i + 1] < 0)
				d2 = (int) (data[i + 1] + 256);
			else
				d2 = (int) (data[i + 1]);

			d = (((d1) & 0x0ff) << 16) | (((d2) & 255) << 8);

			buf.append(charTab[(d >> 18) & 63]);
			buf.append(charTab[(d >> 12) & 63]);
			buf.append(charTab[(d >> 6) & 63]);
			buf.append("=");

		} else if (i == start + len - 1) {
			int d;
			int d1;
			if (data[i] < 0)
				d1 = (int) (data[i] + 256);
			else
				d1 = (int) (data[i]);

			d = ((d1) & 0x0ff) << 16;

			buf.append(charTab[(d >> 18) & 63]);
			buf.append(charTab[(d >> 12) & 63]);
			buf.append("==");
		}
		return buf;
	}

	static int decode(char c) {
		if (c >= 'A' && c <= 'Z')
			return ((int) c) - 65;
		else if (c >= 'a' && c <= 'z')
			return ((int) c) - 97 + 26;
		else if (c >= '0' && c <= '9')
			return ((int) c) - 48 + 26 + 26;

		else
			switch (c) {
			case '_':
				return 62;
			case '/':
				return 63;
			case '=':
				return 0;
			default:
				throw new RuntimeException("unexpected code: " + c);
			}
	}

	/**
	 * Decodes the given Base64 encoded String to a new byte array. The byte
	 * array holding the decoded data is returned.
	 */
	public static byte[] decode(String s) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		decode(s, bos);
		return bos.toByteArray();
	}

	public static void decode(String s, ByteArrayOutputStream bos) {
		int i = 0;
		int len = s.length();

		while (true) {
			while (i < len && s.charAt(i) <= ' ')
				i++;

			if (i == len)
				break;

			int tri = (decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12) + (decode(s.charAt(i + 2)) << 6)
					+ (decode(s.charAt(i + 3)));
			bos.write((tri >> 16) & 255);

			if (s.charAt(i + 2) == '=')
				break;
			bos.write((tri >> 8) & 255);

			if (s.charAt(i + 3) == '=')
				break;
			bos.write(tri & 255);
			i += 4;
		}
	}

	public static boolean needBase64(String s) {

		byte[] buff = null;

		if (s.length() > 2) {
			if (s.substring(0, 2).equals("=?"))
				return true;
		}
		try {
			buff = s.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < buff.length; i++) {
			if (buff[i] >= 0x20 && buff[i] <= 0x7f && buff[i] != '<' && buff[i] != '>' && buff[i] != '&'
					&& buff[i] != '\'' && buff[i] != '"')
				continue;
			return true;
		}
		return false;

	}

	// public static void main(String args[]) throws Exception {
	// // String s = new String(new
	// // String("ÖÜ½ÜÂ×").getBytes("IS08859-1"),"UTF-8");
	// // String s = new String("ÖÜ½ÜÂ×".getBytes(),"UTF-8");
	// byte[] a = "²Ì½¡ÑÅ".getBytes();
	// // new String(b,"utf-8")
	// // for(int i=0;i<a.length;i++)
	// // {
	// // a[i] = 256 + a[i];
	// // }
	// String sEncode = encode(a);
	// System.out.println("sEncode:" + sEncode);
	// byte[] str = decode(sEncode);
	//
	// System.out.println("decode:" + new String(str));
	// /*
	// * String s2 = new String(new String("ÖÜ½ÜÂ×").getBytes()); byte[]a2 =
	// * s2.getBytes(); String sEncode2 = encode(s2.getBytes());
	// */
	//
	// byte[] b = sEncode.getBytes();
	// System.out.println("b:" + new String(b));
	// /*
	// * String queryKeyword = new String(b); String sName = new
	// * String(b,"utf-8");
	// *
	// * System.out.println(queryKeyword); System.out.println(sName);
	// * System.out.println("over");
	// */
	// // byte[] b3 = decode("54yu5LiWTGl2ZSjlkajmnbDkvKYp");
	// byte[] b3 = decode("54ix");
	//
	// System.out.println(new String(b3, "utf-8"));
	// System.out.println(new String(b3));
	// }
}
