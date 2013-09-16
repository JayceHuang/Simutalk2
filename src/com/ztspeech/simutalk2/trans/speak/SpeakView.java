package com.ztspeech.simutalk2.trans.speak;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztspeech.recognizer.speak.OnTTSPlayerListener;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.qa.WaitingActivity;

public class SpeakView extends LinearLayout implements OnClickListener {

	private Context context;
	public SpeakItemData data = null;
	private IOnSpeakViewAdapterCallback mCallback;
	private RelativeLayout mllSpeak;
	private RelativeLayout mllTrans;
	private TextView mtvSpeak;
	private TextView mtvTrans;
	private TextView mtvFlag;
	private ImageButton btnPalyTrans;
	private ImageButton btnPalySpeak;
	private ImageView imgFlag;
	private ImageView imgFlagOnlyR;
	private RelativeLayout rFlag1;
	private RelativeLayout rFlag2;
	// private LinearLayout llLeft;
	// private LinearLayout llRight;
	private float fontSize = 16;

	private static int[][] backgroundId = new int[3][2];

	public interface IOnSpeakViewAdapterCallback {
		public void speakViewAdapterCheckedChange(SpeakItemData data, String cmd);
	}

	public SpeakView(Context context, SpeakItemData data) {
		super(context);
		init(context, data);

	}

	public void setFontSize(float size) {
		fontSize = size;
	}

	@SuppressLint({ "ResourceAsColor", "ResourceAsColor" })
	private void changeFontColorFocusFalse() {
		mtvSpeak.setTextColor(getResources().getColor(R.color.item_focused_true));
		mtvSpeak.setTextSize(fontSize);
		mtvTrans.setTextColor(getResources().getColor(R.color.item_focused_true));
		mtvTrans.setTextSize(fontSize);
	}

	private void changeFontColorFocusTrue() {
		mtvSpeak.setTextColor(getResources().getColor(R.color.item_focused_false));
		mtvSpeak.setTextSize(fontSize);
		mtvTrans.setTextColor(getResources().getColor(R.color.item_focused_false));
		mtvTrans.setTextSize(fontSize);
	}

	public void setData(SpeakItemData data) {

		if (data == null) {
			return;
		}
		this.data = data;

		if(data.mspeak.equals("")){
			mtvSpeak.setText(data.speak);
		}else{
			String[] speak = data.mspeak.split(" / ");
			if(speak.length>1){
				mtvSpeak.setText(Html.fromHtml("<font size=\"3\" color=\"black\">"+speak[0]+"</font><font size=\"3\" color=\"#C0C0C0\">"+speak[1]+"</font>"));
			}else{
				mtvSpeak.setText(Html.fromHtml("<font size=\"3\" color=\"black\">"+speak[0]+"</font>"));
			}
		}
		mtvTrans.setText(data.trans);

		float size = fontSize;

		if (data.focus) {
			size += 1;
		}

		if (data.isFocus == data) {
			// llLeft.setVisibility(View.GONE);
			// llRight.setVisibility(View.VISIBLE);
			rFlag1.setVisibility(View.VISIBLE);
			rFlag2.setVisibility(View.VISIBLE);
			if(data.speakStream==null&&!UserInfo.S2T_LETTER.equals(data.type)){
				btnPalySpeak.setVisibility(View.INVISIBLE);
			}else{
				btnPalySpeak.setVisibility(View.VISIBLE);
			}
			if( data.isTransTTS()){  // 字母结果不TTS
				btnPalyTrans.setVisibility(View.VISIBLE);
			}else{
				btnPalyTrans.setVisibility(View.INVISIBLE);
			}
		} else {
			// llLeft.setVisibility(View.VISIBLE);
			// llRight.setVisibility(View.GONE);
			rFlag1.setVisibility(View.GONE);
			rFlag2.setVisibility(View.GONE);
			
			btnPalySpeak.setVisibility(View.INVISIBLE);
			btnPalyTrans.setVisibility(View.INVISIBLE);
		}

		setBg(data.isFocus == data);

		if (data.isExistTrans()) {
			if (data.flag == 3) {
				imgFlag.setVisibility(View.GONE);
				imgFlagOnlyR.setVisibility(View.GONE);
			} else if (data.flag == 1) {
				imgFlagOnlyR.setVisibility(View.GONE);
				imgFlag.setVisibility(View.VISIBLE);
				imgFlag.setImageResource(R.drawable.trans_pop_good);
			} else if (data.flag == 0) {
				imgFlagOnlyR.setVisibility(View.GONE);
				imgFlag.setVisibility(View.VISIBLE);
				imgFlag.setImageResource(R.drawable.trans_pop_bad);
			}
		} else {
			if (data.flag == 3) {
				imgFlag.setVisibility(View.GONE);
				imgFlagOnlyR.setVisibility(View.GONE);
			} else if (data.flag == 1) {
				imgFlag.setVisibility(View.GONE);
				imgFlagOnlyR.setVisibility(View.VISIBLE);
				imgFlagOnlyR.setImageResource(R.drawable.trans_pop_good);
			} else if (data.flag == 0) {
				imgFlag.setVisibility(View.GONE);
				imgFlagOnlyR.setVisibility(View.VISIBLE);
				imgFlagOnlyR.setImageResource(R.drawable.trans_pop_bad);
			}
		}

		// mtvText.setTextColor(Color.BLACK);
		// mllSpeak.setBackgroundResource(R.drawable.tans_item_selected);
		// mllBackground.setBackgroundColor(0xFFEEEEEE);
		// mllBackground.setBackgroundColor(Color.WHITE);
		// mllTrans.setBackgroundResource(0);
		// mtvText.setTextColor(0xFF707070);

		// this.setFocusable(false);
		//
		//
		// if( data.isExistTrans()){
		// mllSpeak.setBackgroundResource(R.drawable.trans_font_bg_ce_2more_up_normal);
		// mllTrans.setBackgroundResource(R.drawable.trans_font_bg_ce_2more_down_normal);
		//
		// mllSpeak .setVisibility(VISIBLE);
		// mllTrans .setVisibility(VISIBLE);
		// }
		// else {
		// mllSpeak.setBackgroundResource(R.drawable.trans_font_bg_ce_1_normal);
		// mllSpeak .setVisibility(VISIBLE);
		// mllTrans.setVisibility(GONE);
		// }

	}

