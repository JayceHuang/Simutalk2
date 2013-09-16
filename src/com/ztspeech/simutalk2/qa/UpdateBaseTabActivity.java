package com.ztspeech.simutalk2.qa;

import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;

public abstract class UpdateBaseTabActivity extends TabActivity {

	private Handler mMessageLoop = new Handler();
	private int mDelay = 1000;

	public int getmDelay() {
		return mDelay;
	}

	public void setmDelay(int mDelay) {
		this.mDelay = mDelay;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	protected void onPause() {

		super.onPause();
		mMessageLoop.removeCallbacks(mMessageRunnable);
	}

	@Override
	protected void onResume() {

		super.onResume();
		mMessageLoop.postDelayed(mMessageRunnable, mDelay);
	}

	private Runnable mMessageRunnable = new Runnable() {

		public void run() {

			updateMesage();
			mMessageLoop.postDelayed(mMessageRunnable, mDelay);
		}
	};

	public abstract void updateMesage();

}
