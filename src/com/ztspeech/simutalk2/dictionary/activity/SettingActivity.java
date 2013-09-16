package com.ztspeech.simutalk2.dictionary.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.JsonSetUserInfo;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.trans.InterpretActivity;

public class SettingActivity extends PreferenceActivity implements OnPreferenceClickListener,
		OnPreferenceChangeListener {

	// private static int CHANGE_USERNAME = 0;
	// private String lLanguageKey;
	private String cTranslateKey;
	private String cAutoPlayKey;
	private String cTTSKey;
	private String cAutoJumpKey;
	private String cBackRunKey;
	private String cTranslatetalkKey;
	private String cBLClickRecordKey;
	// private String eNikiNameKey;
	// private String lFrontSizeKey;
	// private String lTTSSexKey;
	// private String lTTSSpeedKey;
	private EditTextPreference eNikiName;
	// private ListPreference lLanguage;
	private CheckBoxPreference cTranslate;
	private CheckBoxPreference cTranslatetalk;
	private CheckBoxPreference cAutoPlay;
	private CheckBoxPreference cTTS;
	private CheckBoxPreference cAutoJump;
	private CheckBoxPreference cBackRun;
	private CheckBoxPreference cBLClickRecord;
	// private ListPreference lFrontSize;
	// private ListPreference lTTSSex;
	// private ListPreference lTTSSpeed;
	private InterpretActivity mMain = null;
	private SharedPreferences prefs;
	private UserInfo mUser = UserInfo.getInstanse();
	private PostPackageEngine mPostPackageEngine;

	// private int postPackageType = CHANGE_USERNAME;
	private String username;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				postPackageCallBack((ResultPackage) msg.obj);
				break;
			case 404:
				eNikiName.setText(mUser.getUserName());
				Toast.makeText(SettingActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		overridePendingTransition(R.anim.slideinright, R.anim.slideoutleft);
		setTheme(R.style.perference_set_activity);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_preference);
		// lLanguageKey =
		// getResources().getString(R.string.setting_listpreference_choselanguage);
		cTranslateKey = getResources().getString(R.string.setting_checkboxpreference_translate);
		cAutoPlayKey = getResources().getString(R.string.setting_checkboxpreference_autoplay);
		cTTSKey = getResources().getString(R.string.setting_checkboxpreference_tts);
		cAutoJumpKey = getResources().getString(R.string.setting_checkboxpreference_autojump);
		cBackRunKey = getResources().getString(R.string.setting_checkboxpreference_run);
		cTranslatetalkKey = getResources().getString(R.string.setting_checkboxpreference_translatetalk);
		cBLClickRecordKey = getResources().getString(R.string.setting_checkboxpreference_longclickrecord);
		// eNikiNameKey =
		// getResources().getString(R.string.setting_edittextpreference_nikename);
		// lFrontSizeKey =
		// getResources().getString(R.string.setting_listpreference_fontsize);
		// lTTSSexKey =
		// getResources().getString(R.string.setting_listpreference_ttssex);
		// lTTSSpeedKey =
		// getResources().getString(R.string.setting_listpreference_ttsspeed);

		// eNikiName = (EditTextPreference) findPreference(eNikiNameKey);
		// lLanguage = (ListPreference) findPreference(lLanguageKey);
		cTranslate = (CheckBoxPreference) findPreference(cTranslateKey);
		cTranslatetalk = (CheckBoxPreference) findPreference(cTranslatetalkKey);
		cAutoPlay = (CheckBoxPreference) findPreference(cAutoPlayKey);
		cTTS = (CheckBoxPreference) findPreference(cTTSKey);
		cAutoJump = (CheckBoxPreference) findPreference(cAutoJumpKey);
		cBackRun = (CheckBoxPreference) findPreference(cBackRunKey);
		cBLClickRecord = (CheckBoxPreference) findPreference(cBLClickRecordKey);
		// lFrontSize = (ListPreference) findPreference(lFrontSizeKey);
		// lTTSSex = (ListPreference) findPreference(lTTSSexKey);
		// lTTSSpeed = (ListPreference) findPreference(lTTSSpeedKey);

		// eNikiName.setText(mUser.getUserName());

		// eNikiName.setOnPreferenceClickListener(this);
		// eNikiName.setOnPreferenceChangeListener(this);
		// lLanguage.setOnPreferenceClickListener(this);
		// lLanguage.setOnPreferenceChangeListener(this);
		cTranslate.setOnPreferenceClickListener(this);
		cTranslate.setOnPreferenceChangeListener(this);
		cTranslatetalk.setOnPreferenceClickListener(this);
		cTranslatetalk.setOnPreferenceChangeListener(this);
		cAutoPlay.setOnPreferenceClickListener(this);
		cAutoPlay.setOnPreferenceChangeListener(this);
		cTTS.setOnPreferenceClickListener(this);
		cTTS.setOnPreferenceChangeListener(this);
		cAutoJump.setOnPreferenceClickListener(this);
		cAutoJump.setOnPreferenceChangeListener(this);
		cBackRun.setOnPreferenceChangeListener(this);
		cBLClickRecord.setOnPreferenceChangeListener(this);
		// lFrontSize.setOnPreferenceChangeListener(this);
		// lFrontSize.setOnPreferenceClickListener(this);
		// lTTSSex.setOnPreferenceChangeListener(this);
		// lTTSSex.setOnPreferenceClickListener(this);
		// lTTSSpeed.setOnPreferenceChangeListener(this);
		// lTTSSpeed.setOnPreferenceClickListener(this);

		mMain = InterpretActivity.getInstance();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

	}

	@Override
	protected void onResume() {
		initConfig();
		super.onResume();
	}

	public void initConfig() {
		// if (mUser.s2sType.equals(UserInfo.S2T_CH2EN)) {
		// lLanguage.setValueIndex(0);
		// } else if (mUser.s2sType.equals(UserInfo.S2T_EN2CH)) {
		// lLanguage.setValueIndex(1);
		// }
		// if (mUser.getTtsGender()) {
		// lTTSSex.setValueIndex(0);
		// } else {
		// lTTSSex.setValueIndex(1);
		// }
		cTTS.setChecked(mUser.isLocaleTTS());
		// 设置语速默认值
		// if(mUser.getTtsSpeed()==0.5){
		// lTTSSpeed.setValueIndex(2);
		// }else if(mUser.getTtsSpeed()==1.5){
		// lTTSSpeed.setValueIndex(1);
		// }else if(mUser.getTtsSpeed()==2.0){
		// lTTSSpeed.setValueIndex(0);
		// }
		cAutoJump.setChecked(mUser.isOpenTransView());
		
		cTranslatetalk.setChecked(mUser.isTranslatetalk());
		cBLClickRecord.setChecked(mUser.isLClickRecord());
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// if (preference.getKey().equals(eNikiNameKey)) {
		// // System.out.println(newValue.toString());
		// if (newValue == null || newValue.toString().equals("") ||
		// newValue.toString().length() <= 0) {
		// return false;
		// } else {
		// checkUserName(newValue.toString());
		// }
		// } else
		// if (preference.getKey().equals(lLanguageKey)) {
		// // System.out.println(newValue.toString());
		// // String result = prefs.getString(lLanguageKey, "no");
		// if (newValue.toString().equals("0")) {
		// mUser.s2sType = UserInfo.S2T_CH2EN;
		// mMain.setS2sType(mUser.s2sType);
		// } else if (newValue.toString().equals("1")) {
		// mUser.s2sType = UserInfo.S2T_EN2CH;
		// mMain.setS2sType(mUser.s2sType);
		// }
		// } else
		if (preference.getKey().equals(cTranslateKey)) {
			// System.out.println(newValue.toString());
			mUser.setOnlyRecoginze(!Boolean.parseBoolean(newValue.toString()));
			mMain.setOnlyRecoginze(mUser.isOnlyRecoginze());
		}else if (preference.getKey().equals(cTranslatetalkKey)) {
			// System.out.println(newValue.toString());
			//设置对话翻译
			mUser.setTranslatetalk(Boolean.parseBoolean(newValue.toString()));
			
		} else if (preference.getKey().equals(cAutoPlayKey)) {
			// System.out.println(newValue.toString());
			mUser.autoTTS = Boolean.parseBoolean(newValue.toString());
			mMain.setAutoTTS(mUser.autoTTS);
		} else if (preference.getKey().equals(cTTSKey)) {
			// System.out.println(newValue.toString());
			mUser.setLocaleTTS(Boolean.parseBoolean(newValue.toString()));
			mMain.setLocaleTTS(mUser.isLocaleTTS());
		} else if (preference.getKey().equals(cAutoJumpKey)) {
			mUser.setOpenTransView(Boolean.parseBoolean(newValue.toString()));
		} else if (preference.getKey().equals(cBackRunKey)) {
			mUser.setBackRun(Boolean.parseBoolean(newValue.toString()));
		} else if (preference.getKey().equals(cBLClickRecordKey)) {
			mUser.setLClickRecord(Boolean.parseBoolean(newValue.toString()));
		}
		// else if (preference.getKey().equals(lFrontSizeKey)) {
		// mUser.setFontIndex(Integer.parseInt(newValue.toString()));
		// mMain.setFontSize(Integer.parseInt(newValue.toString()));
		// }
		// else if (preference.getKey().equals(lTTSSexKey)) {
		// switch (Integer.parseInt(newValue.toString())) {
		// case 0:
		// mUser.setTtsGender(true);
		// break;
		// case 1:
		// mUser.setTtsGender(false);
		// break;
		// }
		//
		// }
		// else if(preference.getKey().equals(lTTSSpeedKey)){
		//
		// //设置语速j
		// mUser.setTtsSpeed(Float.valueOf(newValue.toString()));
		// }
		else {
			return false;
		}
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// if (preference.getKey().equals(lLanguageKey)) {
		// // String result = prefs.getString(lLanguageKey, "no");
		// // System.out.println(result);
		// } else
		if (preference.getKey().equals(cTranslateKey)) {

		} else if (preference.getKey().equals(cAutoPlayKey)) {

		} else if (preference.getKey().equals(cTTSKey)) {

		} else if (preference.getKey().equals(cAutoJumpKey)) {

		} else {
			return false;
		}
		return false;
	}

	public boolean checkUserName(String nikeName) {
		String sName = nikeName.trim();
		JsonSetUserInfo info = new JsonSetUserInfo();
		info.setUserName(sName);
		username = sName;

		mPostPackageEngine = new PostPackageEngine(SettingActivity.this, info, handler);
		mPostPackageEngine.post();
		// Toast.makeText(SettingActivity.this, "ok", Toast.LENGTH_LONG).show();
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			overridePendingTransition(R.anim.slideinleft, R.anim.slideoutright);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
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

	private void postPackageCallBack(ResultPackage result) {
		if (result.isNetSucceed()) {

			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (ret != null) {
				if (ret.succeed == true) {
					mMain.setUserName(username);
					mUser.save();
					Toast.makeText(SettingActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
				} else {
					eNikiName.setText(mUser.getUserName());
					new AlertDialog.Builder(SettingActivity.this).setTitle("提示").setMessage(ret.explain)
							.setPositiveButton("确定", null).show();
				}
			} else {
				eNikiName.setText(mUser.getUserName());
			}
		} else {
			eNikiName.setText(mUser.getUserName());
		}
	}
}
