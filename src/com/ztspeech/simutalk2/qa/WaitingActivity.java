package com.ztspeech.simutalk2.qa;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.PopWindowTTs;

public class WaitingActivity extends Activity implements OnClickListener {

	public interface OnWaitingListener {
		public void onCancel();
	}

	private static WaitingActivity instance;
	private static boolean stoped = false;
	private static OnWaitingListener listener;
	private boolean mCanClose = false;

	private Button mBtnCancel;
	private Animation anim;
	private ImageView iView;
	private static PopWindowTTs mPopWindowTTs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_waiting);
		mBtnCancel = (Button) findViewById(R.id.btnCancel);
		mBtnCancel.setOnClickListener(this);
		instance = this;
		anim = new RotateAnimation(0, +3600, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(15000);
		anim.setFillAfter(true);
		anim.setRepeatCount(Animation.INFINITE);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		iView = (ImageView) findViewById(R.id.wloadinganim);
		// if (listener == null) {
		// mBtnCancel.setVisibility(View.GONE);
		// } else {
		// mBtnCancel.setVisibility(View.VISIBLE);
		// }

		if (stoped) {
			closeActivity();
		}
		iView.startAnimation(anim);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (listener != null) {
				listener.onCancel();
			}
			LogInfo.LogOutE("haitian", "WaitingActivity------------backKeyDown");
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		if (mCanClose) {
			instance = null;
			super.finish();
		} else if (listener != null) {
			listener.onCancel();
			instance = null;
			super.finish();
		}
	}

	private void closeActivity() {
		mCanClose = true;
		iView.clearAnimation();
		finish();
	}

	public static void stop() {

		// stoped = true;
		// if (instance != null) {
		// instance.closeActivity();
		// }
		if (mPopWindowTTs != null) {
			mPopWindowTTs.stopProgressDialog();
		}
	}

	public static void waiting(Activity context, int requestCode) {
		mPopWindowTTs = new PopWindowTTs(context);
		mPopWindowTTs.setCancel(null);
		mPopWindowTTs.showLoading();
		// listener = null;
		// stoped = false;
		// Intent intent = new Intent();
		// intent.setClass(context, WaitingActivity.class);
		// context.startActivityForResult(intent, requestCode);
	}

	public static void waiting(Activity context, int requestCode, OnWaitingListener l) {
		mPopWindowTTs = new PopWindowTTs(context);
		mPopWindowTTs.setCancel(l);
		mPopWindowTTs.showLoading();
		// listener = l;
		// stoped = false;
		// Intent intent = new Intent();
		// intent.setClass(context, WaitingActivity.class);
		// context.startActivityForResult(intent, requestCode);
	}

	public void onClick(View v) {
		if (listener != null) {
			listener.onCancel();
		}
	}

}
