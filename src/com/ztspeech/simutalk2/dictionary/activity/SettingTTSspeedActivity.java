package com.ztspeech.simutalk2.dictionary.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;

public class SettingTTSspeedActivity extends BaseActivity implements OnSeekBarChangeListener, OnClickListener {

	private SeekBar sbTTSspeed;
	private UserInfo mUser = UserInfo.getInstanse();
	private float progress = 0;
	private Button btnSure;
	private TextView tvShow;

	private static final int MIN_COUNT_NUM = 10;
	private static final int DIV_COUNT_NUM = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitleAndContentView(R.layout.setting_ttsspeed, "TTS”ÔÀŸ…Ë÷√");
		sbTTSspeed = (SeekBar) findViewById(R.id.sbTTSspeed);
		btnSure = (Button) findViewById(R.id.btnSure);
		tvShow = (TextView) findViewById(R.id.tvShow);

		sbTTSspeed.setMax(30);
		sbTTSspeed.setOnSeekBarChangeListener(this);
		btnSure.setOnClickListener(this);
		btnCancle.setOnClickListener(this);

		init();
	}

	private void init() {
		// Toast.makeText(this, "ttsspeed:"+mUser.getTtsSpeed(),
		// Toast.LENGTH_LONG).show();
		progress = mUser.getTtsSpeed() * (float) DIV_COUNT_NUM;
		tvShow.setText("X" + progress / (float) DIV_COUNT_NUM + "");
		sbTTSspeed.setProgress((int) progress - MIN_COUNT_NUM);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		tvShow.setText("X" + (progress + MIN_COUNT_NUM) / (float) DIV_COUNT_NUM + "");
		mUser.setTtsSpeed((progress + MIN_COUNT_NUM) / (float) DIV_COUNT_NUM);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		progress = sbTTSspeed.getProgress();
	}

	@Override
	public void onClick(View v) {
		if (v == btnSure) {
			mUser.setTtsSpeed((progress + MIN_COUNT_NUM) / (float) DIV_COUNT_NUM);
			finish();
		} else if (v == btnCancle) {
			finish();
		}

	}
}
