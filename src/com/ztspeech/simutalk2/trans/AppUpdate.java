package com.ztspeech.simutalk2.trans;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class AppUpdate extends Activity {

    private Handler mHandlerDelay = new Handler();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mHandlerDelay.postDelayed(mDelayRunnable, 200);
	}
	
	private Runnable mDelayRunnable = new Runnable() {
		
		public void run() {
			finish();
		}
	};
}
