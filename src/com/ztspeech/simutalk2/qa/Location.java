package com.ztspeech.simutalk2.qa;

import java.util.HashMap;
import java.util.Map;

import com.baidu.location.*;
import com.ztspeech.recognizer.PhoneInfo;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.MsgGroupTable;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.TransDataBase;
import com.ztspeech.simutalk2.data.TransTextTable;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.dom.InitDataBase;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.exception.CrashHanlderExcetpion;
import com.ztspeech.simutalk2.net.PostPackage;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.Vibrator;

public class Location extends Application {

	public LocationClient mLocationClient = null;
	private String mData;  
	public MyLocationListenner myListener = new MyLocationListenner();
	public TextView mTv;
	public NotifyLister mNotifyer=null;
	public Vibrator mVibrator01;
	public final static int GET_LOCATION = 10002;
	
	
	
	
	private MsgGroupTable mMsgGroupTable = MsgGroupTable.getInstance();

	private UserInfo mUser = UserInfo.getInstanse();

	private static TransDataBase mDatabase;
	private static MainActivity mInstance = null;
	public static TransTextTable mTableTransText;
	
	public static String isok2 = null;
	public static String isok = null;
	
	public Handler myHandler;
	public void setHandler(Handler myHandler){
		this.myHandler = myHandler;
	}
	@Override
	public void onCreate() {
		mLocationClient = new LocationClient( getApplicationContext() );
		mLocationClient.registerLocationListener( myListener );
		super.onCreate(); 
		Log.d("locSDK_Demo1", "... Application onCreate... pid=" + Process.myPid());
		//初始化崩溃log
		initCrash();
		
		
		//初始化数据库
		if (mDatabase == null) {
			mDatabase = new TransDataBase(this.getApplicationContext(), "trans",5);
			mTableTransText = new TransTextTable("trans_text");
			PhoneInfo.getInstance().initData(this);
			mDatabase.addTable(mMsgGroupTable);
			mDatabase.addTable(mUser);
			MsgGroupList.getInstance().load();
			mUser.load();
		}
		
		initMyDictionary();
		initData();
		
		TextPlayer.getInstance().init(this);
		TextPlayer.getInstance().error = getString(R.string.main_tts_error);
		TextPlayer.getInstance().cancel = getString(R.string.main_tts_cancel);
		PostPackage.setDefaultHost(getString(R.string.host_ip));
		
	}
	
	public void initMyDictionary() {
		InitDataBase writeDBtoSDCard = new InitDataBase(this);
		isok2 = writeDBtoSDCard.writeDatabaseToPhone();
		if (isok2.equals(getResources().getString(R.string.dbInit_Successfull))) {
			new SQLiteDom().openDB2();
			isok = writeDBtoSDCard.writeDatabase();
			new SQLiteDom().openDB1();
		} else {
			// isok2 = writeDBtoSDCard.writeDatabaseToPhone();
		}
	}
	
	public void initData() {
		mTableTransText.load();
		mTableTransText.initRecord_Id();
	}
	
	public void initCrash(){
		CrashHanlderExcetpion.getInstance().init(this);
	}

	
	/**
	 * 显示字符串
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			mData = str;
			if ( mTv != null )
				mTv.setText(mData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			Map mlocation = new HashMap<String, Object>();
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			mlocation.put("latitude", latitude);
			mlocation.put("longitude", longitude);
			Message msg = new Message();
			msg.what = GET_LOCATION;
			msg.obj = mlocation;
			myHandler.sendMessage(msg);
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ;
			}
			Map mlocation = new HashMap<String, Object>();
			double latitude = poiLocation.getLatitude();
			double longitude = poiLocation.getLongitude();
			mlocation.put("latitude", latitude);
			mlocation.put("longitude", longitude);
			Message msg = new Message();
			msg.what = GET_LOCATION;
			msg.obj = mlocation;
			myHandler.sendMessage(msg);
		}
	}
	
	/**
	 * 位置提醒回调函数
	 */
	public class NotifyLister extends BDNotifyListener{
		public void onNotify(BDLocation mlocation, float distance){
			mVibrator01.vibrate(1000);
		}
	}
	
}