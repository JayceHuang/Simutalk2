package com.ztspeech.simutalk2.dictionary.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;
import android.view.View;

public class PublicArithmetic {

	public Integer isWhat(String str) {
		if (str == null || str.equals("")) {

		} else {
			str = str.replace(" ", "");
			float a = str.getBytes().length;
			float b = str.length();
			float result = a / b;
			if (result == 3) {
				return 0;// 中文
			} else if (result == 1) {
				return 1;// 英文
			} else if (result > 1 && result < 2) {
				return 2;// 中英混合,英多中少
			} else if (result > 2 && result < 3) {
				return 3;// 中英混合,中多英少
			}
		}
		return 0;
	}

	public static boolean isEngString(String str) {
		int engCount = 0;
		int totalCount = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (Character.isWhitespace(ch))
				continue;
			totalCount++;
			if (Character.isLowerCase(ch) || Character.isUpperCase(ch)
					|| Character.isDigit(ch))
				engCount++;
		}
		if (totalCount > 0 && engCount * 100 / totalCount > 50)
			return true;
		else
			return false;
	}
	
	public static boolean chTandEnF(String str){
		int result = new PublicArithmetic().isWhat(str);
		if(result==0||result ==3){
			return true;
		}else if(result == 1|| result == 2){
			return false;
		}else{
			return true;
		}
	}
	public String getCurrentDateTime() {
		Date now = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return f.format(now);
	}

	public String getCurrentDate() {
		Date now = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		return f.format(now);
	}

	// public void tongbuKouyiRecord(String said,int index){
	// SQLiteDom sqliteDom = new SQLiteDom();
	// List<KouyiRecord> list = sqliteDom.getKouyiBySaidandTranslate(said);
	// sqliteDom.deleteRecordById(list.get(index).getRecordId());
	// }
	public static void buttonClickOnlyOneTime(final View btn) {
		btn.setEnabled(false);
		new Handler().postDelayed(new Runnable() {

			public void run() {
				btn.setEnabled(true);
			}

		}, 1000);
	}
}
