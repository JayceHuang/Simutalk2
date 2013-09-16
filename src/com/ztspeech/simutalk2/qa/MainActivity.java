package com.ztspeech.simutalk2.qa;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.JsonFunction;
import cn.ac.ia.directtrans.json.JsonRequest;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ztspeech.recognizer.PhoneInfo;
import com.ztspeech.recognizer.net.HttpGetQtEv;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.MsgDataList;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.MsgGroupTable;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.TransDataBase;
import com.ztspeech.simutalk2.data.TransTextTable;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.dom.InitDataBase;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.HttpDownloader;
import com.ztspeech.simutalk2.net.HttpDownloader.IHttpDownloadLisenter;
import com.ztspeech.simutalk2.net.PostPackage;
import com.ztspeech.simutalk2.qa.message.ProcessMessage;
import com.ztspeech.simutalk2.trans.HelpActivity;
import com.ztspeech.simutalk2.trans.InterpretActivity;
import com.ztspeech.simutalk2.trans.LoginActivity;
import com.ztspeech.simutalk2.weibo.IRenren;
import com.ztspeech.simutalk2.weibo.ISina;
import com.ztspeech.simutalk2.weibo.ITencent;
import com.ztspeech.simutalk2.weibo.Ikaixin;
import com.ztspeech.simutalk2.weixinchat.Constants;
import com.ztspeech.simutalk2.weixinchat.WchatActivity;

public class MainActivity extends Activity implements OnClickListener {

	public static final int MAIN_TAB_SHOW_TRANS = 0;
	public static final int MAIN_TAB_SHOW_LIB = 2;
	public static final int MAIN_TAB_SHOW_QA = 3;
	public static final int MAIN_TAB_SHOW_SETTINGS = 4;
	public static final String MAIN_TAB_DO = "do";

	private MsgGroupTable mMsgGroupTable = MsgGroupTable.getInstance();

	private UserInfo mUser = UserInfo.getInstanse();

	private static TransDataBase mDatabase;
	private static MainActivity mInstance = null;
	public static TransTextTable mTableTransText;
	private Handler mHandlerDelayUpdate = new Handler();
	private String mUpdateTipText;
	private boolean mShowTrans = true;

	private static Intent mService = null;
	// view control
	private Button mBtnSpeechTrans;
	private Button mBtnQA;
	private Button mBtnLibrary;
	private Button mBtnClose;
	private Button mBtnHelp;
	private Button mBtnShare;
	private Button mBtnSettings;
	public static ISina iSina;
	public static IRenren iRenren;
	public static ITencent iTencent;
	public static Ikaixin ikaixin;
	private HttpGetQtEv mHttpGetQtEv;

	public static Intent getService(){
		return mService;
	}
	public static MainActivity getInstance() {

		return mInstance;
	}

	private void exit() {

		// 取消 消息提示
		NotificationManager noticedManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (noticedManager != null) {
			noticedManager.cancel(MessageActivity.NOTIFICATION_ID);
		}

		new SQLiteDom().closeDataBase();
		mUser.save();
		postLogoutMessage();
		System.exit(0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, getString(R.string.main_menu_exit));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			if (mService != null) {
				this.stopService(mService);
			}
			exit();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {
		super.finish();

		if (false == mUser.isBackRun()) {
			if (mService != null) {
				this.stopService(mService);
			}
			exit();
		}

		/*
		 * if (mIsDoExit) {
		 * 
		 * toastMsg(getString(R.string.app_name) + "程序已退出", Toast.LENGTH_SHORT);
		 * super.finish(); exit(); } else { mIsDoExit = true;
		 * mHandlerDelayExit.postDelayed(mResetExitStateRunnable, 6000);
		 * toastMsg("再按一次退出程序", Toast.LENGTH_SHORT); }
		 */
	}

	private void postLogoutMessage() {

		PostPackage postPackage = new PostPackage(this, null);
		JsonRequest request = new JsonRequest();
		request.function = JsonFunction.LOGOUT;
		postPackage.post(request, getString(R.string.host_ip), false, 2000, 1000);
	}



	@Override
	protected void onPause() {

		super.onPause();

	}

	public void hideNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		notificationManager.cancel(0);
	}

	@Override
	protected void onStart() {

		if (mShowTrans) {
			if (UserInfo.getInstanse().isOpenTransView()) {
				showSpeechTrans();
			}
			mShowTrans = false;
		}
		super.onStart();
	}

