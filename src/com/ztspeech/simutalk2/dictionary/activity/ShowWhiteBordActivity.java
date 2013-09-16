package com.ztspeech.simutalk2.dictionary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;

public class ShowWhiteBordActivity extends BaseActivity implements OnClickListener {

	private TextView tvEnglish;
	private TextView tvChinese;
	private ImageButton ibSpeak;
	private ImageButton ibClose;
	private Intent fromWordsActivity;

	// private String isplayingStr;

	private String english;
	private String chinese;
	private TextPlayer mTextPlayer;
	private Integer chOrEn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		setContentView(R.layout.activity_whitebord);
		fromWordsActivity = getIntent();
		english = fromWordsActivity.getStringExtra("english");
		chinese = fromWordsActivity.getStringExtra("chinese");
		chOrEn = fromWordsActivity.getIntExtra("chOrEn", 0);

		ibSpeak = (ImageButton) findViewById(R.id.ibSpeak);
		ibClose = (ImageButton) findViewById(R.id.ibClose);
		if (chOrEn == 0) {
			tvEnglish = (TextView) findViewById(R.id.tvEnglish);
			tvChinese = (TextView) findViewById(R.id.tvChinese);
		} else if (chOrEn == 1) {
			tvChinese = (TextView) findViewById(R.id.tvEnglish);
			tvEnglish = (TextView) findViewById(R.id.tvChinese);
		}
		tvEnglish.setText(english);
		tvChinese.setText(chinese);
		tvEnglish.setOnClickListener(this);
		tvChinese.setOnClickListener(this);
		ibSpeak.setOnClickListener(this);
		ibClose.setOnClickListener(this);
		mTextPlayer = TextPlayer.getInstance();
		mTextPlayer.setPopContext(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvEnglish:
			if (chOrEn == 0) {
				// if(mTextPlayer.isPlaying()&&isplayingStr.equals(english)){
				// mTextPlayer.stop();
				// }else{
				speakStr(english);
				// }

			} else if (chOrEn == 1) {
				// if(mTextPlayer.isPlaying()&&isplayingStr.equals(chinese)){
				// mTextPlayer.stop();
				// }else{
				speakStr(chinese);
				// }

			}
			break;
		case R.id.tvChinese:
			if (chOrEn == 0) {
				// if(mTextPlayer.isPlaying()&&isplayingStr.equals(chinese)){
				// mTextPlayer.stop();
				// }else{
				speakStr(chinese);
				// }

			} else if (chOrEn == 1) {
				// if(mTextPlayer.isPlaying()&&isplayingStr.equals(english)){
				// mTextPlayer.stop();
				// }else{
				speakStr(english);
				// }

			}
			break;
		case R.id.ibSpeak:
			if (chOrEn == 0) {
				// if(mTextPlayer.isPlaying()){
				// mTextPlayer.stop();
				// }else{
				speakStr(english);
				// }
			} else if (chOrEn == 1) {
				// if(mTextPlayer.isPlaying()){
				// mTextPlayer.stop();
				// }else{
				speakStr(chinese);
				// }

			}
			break;
		case R.id.ibClose:
			this.finish();
			break;
		}

	}

	public void speakStr(String str) {
		int result = new PublicArithmetic().isWhat(str);
		switch (result) {
		case 0:
		case 3:
			if (mTextPlayer.isPlaying()) {
				mTextPlayer.stop();
			} else {
				// isplayingStr = str;
				mTextPlayer.playChinese(str);
			}

			break;
		case 1:
		case 2:
			if (mTextPlayer.isPlaying()) {
				mTextPlayer.stop();
			} else {
				// isplayingStr = str;
				mTextPlayer.playEnglish(str);
			}
			break;
		}
	}

	@Override
	protected void onStop() {
		if(TextPlayer.getInstance().isPlaying()){
			TextPlayer.getInstance().stop();
		}
		super.onStop();
	}
}
