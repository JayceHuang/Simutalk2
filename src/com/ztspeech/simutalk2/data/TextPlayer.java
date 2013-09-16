package com.ztspeech.simutalk2.data;

import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.ztspeech.recognizer.speak.LocaleTTS;
import com.ztspeech.recognizer.speak.OnTTSPlayerListener;
import com.ztspeech.recognizer.speak.TTSDefine;
import com.ztspeech.recognizer.speak.TTSPlayer;
import com.ztspeech.simutalk2.dictionary.util.PopWindowTTs;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.qa.WaitingActivity.OnWaitingListener;

public class TextPlayer implements OnTTSPlayerListener, OnWaitingListener {

	private LocaleTTS mLocaleTts;
	private TTSPlayer mPlayer = null;

	private UserInfo mUser = UserInfo.getInstanse();
	private OnTTSPlayerListener mListener;
	@SuppressWarnings("unused")
	private Context mActivity;
	public String error = "error";
	public String cancel = "cancel";
	public ProgressDialog loadingDialog;
	private static TextPlayer instance;
	private Context context;
	private PopWindowTTs mPopWindowTTs;
	public boolean isLoadingData = false;

	public static TextPlayer getInstance() {
		if (instance == null) {
			instance = new TextPlayer();
		}
		return instance;
	}

	public void setPopContext(Context context) {
		this.context = context;
		mPopWindowTTs = new PopWindowTTs(context);
		mPopWindowTTs.setCancel(this);
	}

	private void setOnTTSPlayerListener(OnTTSPlayerListener listener) {
		mListener = listener;
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		mLocaleTts.stop();
		mPlayer.stop();
	}

	/**
	 * 取消数据下载
	 */
	public void cancel() {
		mLocaleTts.stop();
		mPlayer.stop();
		onTtsPlayLoadDataEnd();
	}

	public boolean isPlaying() {

		if (mLocaleTts.isPlaying()) {
			return true;
		}

		if (mPlayer.isPlayying()) {
			return true;
		}
		return false;
	}

	public void init(Context activity) {
		mActivity = activity;
		mLocaleTts = new LocaleTTS();
		mPlayer = new TTSPlayer(activity, this);
		mLocaleTts.initData(activity);
	}

	/**
	 * 播放数据流
	 * 
	 * @param stream
	 */
	public void play(InputStream stream) {

		if (isPlaying()) {
			stop();
			return;
		}

		mPlayer.play(stream);
	}

	private void playText(String text) {

		if (isPlaying()) {
			stop();
			return;
		}

		if (mUser.getTtsGender()) {
			mPlayer.setGender(TTSDefine.GENDER_MALE);
		} else {
			mPlayer.setGender(TTSDefine.GENDER_FEMALE);
		}

		mPlayer.setTTSVoiceSpeedLevel(mUser.getTtsSpeed());
		mPlayer.play(text);
	}

	public void playChinese(String text) {

		playChinese(text, null);

	}

	public void playEnglish(String text) {

		playEnglish(text, null);

	}

	private void playChinese(String text, OnTTSPlayerListener listener) {

		setOnTTSPlayerListener(listener);
		boolean playOk = false;
		if (mUser.isLocaleTTS()) {
			mLocaleTts.setSpeechRate(mUser.getTtsSpeed());
			playOk = mLocaleTts.playChinese(text);
		}

		if (false == playOk) {
			mPlayer.setLanguageToChinese();
			playText(text);
		}
	}

	private void playEnglish(String text, OnTTSPlayerListener listener) {
		setOnTTSPlayerListener(listener);
		boolean playOk = false;
		if (mUser.isLocaleTTS()) {
			mLocaleTts.setSpeechRate(mUser.getTtsSpeed());
			playOk = mLocaleTts.playEnglish(text);
		}

		if (false == playOk) {
			mPlayer.setLanguageToEnglish();
			playText(text);
		}
	}

	@Override
	public void onTtsPlayEnd() {

		if (mListener != null)
			mListener.onTtsPlayEnd();
	}

	@Override
	public void onTtsPlayError(int arg0) {

		// toastMsg("", Toast.LENGTH_SHORT);
		if (mListener != null) {
			mListener.onTtsPlayError(arg0);
		} else {
			toastMsg(error, Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void onTtsPlayLoadDataEnd() {

		if (mListener != null) {
			mListener.onTtsPlayLoadDataEnd();
		} else {
			// WaitingActivity.stop();
			if (mPopWindowTTs != null) {
				isLoadingData = false;
				mPopWindowTTs.stopProgressDialog();
			}

		}
	}

	@Override
	public void onTtsPlayLoadDataStart() {

		if (mListener != null) {
			mListener.onTtsPlayLoadDataStart();
		} else {
			// WaitingActivity.waiting(mActivity, 0, this);
			if (mPopWindowTTs != null) {
				isLoadingData = true;
				mPopWindowTTs.showLoading();
			}
		}
	}

	public void toastMsg(String msg, int duration) {
		Util.showToast(context, msg);
		// Toast.makeText(context, msg, duration).show();
	}

	@Override
	public void onTtsPlayStart() {

		if (mListener != null)
			mListener.onTtsPlayStart();
	}

	@Override
	public void onCancel() {
		cancel();
		toastMsg(cancel, Toast.LENGTH_SHORT);
	}

}