	// 显示Notification
	public void showNotification(MsgDataList list, MsgInfoData d) {

		if (false == MessageActivity.isShowNotifitionTip(list) || false == FriendActivity.isShowNotifitionTip(list)
				|| false == MsgGroupListActivity.isShowNotifitionTip(list)
				|| false == SolveQuestionActivity.isShowNotifitionTip(list)) {
			return;
		}

		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra(MsgGroupList.PARAM_TYPE, list.type);
		intent.putExtra(MsgGroupList.PARAM_ID, list.id);

		PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification noticed = new Notification();
		noticed.icon = R.drawable.ic_launcher;
		noticed.tickerText = getString(R.string.main_msg_tip);

		// noticed.defaults = Notification.DEFAULT_SOUND; // 使用默认的声音
		// noticed.defaults = Notification.DEFAULT_VIBRATE; // 使用默认的震动
		// noticed.defaults = Notification.DEFAULT_LIGHTS; // 使用默认的Light
		// noticed.defaults = Notification.DEFAULT_ALL; // 所有的都使用默认值
		noticed.defaults = Notification.DEFAULT_SOUND;

		// 设置音乐
		// noticed.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");
		// noticed.sound =
		// Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
		noticed.setLatestEventInfo(this, d.name + ":", d.text.split("/")[0], pending);
		NotificationManager noticedManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		noticedManager.notify(MessageActivity.NOTIFICATION_ID, noticed);
	}

