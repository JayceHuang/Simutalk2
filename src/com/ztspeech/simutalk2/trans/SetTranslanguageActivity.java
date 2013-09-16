package com.ztspeech.simutalk2.trans;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;

public class SetTranslanguageActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
	private static final String TAG = "SetTranslanguageActivity";
	private UserInfo mUser = UserInfo.getInstanse();
	private Context context;
	private InterpretActivity mMain = null;
	private RadioGroup mRadioGroup;
	private RadioButton radioC2E, radioE2C;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.setting_trans_language, "—°‘Ò”Ô—‘");
		context = this;
		mMain = InterpretActivity.getInstance();
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioC2E = (RadioButton) findViewById(R.id.radioC2E);
		radioE2C = (RadioButton) findViewById(R.id.radioE2C);
		mRadioGroup.setOnCheckedChangeListener(this);
		if (mUser.s2sType.equals(UserInfo.S2T_CH2EN)) {
			radioC2E.setChecked(true);
			mUser.s2sType = UserInfo.S2T_CH2EN;
			mMain.setS2sType(mUser.s2sType);
		} else if (mUser.s2sType.equals(UserInfo.S2T_EN2CH)) {
			radioE2C.setChecked(true);
			mUser.s2sType = UserInfo.S2T_EN2CH;
			mMain.setS2sType(mUser.s2sType);
		}
	}

	public void savelanguage(View v) {
		finish();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if (radioC2E.isChecked()) {
	// mUser.s2sType = UserInfo.S2T_CH2EN;
	// mMain.setS2sType(mUser.s2sType);
	// } else {
	// mUser.s2sType = UserInfo.S2T_EN2CH;
	// mMain.setS2sType(mUser.s2sType);
	// }
	// }
	// return super.onKeyDown(keyCode, event);
	// }

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
		if (R.id.radioC2E == arg1) {
			mUser.s2sType = UserInfo.S2T_CH2EN;
			mMain.setS2sType(mUser.s2sType);
		} else if (R.id.radioE2C == arg1) {
			mUser.s2sType = UserInfo.S2T_EN2CH;
			mMain.setS2sType(mUser.s2sType);
		}
	}

}
