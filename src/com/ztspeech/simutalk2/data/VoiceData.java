package com.ztspeech.simutalk2.data;

public class VoiceData {
	public static boolean isVoice(int nSize) {

		if (nSize < 1400) {
			return false;
		}

		if (nSize % 70 != 0) {
			return false;
		}
		return true;
	}
}