	// 显示Notification
	public void showNotification() {
		// 创建一个NotificationManager的引用
		NotificationManager notificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		// 定义Notification的各种属性
		Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.app_name),
				System.currentTimeMillis());
		// 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		// 点击后自动清除Notification
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		notification.ledARGB = Color.BLUE;
		notification.ledOnMS = 5000;

		// 设置通知的事件消息
		CharSequence contentTitle = getString(R.string.app_name); // 通知栏标题
		CharSequence contentText = getString(R.string.app_name) + getString(R.string.app_run_tip); // 通知栏内容

		Intent intent = new Intent(MainActivity.getInstance(), MainActivity.getInstance().getClass());
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent contentIntent = PendingIntent.getActivity(MainActivity.getInstance(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(MainActivity.getInstance(), contentTitle, contentText, contentIntent);
		// 把Notification传递给NotificationManager
		notificationManager.notify(0, notification);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.activity_main);
		mBtnSpeechTrans = (Button) findViewById(R.id.btnSeppechTrans);
		mBtnQA = (Button) findViewById(R.id.btnQA);
		mBtnLibrary = (Button) findViewById(R.id.btnLibrary);
		mBtnHelp = (Button) findViewById(R.id.btnHelp);
		mBtnShare = (Button) findViewById(R.id.btnShare);
		mBtnSettings = (Button) findViewById(R.id.btnSettings);
		mBtnClose = (Button) findViewById(R.id.btnClose);

		mBtnSpeechTrans.setOnClickListener(this);
		mBtnQA.setOnClickListener(this);
		mBtnLibrary.setOnClickListener(this);
		mBtnHelp.setOnClickListener(this);
		mBtnShare.setOnClickListener(this);
		mBtnSettings.setOnClickListener(this);
		mBtnClose.setOnClickListener(this);

		initHostAddress();

//		if (mDatabase == null) {
//			mDatabase = new TransDataBase(this.getApplicationContext(), "trans",5);
//			mTableTransText = new TransTextTable("trans_text");
//			PhoneInfo.getInstance().initData(this);
//			mDatabase.addTable(mMsgGroupTable);
//			mDatabase.addTable(mUser);
//
//			// mDatabase.addTable(mTableTransText);
//
//			// mTableTransText.load();
//			MsgGroupList.getInstance().load();
//			mUser.load();
//
//			inputUserName();
//			getHostAppVersion();
//
//			mService = new Intent(this, MsgService.class);
//			this.startService(mService);
//
//		}
		mTableTransText = ((Location)getApplication()).mTableTransText;
		inputUserName();
		getHostAppVersion();

		mService = new Intent(this, MsgService.class);
		this.startService(mService);

		//initMyDictionary();
		//initData();
		if (MsgGroupList.getInstance().size() > 0) {
			mUser.setMaxIdToMsg();
		}

//		TextPlayer.getInstance().init(this);
//		TextPlayer.getInstance().error = getString(R.string.main_tts_error);
//		TextPlayer.getInstance().cancel = getString(R.string.main_tts_cancel);
//		PostPackage.setDefaultHost(getString(R.string.host_ip));

//		mMessageLoop = ProcessMessage.getInstance();
//		mMessageLoop.set(this, getString(R.string.host_ip));
//		mMessageLoop.start();
		
//		Intent processService = new Intent(this,ProcessService.class);
//		stopService(processService);
//		startService(processService);
		
		mInstance = this;
		mHttpGetQtEv = new HttpGetQtEv();
		new Thread(new Runnable() {
			public void run() {
				mHttpGetQtEv.setQtRequest("accesslist");
				// if (HttpGetQtEv.hostList.contains("s2s.simutalk.com")) {
				// HttpGetQtEv.hostList.remove("s2s.simutalk.com");
				// }
				// HttpGetQtEv.hostList.add(0, "s2s.simutalk.com");
			}
		}).start();
		
		
		
		
		/**
		 * 微信插件注册
		 */
		IWXAPI iapi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		iapi.registerApp(Constants.APP_ID);
	}

	public void initHostAddress() {

		Util.HOST_IP = getString(R.string.host_ip);
		Util.FILE_HOST_IP = getString(R.string.file_host_ip);
		Util.BTNTOHOST = getString(R.string.btnToHost);
		Util.HOST_CH_UPDATE = getString(R.string.host_ch_update);
		Util.HELP_URL = getString(R.string.help_url);
	}

	/**
	 * 我的词库 数据库初始化
	 * 
	 */
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

	/**
	 * 翻译界面数据初始化 必须在database2打开后执行
	 */
	public void initData() {
		mTableTransText.load();
		mTableTransText.initRecord_Id();
	}

	private String isok = "";
	private String isok2 = "";

	@Override
	protected void onResume() {
		// hideNotification();
		String isok = ((Location)getApplication()).isok;
		String isok2 = ((Location)getApplication()).isok2;
		mShowTrans = true;
		if (isok.equals(getResources().getString(R.string.dbInit_Successfull))
				&& isok2.equals(getResources().getString(R.string.dbInit_Successfull))) {

		} else {
			if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
				new AlertDialog.Builder(MainActivity.this).setTitle("提示")
						.setMessage("数据库初始化失败，请联系紫冬口译开发小组。给您带来的不便我们深表歉意。")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								System.exit(0);
							}
						}).setOnCancelListener(new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								System.exit(0);

							}
						}).show();
				// System.exit(0);
			} else {
				new AlertDialog.Builder(MainActivity.this).setTitle("提示")
						.setMessage("请插入sdcard后尝试开启本程序，给您带来的不便我们深表歉意。")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								System.exit(0);

							}
						}).setOnCancelListener(new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								System.exit(0);

							}
						}).show();
				// System.exit(0);
			}

		}
		super.onResume();
	}

	@Override
	protected void onRestart() {

		mShowTrans = false;
		super.onRestart();
	}

	private void getHostAppVersion() {
		UserInfo.version = getVersionName();
		HttpDownloader httpDownloader = new HttpDownloader();
		httpDownloader.download(getString(R.string.host_ch_update), mGetVersionLisenter);
	}

	private String mUpdateUrl;
	private DialogInterface.OnClickListener mAppUpdateClickListener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface arg0, int arg1) {

			updateApp(mUpdateUrl);
		}
	};

	private void updateApp(String sUpdateUrl) {

		Uri uri = Uri.parse(sUpdateUrl);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(it);
	}

	private void inputUserName() {

		if (mUser.getUserName().length() == 0) {
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);// 为Intent设置要激活的组件
			startActivity(intent);
		}
	}

	private Runnable mDelayUpdateRunnable = new Runnable() {

		public void run() {

			if (InterpretActivity.isWorking) {
				mHandlerDelayUpdate.postDelayed(mDelayUpdateRunnable, 1000);
				return;
			}

			new AlertDialog.Builder(MainActivity.this).setTitle("更新").setMessage(mUpdateTipText)
					.setPositiveButton("更新", mAppUpdateClickListener).setNeutralButton("稍后再说", null).show();

			SharedPreferences pref = getSharedPreferences("isPopUpdate", 0);
			String currentdate = new PublicArithmetic().getCurrentDate();
			pref.edit().putString("lastdate", currentdate).commit();

			mHandlerDelayUpdate.removeCallbacks(mDelayUpdateRunnable);
		}
	};

	private IHttpDownloadLisenter mGetVersionLisenter = new IHttpDownloadLisenter() {

		public void onHttpDownloadString(String sText) {
			String info[] = sText.split("/r/n");
			if (info.length == 4) {

				String v = UserInfo.version;
				if (v.compareTo("") == 0) {
					return;
				}

				String b = info[1].trim().toString();
				if (false == isNewVersion(v, b)) {
					return;
				}

				mUpdateUrl = info[2];
				mUpdateTipText = info[3].replace("\\n", "\r\n");
				SharedPreferences pref = getSharedPreferences("isPopUpdate", 0);
				String lastdate = pref.getString("lastdate", "");

				String currentdate = new PublicArithmetic().getCurrentDate();

				if (!currentdate.equals(lastdate)) {
					mHandlerDelayUpdate.postDelayed(mDelayUpdateRunnable, 1000);
				}
			}
		}
	};

	/**
	 * 程序版本比对
	 * 
	 * @param app
	 * @param server
	 * @return
	 */
	private boolean isNewVersion(String app, String server) {

		if (app == null) {
			return true;
		}

		if (server == null) {
			return true;
		}
		app = app.replace('.', ';');
		server = server.replace('.', ';');
		String appV[] = app.split(";");
		String serverV[] = server.split(";");
		if (appV.length == serverV.length) {
			for (int i = 0; i < appV.length; i++) {

				int vApp = stringToInt(appV[i], 0);
				int vSvr = stringToInt(serverV[i], 1);
				if (vApp < vSvr) {
					return true;
				} else if (vApp > vSvr) {
					return false;
				}
			}
		}
		return false;
	}

	private int stringToInt(String value, int def) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {

			e.printStackTrace();
		}
		return def;
	}

	public String getVersionName() {

		// 获取packagemanager的实例

		PackageManager packageManager = this.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			String name = this.getPackageName();
			packInfo = packageManager.getPackageInfo(name, 0);
			return packInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void toastMsg(String msg, int duration) {

		Toast.makeText(this, msg, duration).show();
	}

	public void onClick(View arg0) {

		if (arg0 == mBtnSpeechTrans)
			showSpeechTrans();
		else if (arg0 == mBtnQA){
			showQA();
//			Intent intent = new Intent(this,WchatActivity.class);
//			startActivity(intent);
		}
		else if (arg0 == mBtnLibrary)
			showLibrary();
		else if (arg0 == mBtnHelp)
			showHelp();
		else if (arg0 == mBtnShare)
			showShare();
		else if (arg0 == mBtnSettings)
			showSettings();
		else if (arg0 == mBtnClose) {
			if (mService != null) {
				this.stopService(mService);
			}
			exit();
		}
	}

	private void showHelp() {

		Intent intent = new Intent(this, HelpActivity.class);
		startActivityForResult(intent, 0);
	}

	private void showSettings() {

		Intent intent = new Intent(this, MainTabActivity.class);
		intent.putExtra(MAIN_TAB_DO, MainActivity.MAIN_TAB_SHOW_SETTINGS);
		startActivityForResult(intent, 0);
	}

	private void showShare() {

		Intent mailIntent = new Intent(Intent.ACTION_SEND);
		mailIntent.setType("text/plain");
		mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share_text));
		mailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (UserInfo.S2T_CH2EN.equals(mUser.s2sType)) {
			startActivity(Intent.createChooser(mailIntent, "发送"));
		} else {
			startActivity(Intent.createChooser(mailIntent, "Send"));
		}
	}

	private void showLibrary() {

		Intent intent = new Intent(this, MainTabActivity.class);
		intent.putExtra(MAIN_TAB_DO, MainActivity.MAIN_TAB_SHOW_LIB);
		startActivityForResult(intent, 0);
	}

	private void showQA() {

		Intent intent = new Intent(this, MainTabActivity.class);
		intent.putExtra(MAIN_TAB_DO, MainActivity.MAIN_TAB_SHOW_QA);
		startActivityForResult(intent, 0);
	}

	private void showSpeechTrans() {

		Intent intent = new Intent(this, MainTabActivity.class);
		intent.putExtra(MAIN_TAB_DO, MainActivity.MAIN_TAB_SHOW_TRANS);
		startActivityForResult(intent, 0);
	}

	@Override
	public void onLowMemory() {

		super.onLowMemory();
		// if(false == isAppOnForeground()){
		// exit();
		// }
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device
		ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = this.getPackageName();

		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

}
