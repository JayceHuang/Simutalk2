package com.ztspeech.simutalk2.trans;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;

public class SetTtsSoundActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
	private static final String TAG = "SetTtsSoundActivity";
	private UserInfo mUser = UserInfo.getInstanse();
	private Context context;
	private InterpretActivity mMain = null;
	private RadioGroup mRadioGroup;
	private RadioButton radioMan, radioWoman;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.setting_tts_sound, "TTSÒôÉ«");
		context = this;
		mMain = InterpretActivity.getInstance();
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioMan = (RadioButton) findViewById(R.id.radioMan);
		radioWoman = (RadioButton) findViewById(R.id.radioWoman);
		mRadioGroup.setOnCheckedChangeListener(this);
		if (mUser.getTtsGender()) {
			radioMan.setChecked(true);
			mUser.setTtsGender(true);
		} else {
			radioWoman.setChecked(true);
			mUser.setTtsGender(false);
		}
	}

	public void savelanguage(View v) {
		finish();
	}

	@Override
	protected void onPause() {
		mUser.save();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mUser.save();
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if (R.id.radioMan == arg1) {
			mUser.setTtsGender(true);
		} else if (R.id.radioWoman == arg1) {
			mUser.setTtsGender(false);
		}
	}

}
