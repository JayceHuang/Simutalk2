package cn.ac.ia.directtrans.json;

import java.util.Date;

public class CommFunction {

	public static String getTimeString() {
		Date d = new Date();

		return String.format("%02d%02d%02d%02d%02d", d.getMonth(),
				d.getDay(), d.getHours(), d.getMinutes(), d.getSeconds());
	}
	

}
