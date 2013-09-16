package com.ztspeech.simutalk2.qa;

import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.Json;
import cn.ac.ia.directtrans.json.JsonMessage;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.QuestionInfo;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.recognizer.speak.VoicePlayer;
import com.ztspeech.recognizer.speak.interf.OnPlayerListener;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.net.ResultPackage;

@SuppressLint("HandlerLeak")
public class GetQuestionActivity extends UpdateBaseActivity implements OnClickListener {

	// Intent param define
	public static final String PARAM_TYPE = "type";
	public static final String PARAM_ID = "id";
	public static final int TYPE_ASK = 1;
	public static final int TYPE_SOLVE = 2;
	// public static final int TYPE_ASK1 = 1;

	// view control
	private Button mBtnGiveUp;
	private Button mBtnResponse;

	private QuestionInfo mAskQuestion;
	private VoicePlayer mPlayer = null;
	private TextView tvMsgText;
	private TextView tvMsgTime;
	private TextView tvName;
	private Button btnPlayer;
	private TextView tvLen;
	private LinearLayout layoutQuestion;

	// data
	private MsgInfoData data;
	private Context context;
	private GetVoiceFromServerEngine mGetQuestionActivityEngine;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(context, context.getString(R.string.cancelnet), Toast.LENGTH_LONG).show();
				break;
			case 1:
				Toast.makeText(context, context.getString(R.string.playererror), Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(context, context.getString(R.string.qa_msg_download_error), Toast.LENGTH_LONG).show();
				break;
			case 3:
				handler.postDelayed(new Runnable() {
					public void run() {
						layoutQuestion.setBackgroundColor(0);
					}
				}, data.vLen);
				break;
			case 200:
				postPackageCallBack((ResultPackage) msg.obj);
				break;
			case 404:
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_getquestion);
		context = this;
		mPlayer = new VoicePlayer(this);
		mPlayer.setListener(mPlayerListener);
		mBtnGiveUp = (Button) findViewById(R.id.btnGiveUp);
		mBtnResponse = (Button) findViewById(R.id.btnResponse);
		tvMsgText = (TextView) findViewById(R.id.tvMsgText);
		tvMsgTime = (TextView) findViewById(R.id.tvMsgTime);
		tvName = (TextView) findViewById(R.id.tvName);
		tvLen = (TextView) findViewById(R.id.tvLen);
		btnPlayer = (Button) findViewById(R.id.btnPlay);
		layoutQuestion = (LinearLayout) findViewById(R.id.layoutQuestion);

		layoutQuestion.setOnClickListener(this);
		mBtnGiveUp.setOnClickListener(this);
		mBtnResponse.setOnClickListener(this);
		mGetQuestionActivityEngine = new GetVoiceFromServerEngine(context, mPlayer, handler);
		// mEditMsg .setFocusableInTouchMode(false);
		getIntentData();
	}

	private void postPackageCallBack(ResultPackage result) {
		if (result.isNetSucceed()) {

			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (ret != null) {
				if (ret.succeed == true) {

					JsonMessage q = Json.fromJson(ret.json, JsonMessage.class);
					if (q != null) {

					}
				} else {
					new AlertDialog.Builder(GetQuestionActivity.this).setTitle("提示").setMessage(ret.explain)
							.setPositiveButton("确定", null).show();
				}
			}
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();

	}

	private void getIntentData() {

		mAskQuestion = UserStateActivity.mQuestionInfo;
		if (mAskQuestion != null) {
			data = new MsgInfoData();
			data.name = mAskQuestion.senderName;
			data.time = mAskQuestion.time;
			data.text = mAskQuestion.text;
			data.vLen = mAskQuestion.vLen;
			data.vId = mAskQuestion.vId;
			data.senderId = mAskQuestion.senderId;

			tvName.setText(data.name);
			tvMsgTime.setText(UserInfo.getTimeString(data.time));
			tvMsgText.setText(data.text);

			if (data.vLen == 0) {
				btnPlayer.setVisibility(View.GONE);
				tvLen.setVisibility(View.GONE);
			} else {
				btnPlayer.setVisibility(View.VISIBLE);
				tvLen.setVisibility(View.VISIBLE);
				tvLen.setText(UserInfo.getSbxLen(data.vLen));
			}
		}
	}

	private OnPlayerListener mPlayerListener = new OnPlayerListener() {

		public InputStream getPlayWaveData(String text) {

			// InputStream in = getTTSInputStream(text);
			// InputStream in = mTtsDownloader.getTTSInputStream(text);

			return null;
		}

		public void onPlayStart() {
			// TODO Auto-generated method stub

		}

		public void onPlayStop() {
			// TODO Auto-generated method stub
		}

		public void onPlayLoadDataStart() {
			// TODO Auto-generated method stub
		}

		public void onPlayLoadDataEnd() {
			// TODO Auto-generated method stub
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onClick(View v) {

		if (v == mBtnGiveUp) {
			finish();
		} else if (v == mBtnResponse) {
			// sendMsg();
			Intent intent = new Intent(GetQuestionActivity.this, SolveQuestionActivity.class);
			startActivity(intent);
			finish();
		} else if (v == layoutQuestion) {
			layoutQuestion.setBackgroundColor(R.drawable.no2_searchresult_middle1);
			if (data.hasVoice()) {
				mGetQuestionActivityEngine.httpRequestNewThread(data.vId, RequestParam.FILE_TYPE_VOICE);
				// AsyncHttpDownloader download = new
				// AsyncHttpDownloader(mDownloadLisenter);
				// download.setParam(getString(R.string.file_host_ip),
				// UserInfo.appId, UserInfo.state.id + "");
				// download.download(data.vId, RequestParam.FILE_TYPE_VOICE);
			}
		}

	}

	@Override
	public void updateMesage() {
		// TODO Auto-generated method stub

	}

}
