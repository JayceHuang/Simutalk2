package com.ztspeech.simutalk2.trans;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.qa.MainActivity;
import com.ztspeech.simutalk2.weibo.IRenren;
import com.ztspeech.simutalk2.weibo.ISina;
import com.ztspeech.simutalk2.weibo.ITencent;
import com.ztspeech.simutalk2.weibo.Ikaixin;

public class SetWeiboCountBinderActivity extends BaseActivity implements OnClickListener {
	public static final int CONSTANT_SINA_WEIBO_COUNT = 1;
	public static final int CONSTANT_TENCENT_WEIBO_COUNT = 2;
	public static final int CONSTANT_RENREN_WEIBO_COUNT = 3;
	public static final int CONSTANT_KAIXIN_WEIBO_COUNT = 4;

	private static final String TAG = "SetWeiboCountBinderActivity";
	private UserInfo mUser = UserInfo.getInstanse();
	private Context context;
	private InterpretActivity mMain = null;

	private TextView sina_count_msg, tencent_count_msg, renren_count_msg, kaixin_count_msg;
	private Button sina_count, tencent_count, renren_count, kaixin_count;
	private SharedPreferences sp;
	private boolean isTencentCountBinder = false;
	private boolean isSinaCountBinder = false;
	private boolean isRenrenCountBinder = false;
	private boolean isKaixinCountBinder = false;
	private AlertDialog dialog;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CONSTANT_SINA_WEIBO_COUNT:
				boolean isSinaCountBinder = (Boolean) msg.obj;
				if (isSinaCountBinder) {
					unBinderSinaCount();
				} else {
					MainActivity.iSina.bindSina(context, handler);
				}
				break;
			case CONSTANT_TENCENT_WEIBO_COUNT:
				boolean isTencentCountBinder = (Boolean) msg.obj;
				if (isTencentCountBinder) {
					unBinderCount();
				} else {
					MainActivity.iTencent.bindTencent(context, handler);
				}
				break;
			case CONSTANT_RENREN_WEIBO_COUNT:
				boolean isRenrenCountBinder = (Boolean) msg.obj;
				if (isRenrenCountBinder) {
					unBinderRenrenCount();
				} else {
					MainActivity.iRenren.bindRenren(context, handler);
				}
				break;
			case CONSTANT_KAIXIN_WEIBO_COUNT:
				boolean isKaixinCountBinder = (Boolean) msg.obj;
				if (isKaixinCountBinder) {
					unbinderKaixinount();
				} else {
					MainActivity.ikaixin.bindKaixin(context, handler);
				}
				break;
			case 201:
				updateWeiboCountTxt();
				break;
			default:
				break;
			}
		}
	};

	private void unBinderCount() {
		dialog = new AlertDialog.Builder(this).setTitle(context.getString(R.string.tip))
				.setPositiveButton(context.getString(R.string.OK_Txt), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SharedPreferences sp = context.getSharedPreferences("tencent", 0);
						sp.edit().putString("isBind", "no").commit();
						MainActivity.iTencent.setOAuth();
						updateWeiboCountTxt();
					}

				}).setNegativeButton(context.getString(R.string.CANCEL_Txt), null).create();
		dialog.setMessage(context.getString(R.string.unbindertencent));
		dialog.show();
	}

	private void unBinderSinaCount() {
		dialog = new AlertDialog.Builder(this).setTitle(context.getString(R.string.tip))
				.setPositiveButton(context.getString(R.string.OK_Txt), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SharedPreferences sp = context.getSharedPreferences("sina", 0);
						sp.edit().putString("isBind", "no").commit();
						MainActivity.iSina.setO2at();
						updateWeiboCountTxt();
					}

				}).setNegativeButton(context.getString(R.string.CANCEL_Txt), null).create();
		dialog.setMessage(context.getString(R.string.unbindersina));
		dialog.show();
	}

	private void unBinderRenrenCount() {
		dialog = new AlertDialog.Builder(this).setTitle(context.getString(R.string.tip))
				.setPositiveButton(context.getString(R.string.OK_Txt), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MainActivity.iRenren.unBinderRenren(context);
						updateWeiboCountTxt();
					}

				}).setNegativeButton(context.getString(R.string.CANCEL_Txt), null).create();
		dialog.setMessage(context.getString(R.string.unbinderrenren));
		dialog.show();
	}

	private void unbinderKaixinount() {
		dialog = new AlertDialog.Builder(this).setTitle(context.getString(R.string.tip))
				.setPositiveButton(context.getString(R.string.OK_Txt), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						MainActivity.ikaixin.unbinderKaixin(context);
						updateWeiboCountTxt();
					}

				}).setNegativeButton(context.getString(R.string.CANCEL_Txt), null).create();
		dialog.setMessage(context.getString(R.string.unbinderkaixin));
		dialog.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.weibo_binder_count, "Œ¢≤©’À∫≈…Ë÷√");
		context = this;
		mMain = InterpretActivity.getInstance();
		if (MainActivity.iSina != null) {
			MainActivity.iSina.setClass();
		}
		if (MainActivity.iRenren != null) {
			MainActivity.iRenren.setClass();
		}
		if (MainActivity.iTencent != null) {
			MainActivity.iTencent.setClass();
		}
		if (MainActivity.ikaixin != null) {
			MainActivity.ikaixin.setClass();
		}
		MainActivity.iSina = ISina.getInstance(this);
		MainActivity.iSina.init();
		MainActivity.iRenren = IRenren.getInstance(this);
		MainActivity.iRenren.init(this);
		MainActivity.iTencent = ITencent.getInstance(this);
		MainActivity.iTencent.init();
		MainActivity.ikaixin = Ikaixin.getInstance(this);
		MainActivity.ikaixin.init(this);

		sina_count_msg = (TextView) findViewById(R.id.sina_count_msg);
		tencent_count_msg = (TextView) findViewById(R.id.tencent_count_msg);
		renren_count_msg = (TextView) findViewById(R.id.renren_count_msg);
		kaixin_count_msg = (TextView) findViewById(R.id.kaixin_count_msg);
		sina_count = (Button) findViewById(R.id.sina_count);
		tencent_count = (Button) findViewById(R.id.tencent_count);
		renren_count = (Button) findViewById(R.id.renren_count);
		kaixin_count = (Button) findViewById(R.id.kaixin_count);
		sina_count.setOnClickListener(this);
		renren_count.setOnClickListener(this);
		tencent_count.setOnClickListener(this);
		kaixin_count.setOnClickListener(this);
	}

	public void updateWeiboCountTxt() {
		sp = context.getSharedPreferences("tencent", 0);
		String isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			tencent_count.setText(R.string.de_tencent_binder);
			isTencentCountBinder = true;
		} else {
			tencent_count.setText(R.string.tencent_binder);
			isTencentCountBinder = false;
		}

		sp = context.getSharedPreferences("sina", 0);
		isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			sina_count.setText(R.string.de_sina_binder);
			isSinaCountBinder = true;
		} else {
			sina_count.setText(R.string.sina_binder);
			isSinaCountBinder = false;
		}

		if (MainActivity.iRenren.isBinder()) {
			renren_count.setText(R.string.de_renren_binder);
			isRenrenCountBinder = true;
		} else {
			renren_count.setText(R.string.renren_binder);
			isRenrenCountBinder = false;
		}
		if (MainActivity.ikaixin.isBinder()) {
			kaixin_count.setText(R.string.de_kaixin_binder);
			isKaixinCountBinder = true;
		} else {
			kaixin_count.setText(R.string.kaixin_binder);
			isKaixinCountBinder = false;
		}
	}

	@Override
	protected void onStart() {
		LogInfo.LogOut("haitian", "onStart");
		updateWeiboCountTxt();
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sina_count:
			handler.sendMessage(Message.obtain(handler, CONSTANT_SINA_WEIBO_COUNT, isSinaCountBinder));
			break;
		case R.id.tencent_count:
			handler.sendMessage(Message.obtain(handler, CONSTANT_TENCENT_WEIBO_COUNT, isTencentCountBinder));
			break;
		case R.id.renren_count:
			handler.sendMessage(Message.obtain(handler, CONSTANT_RENREN_WEIBO_COUNT, isRenrenCountBinder));
			break;
		case R.id.kaixin_count:
			handler.sendMessage(Message.obtain(handler, CONSTANT_KAIXIN_WEIBO_COUNT, isKaixinCountBinder));
			break;
		default:
			break;
		}
	}

}
