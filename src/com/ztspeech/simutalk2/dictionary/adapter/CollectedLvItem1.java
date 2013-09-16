package com.ztspeech.simutalk2.dictionary.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.entity.Categroy;
import com.ztspeech.simutalk2.dictionary.entity.Child;
import com.ztspeech.simutalk2.dictionary.entity.Collecter;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class CollectedLvItem1 extends RelativeLayout implements OnClickListener {

	private BaseLvAdapter blv;
	private Collecter collecter;
	private Child child;
	private Context context;
	private Categroy categroy;
	public TextView tvEnglish;
	public TextView tvChinese;
	public TextView tvCategroy;
	public TextView tvChild;
	public ImageButton btnMore1;
	public ImageButton btnMore2;
	public ImageButton btnSpeak1;
	public ImageButton btnSpeak2;

	public LinearLayout llMiddle1;
	public LinearLayout llMiddle3;

	private UserInfo mUser = UserInfo.getInstanse();
	private Integer chOrEn = 0;
	private static Integer lastWord = 0;
	private static boolean lastCN;

	public void setData(Collecter data, Categroy categroy, Child child, Context context, Integer chOrEn) {
		collecter = data;
		this.categroy = categroy;
		this.chOrEn = chOrEn;
		this.context = context;
		tvEnglish.setText(collecter.getText1());
		tvChinese.setText(collecter.getText2());
		// if(chOrEn == 0){
		//
		// }
		// else {
		// tvEnglish.setText(word.getEnglish());
		// tvChinese.setText(word.getChinese());
		// }
		// tvCategroy.setText(categroy.getCategroyName());
		// tvChild.setText(child.getChildName());

		tvEnglish.setOnClickListener(this);
		tvChinese.setOnClickListener(this);
		tvEnglish.setTextSize(mUser.getFontSize());
		tvChinese.setTextSize(mUser.getFontSize() - 2);

		btnMore1.setOnClickListener(this);
		btnMore2.setOnClickListener(this);
		btnSpeak1.setOnClickListener(this);

		if (lastWord == collecter.getId()) {
			if (lastCN) {
				llMiddle1.setBackgroundResource(R.drawable.trans_font_bg_ce_2more_up_normal);
				llMiddle3.setBackgroundResource(R.drawable.trans_font_bg_ce_2more_down_normal);
			} else {
				llMiddle3.setBackgroundResource(R.drawable.trans_font_bg_ce_2more_down_normal);
				llMiddle1.setBackgroundResource(R.drawable.trans_font_bg_ce_2more_up_normal);
			}

			// if(chOrEn == 0){
			// llMiddle1.setBackgroundResource(R.drawable.no2_searchresult_middle1);
			// llMiddle3.setBackgroundResource(0);
			// }
			// else {
			// llMiddle3.setBackgroundResource(R.drawable.no2_searchresult_middle1);
			//
			// llMiddle1.setBackgroundResource(0);
			// }
		} else {
			llMiddle1.setBackgroundResource(R.drawable.trans_font_bg_ce_2more_up_normal);
			llMiddle3.setBackgroundResource(R.drawable.trans_font_bg_ce_2more_down_normal);
		}

	}

	public CollectedLvItem1(Context context, BaseLvAdapter blv) {
		super(context);
		this.context = context;
		this.blv = blv;
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.listview_words_detailitem, this, true);

		tvEnglish = (TextView) findViewById(R.id.tvEnglish);
		tvChinese = (TextView) findViewById(R.id.tvChinese);
		tvCategroy = (TextView) findViewById(R.id.tvCategroy);
		tvChild = (TextView) findViewById(R.id.tvChild);
		btnMore1 = (ImageButton) findViewById(R.id.btnMore1);
		btnSpeak1 = (ImageButton) findViewById(R.id.btnSpeak1);
		btnMore2 = (ImageButton) findViewById(R.id.btnMore2);
		btnSpeak2 = (ImageButton) findViewById(R.id.btnSpeak2);
		llMiddle1 = (LinearLayout) findViewById(R.id.llMiddle1);
		llMiddle3 = (LinearLayout) findViewById(R.id.llMiddle3);
	}

	public void speakStr(String str) {
		int result = new PublicArithmetic().isWhat(str);
		switch (result) {
		case 0:
		case 3:
			TextPlayer.getInstance().setPopContext(context);
			if (TextPlayer.getInstance().isPlaying()) {
				TextPlayer.getInstance().stop();
			} else {
				TextPlayer.getInstance().playChinese(str);
			}

			// if (false == mLocaleTTS.playChinese(str)) {
			// mTtsPlayer = new TTSPlayer(context, onTTSPlayerListener);
			//
			// mTtsPlayer.setLanguageToChinese();
			// mTtsPlayer.setGender(TTSDefine.GENDER_MALE);
			// mTtsPlayer.play(str);
			//
			// }
			break;
		case 1:
		case 2:
			TextPlayer.getInstance().setPopContext(context);
			if (TextPlayer.getInstance().isPlaying()) {
				TextPlayer.getInstance().stop();
			} else {
				TextPlayer.getInstance().playEnglish(str);
			}

			// if (false == mLocaleTTS.playEnglish(str)) {
			// mTtsPlayer = new TTSPlayer(context, onTTSPlayerListener);
			// mTtsPlayer.setLanguageToEnglish();
			// mTtsPlayer.setGender(TTSDefine.GENDER_MALE);
			// mTtsPlayer.play(str);
			// }
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == tvEnglish) {
			speakStr(collecter.getText1());
			// if (chOrEn == 0) {
			//
			// } else {
			// speakStr(word.getChinese());
			//
			// }
			lastWord = collecter.getId();
			lastCN = true;

		} else if (v == tvChinese) {
			speakStr(collecter.getText2());
			// if (chOrEn == 0) {
			// speakStr(word.getChinese());
			// } else {
			// speakStr(word.getEnglish());
			//
			// }
			lastWord = collecter.getId();
			lastCN = false;
		} else if (v == btnMore1) {

			Intent intent = new Intent(Util.ACTION_POMENU);
			intent.putExtra("button", 1);
			intent.putExtra("collecter", collecter);
			context.sendBroadcast(intent);
		} else if (v == btnMore2) {

			Intent intent = new Intent(Util.ACTION_POMENU);
			intent.putExtra("button", 1);
			intent.putExtra("collecter", collecter);
			context.sendBroadcast(intent);
		}
		blv.notifyDataSetChanged();
	}

}