	private void init(Context context, SpeakItemData data) {

		this.context = context;
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.listview_item_interpret, this, true);
		mllSpeak = (RelativeLayout) findViewById(R.id.llSpeak);
		mllTrans = (RelativeLayout) findViewById(R.id.llTrans);
		mtvSpeak = (TextView) findViewById(R.id.textSpeak);
		mtvTrans = (TextView) findViewById(R.id.textTrans);

		btnPalySpeak = (ImageButton) findViewById(R.id.btnPlaySpeak);
		btnPalyTrans = (ImageButton) findViewById(R.id.btnPlayTrans);
		imgFlag = (ImageView) findViewById(R.id.imgFlag);
		imgFlagOnlyR = (ImageView) findViewById(R.id.imgFlagOnlyR);
		rFlag1 = (RelativeLayout) findViewById(R.id.rFlag1);
		rFlag2 = (RelativeLayout) findViewById(R.id.rFlag2);
		// llLeft = (LinearLayout) findViewById(R.id.rlLeft);
		// llRight = (LinearLayout) findViewById(R.id.rlRight);

		btnPalyTrans.setOnClickListener(this);
		btnPalySpeak.setOnClickListener(this);
		setData(data);
	}

	public void setOnCallbackListener(IOnSpeakViewAdapterCallback listener) {
		mCallback = listener;
	}

	public void onClick(View v) {
		if (v == btnPalyTrans) {
			speakTrans(data.trans, data.type);
		} else if (v == btnPalySpeak) {
			speakSpeak(data.speak, data.type);
		}
	}

	private OnTTSPlayerListener onTTSPlayerListener = new OnTTSPlayerListener() {

		@Override
		public void onTtsPlayStart() {

		}

		@Override
		public void onTtsPlayLoadDataStart() {
			WaitingActivity.waiting((Activity) context, 0);
		}

		@Override
		public void onTtsPlayLoadDataEnd() {
			WaitingActivity.stop();

		}

		@Override
		public void onTtsPlayError(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTtsPlayEnd() {

		}
	};

	public void speakSpeak(String str, String type) {
		if(data.speakStream!=null&&!type.equals(UserInfo.S2T_LETTER)){
			TextPlayer.getInstance().play(data.speakStream);
		}else{
			TextPlayer.getInstance().setPopContext(context);
			if (type.equals(UserInfo.S2T_CH2EN)) {
				if (TextPlayer.getInstance().isPlaying()) {
					TextPlayer.getInstance().stop();
				} else {
					TextPlayer.getInstance().playChinese(str);
				}
			} else if (type.equals(UserInfo.S2T_EN2CH)) {
				if (TextPlayer.getInstance().isPlaying()) {
					TextPlayer.getInstance().stop();
				} else {
					TextPlayer.getInstance().playEnglish(str);
				}
			} else if (type.equals(UserInfo.S2T_LETTER)) {
				if (TextPlayer.getInstance().isPlaying()) {
					TextPlayer.getInstance().stop();
				} else {
					TextPlayer.getInstance().playEnglish(str);
				}
			}
		}
	}

	public void speakTrans(String str, String type) {
		TextPlayer.getInstance().setPopContext(context);
		if (type.equals(UserInfo.S2T_CH2EN)) {
			if (TextPlayer.getInstance().isPlaying()) {
				TextPlayer.getInstance().stop();
			} else {
				TextPlayer.getInstance().playEnglish(str);
			}
		} else if (type.equals(UserInfo.S2T_EN2CH)) {
			if (TextPlayer.getInstance().isPlaying()) {
				TextPlayer.getInstance().stop();
			} else {
				TextPlayer.getInstance().playChinese(str);
			}
		} else if (type.equals(UserInfo.S2T_LETTER)) {
			if (TextPlayer.getInstance().isPlaying()) {
				TextPlayer.getInstance().stop();
			} else {
				TextPlayer.getInstance().playChinese(str);
			}
		}
	}

	public void setBg(boolean focus) {
		int[] bgid = getBgResourceByType(data.type);
		if (focus) {
			changeFontColorFocusTrue();
			if (data.isExistTrans()) {
				mllSpeak.setBackgroundResource(bgid[0]);
				mllTrans.setBackgroundResource(bgid[1]);

				mllSpeak.setVisibility(VISIBLE);
				mllTrans.setVisibility(VISIBLE);
			} else {
				mllSpeak.setBackgroundResource(bgid[2]);
				mllSpeak.setVisibility(VISIBLE);
				mllTrans.setVisibility(GONE);
			}
		} else {
			changeFontColorFocusFalse();
			if (data.isExistTrans()) {
				mllSpeak.setBackgroundResource(bgid[3]);
				mllTrans.setBackgroundResource(bgid[4]);

				mllSpeak.setVisibility(VISIBLE);
				mllTrans.setVisibility(VISIBLE);
			} else {
				mllSpeak.setBackgroundResource(bgid[5]);
				mllSpeak.setVisibility(VISIBLE);
				mllTrans.setVisibility(GONE);

			}
		}
	}

	public int[] getBgResourceByType(String type) {
		int[] bgid = new int[6];
		if (UserInfo.S2T_CH2EN.equals(type)) {
			bgid[0] = R.drawable.trans_font_bg_ce_2more_up_click;
			bgid[1] = R.drawable.trans_font_bg_ce_2more_down_click;
			bgid[2] = R.drawable.trans_font_bg_ce_1_click;
			bgid[3] = R.drawable.trans_font_bg_ce_2more_up_normal;
			bgid[4] = R.drawable.trans_font_bg_ce_2more_down_normal;
			bgid[5] = R.drawable.trans_font_bg_ce_1_normal;
		} else if (UserInfo.S2T_EN2CH.equals(type)) {
			bgid[0] = R.drawable.trans_font_bg_ec_2more_up_click;
			bgid[1] = R.drawable.trans_font_bg_ec_2more_down_click;
			bgid[2] = R.drawable.trans_font_bg_ec_1_click;
			bgid[3] = R.drawable.trans_font_bg_ec_2more_up_normal;
			bgid[4] = R.drawable.trans_font_bg_ec_2more_down_normal;
			bgid[5] = R.drawable.trans_font_bg_ec_1_normal;
		} else if (UserInfo.S2T_LETTER.equals(type)) {
			bgid[0] = R.drawable.trans_font_bg_zimu_2more_up_click;
			bgid[1] = R.drawable.trans_font_bg_zimu_2more_down_click;
			bgid[2] = R.drawable.trans_font_bg_zimu_1_click;
			bgid[3] = R.drawable.trans_font_bg_zimu_2more_up_normal;
			bgid[4] = R.drawable.trans_font_bg_zimu_2more_down_normal;
			bgid[5] = R.drawable.trans_font_bg_zimu_1_normal;
		} else {
			bgid[0] = R.drawable.trans_font_bg_ce_2more_up_click;
			bgid[1] = R.drawable.trans_font_bg_ce_2more_down_click;
			bgid[2] = R.drawable.trans_font_bg_ce_1_click;
			bgid[3] = R.drawable.trans_font_bg_ce_2more_up_normal;
			bgid[4] = R.drawable.trans_font_bg_ce_2more_down_normal;
			bgid[5] = R.drawable.trans_font_bg_ce_1_normal;
		}
		return bgid;
	}
}
