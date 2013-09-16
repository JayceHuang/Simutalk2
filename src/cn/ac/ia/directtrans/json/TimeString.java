package cn.ac.ia.directtrans.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeString {
	
	public static final String PATTERN = "yyyyMMddHHmmss";
	private static SimpleDateFormat timeFormat = new SimpleDateFormat("",Locale.SIMPLIFIED_CHINESE);
	public static String getNowTime(){	
		SimpleDateFormat timeFormat = new SimpleDateFormat("",Locale.SIMPLIFIED_CHINESE);
		timeFormat.applyPattern(PATTERN);
		return timeFormat.format(new Date());
	}	
	
	public static String getTimeString(String pattern, long time){	
		timeFormat.applyPattern(pattern);
		return timeFormat.format(new Date(time));
	}
	
	public static String getTimeString(String pattern, Date time){	
		timeFormat.applyPattern(pattern);
		return timeFormat.format(time);
	}
	
	public static String dateToString( Date time){
		timeFormat.applyPattern(PATTERN);
		return timeFormat.format(time);		
	}
	
	
	public static Date parseString(String time){

		SimpleDateFormat timeFormat = new SimpleDateFormat("",Locale.SIMPLIFIED_CHINESE);
		timeFormat.applyPattern(PATTERN);
		Date d = null;
		
		try {
			d = timeFormat.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}
}
