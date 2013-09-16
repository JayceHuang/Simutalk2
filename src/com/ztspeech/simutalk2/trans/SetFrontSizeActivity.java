package com.ztspeech.simutalk2.trans;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;

public class SetFrontSizeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
	private static final String TAG = "SetFrontSizeActivity";
	private UserInfo mUser = UserInfo.getInstanse();
	private Context context;
	private InterpretActivity mMain = null;
	private RadioGroup mRadioGroup;
	private RadioButton radioL, radioM, radioS;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.setting_front_size, "选择字体大小");
		context = this;
		mMain = InterpretActivity.getInstance();
		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioL = (RadioButton) findViewById(R.id.radioL);
		radioM = (RadioButton) findViewById(R.id.radioM);
		radioS = (RadioButton) findViewById(R.id.radioS);
		mRadioGroup.setOnCheckedChangeListener(this);
		if (mUser.getFontIndex() == 3) {
			radioL.setChecked(true);
			mUser.setFontIndex(3);
			mMain.setFontSize(3);
		} else if (mUser.getFontIndex() == 2) {
			radioM.setChecked(true);
			mUser.setFontIndex(2);
			mMain.setFontSize(2);
		} else if (mUser.getFontIndex() == 1) {
			radioS.setChecked(true);
			mUser.setFontIndex(1);
			mMain.setFontSize(1);
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
		if (R.id.radioL == arg1) {
			mUser.setFontIndex(3);
			mMain.setFontSize(3);
		} else if (R.id.radioM == arg1) {
			mUser.setFontIndex(2);
			mMain.setFontSize(2);
		} else if (R.id.radioS == arg1) {
			mUser.setFontIndex(1);
			mMain.setFontSize(1);
		}
	}

}
