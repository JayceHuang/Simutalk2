package com.ztspeech.simutalk2.dictionary.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;

public class BaseActivity extends Activity {

	public ImageButton btnCancle;
	public TextView tvTitle;

	public void setTitleAndContentView(int layout, String title) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(title);
		btnCancle = (ImageButton) findViewById(R.id.btnLeft);
		btnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LogInfo.LogOut("baseactivity", "onclick");
				finish();
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slideinleft, R.anim.slideoutright);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.slideinright, R.anim.slideoutleft);
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	public void setTitle(String str) {
		tvTitle.setText(str);
	}

	public void sendSMS(String str) {
		Intent mailIntent = new Intent(Intent.ACTION_SEND);
		mailIntent.setType("text/plain");
		mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, str);
		mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, str);
		mailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(mailIntent, "·¢ËÍ"));
	}

	public void shareText(String str) {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboard.setText(str);
	}

	public void setBackGround(int id) {
		switch (id) {
		case 0:
			// this.set
			break;
		case 1:
			break;
		case 2:
			break;
		}
	}
}
