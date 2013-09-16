package com.ztspeech.simutalk2.trans;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.JsonQuestion;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizer.speak.VoicePlayer;
import com.ztspeech.recognizer.speak.interf.OnPlayerListener;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialog;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.qa.PostVoiceDataToServerEngine;
import com.ztspeech.simutalk2.qa.view.InterpretView;

public class UserPropose extends BaseActivity implements OnClickListener {

	// Intent param define
	public static final int TYPE_NEW = 1;
	public static final int TYPE_OLD = 2;

	// view control
	private EditText mEditMsg;
	private Button mBtnSend;
	private Button mBtnSpeak;
	private Button mBtnCancel;
	private Button mBtnChOrEn;

	private UnisayRecognizerDialog mDialog = null;
	private VoicePlayer mPlayer = null;

	// data
	private boolean mHasVoice = false;
	private MsgGroupList mMsgGroupList = MsgGroupList.getInstance();
	private UserInfo mUser = UserInfo.getInstanse();
	private JsonQuestion mJsonMessage = new JsonQuestion();
	// private PostPackage mPostPackage;
	private int mVoiceLength = 0;
	private String mVoiceId = "";
	private static String language;
	private PostVoiceDataToServerEngine mPostVoiceDataToServerEngine;
	private Context context;
	private PostPackageEngine mPostPackageEngine;

	private InterpretView mInterpretView;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Util.ON_RECORD_END:
				mInterpretView.clearNoVoiceAnim();
				mInterpretView.clearViewAnim();
				mInterpretView.startViewAnim();
				mInterpretView.setBtnRecordEnable(false);
				break;
			case Util.ON_RECORD_BEGIN:
				mInterpretView.clearViewAnim();
				mInterpretView.startNoVoiceAnim();
				// btnDispProcess(true);
				mInterpretView.setBtnRecordEnable(true);
				break;
			case Util.ON_RECORDER_ERROR:
				mInterpretView.clearViewAnim();
				mInterpretView.setBtnRecordEnable(true);
				break;
			case Util.ON_WAIT_BEGIN:
				mInterpretView.setBtnRecordEnable(false);
				mInterpretView.clearViewAnim();
				mInterpretView.startViewAnim();
				break;
			case Util.ON_WAIT_END:
				mInterpretView.setBtnRecordEnable(true);
				// btnDispProcess(false);
				mInterpretView.clearViewAnim();
				break;
			case Util.ON_RECOGNIZER_ERROR:
				mInterpretView.clearViewAnim();
				mInterpretView.setBtnRecordEnable(true);
				mInterpretView.setTextStatusDisp(View.VISIBLE);
				mDialog.close();
				mInterpretView.setTextStatus((Integer) msg.obj);
				break;
			case Util.ON_VOICE_VALUE:
				mInterpretView.clearViewAnim();
				mInterpretView.setBtnRecordEnable(false);
				int value = (Integer) msg.obj;
				mInterpretView.setBtnRecordBg(value);
				break;
			case Util.SET_LIST_VIEW:
				mInterpretView.setBtnRecordEnable(true);
				mInterpretView.clearViewAnim();
				mDialog.close();
				mInterpretView.dispRresultList((ArrayList<String>) msg.obj);
				break;
			case Util.SELECT_RESULT:

				break;
			case 100:
				Toast.makeText(context, context.getString(R.string.cancelnet), Toast.LENGTH_LONG).show();
				mPostVoiceDataToServerEngine.dismissLoading();
				break;
			case 101:
				Toast.makeText(context, context.getString(R.string.qa_msg_download_error), Toast.LENGTH_LONG).show();
				mPostVoiceDataToServerEngine.dismissLoading();
				break;
			case 102:
				LogInfo.LogOutE("haitian", "AskQuestionActivity>>>>>>>>>>>>>>>>>>> msg.obj =" + (String) msg.obj);
				postVoiceDataCallBack((String) msg.obj);
				break;
			case 104:
				mPostVoiceDataToServerEngine.dismissLoading();
				LogInfo.LogOutE("haitian", "AskQuestionActivity>>>>>>>>>>>>>>>>>>> dismissLoading");
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

	private void btnDispProcess(boolean flag) {
		if (flag) {
			mInterpretView.setBtnCancelDisp(View.VISIBLE);
		} else {
			mInterpretView.setBtnCancelDisp(View.GONE);
		}
	}

	private boolean btnClickflag = false;

	// =========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.activity_userpropose, "意见反馈");
		context = this;
		mInterpretView = new InterpretView(this, handler, this);
		mDialog = new UnisayRecognizerDialog(this, "", mRecognizerListener,
				mInterpretView.mNewRecognizerViewListenerInterface);

		mPlayer = new VoicePlayer(this);
		mPlayer.setListener(mPlayerListener);
		mPostVoiceDataToServerEngine = new PostVoiceDataToServerEngine(context, handler);
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnCancel = (Button) findViewById(R.id.btnCancel);
		mBtnSpeak = (Button) findViewById(R.id.btnSpeak);
		mBtnChOrEn = (Button) findViewById(R.id.btnChOrEn);
		mEditMsg = (EditText) findViewById(R.id.editMsg);
		if (language == null) {
			language = mUser.s2sType;
		}

		if (language.equals(UserInfo.S2T_CH2EN)) {

			mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_z_f);
			mDialog.setToChineseEngine();
		} else {
			mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_e_f);
			mDialog.setToEnglishEngine();
		}

		mBtnSend.setOnClickListener(this);
		mBtnSpeak.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mBtnChOrEn.setOnClickListener(this);
		mEditMsg.addTextChangedListener(mOnTextChangedListener);
	}

	private OnPlayerListener mPlayerListener = new OnPlayerListener() {

		public InputStream getPlayWaveData(String text) {
			// TODO Auto-generated method stub
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

	private OnEngineListener mRecognizerListener = new OnEngineListener() {

		public void onEngineResult(List list, int flag, String id) {
			mDialog.close();
			mInterpretView.dismissPopWindow();
			// TODO Auto-generated method stub
			if (list == null) {
				mVoiceLength = 0;
				return;
			}
			// String text = "";
			for (int i = 0; i < list.size(); i++) {
				mEditMsg.append((String) list.get(i));
			}
			// mEditMsg.setText(text);

			if (list.size() > 0) {
				byte[] voice = (byte[]) mDialog.getObject();
				mVoiceLength = voice.length;
				mHasVoice = true;
			}

			mBtnSpeak.setEnabled(true);
		}

		public void onEngineStart() {
			// TODO Auto-generated method stub

		}

		public void onEngineEnd() {
			// TODO Auto-generated method stub
			mBtnSpeak.setEnabled(true);
		}
	};

	public void onClick(View v) {

		if (v == mBtnCancel) {
			finish();
		} else if (v == mBtnSend) {
			sendMsg();
		} else if (v == mBtnSpeak) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEditMsg.getWindowToken(), 0);
			// mBtnSpeak.setEnabled(false);
			mInterpretView.showPopWindowLocation();
			mDialog.show();
		} else if (v == mEditMsg) {

		} else if (v == mBtnChOrEn) {
			if (language.equals(UserInfo.S2T_CH2EN)) {
				mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_e_f);
				language = UserInfo.S2T_EN2CH;
				mDialog.setToEnglishEngine();
			} else {
				mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_z_f);
				language = UserInfo.S2T_CH2EN;
				mDialog.setToChineseEngine();
			}
		} else if (v.getId() == R.id.btn_bg || v.getId() == R.id.imagenovoiceanim) {
			mDialog.onRecognizerViewRecord();
			LogInfo.LogOutE("haitian", "---------------stop record------------");
		} else if (v.getId() == R.id.btn_cancel) {
			mDialog.onRecognizerViewCancel();
			mInterpretView.dismissPopWindow();
		} else if (v.getId() == R.id.btn_record) {
			if (!btnClickflag) {
				btnClickflag = true;
				mDialog.show();
				mInterpretView.setBtnRecordEnable(true);
			} else {
				btnClickflag = false;
				mDialog.close();
				mInterpretView.dismissPopWindow();
			}
		}
	}

	private void hideViewInput(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	private void sendMsg() {
		String msg = mEditMsg.getText().toString().trim();
		if (msg.length() == 0 && mHasVoice == false) {
			Util.showToast(this, "内容不能为空！");
			return;
		}

		if (mHasVoice) {

			byte[] voice = (byte[]) mDialog.getObject();
			// AsyncHttpPost post = new AsyncHttpPost(this);
			// post.setHost(getString(R.string.file_host_ip), "drt",
			// UserInfo.state.id + "");
			// post.postVoice(voice, RequestParam.FILE_TYPE_VOICE);
			// setButtonEnabled(false);

			mPostVoiceDataToServerEngine.httpRequestPostNewThread(voice, RequestParam.FILE_TYPE_VOICE);

		} else {
			postMsg(msg, "", 0);

		}
	}

	private void setButtonEnabled(boolean b) {
		// TODO Auto-generated method stub
		mBtnSend.setEnabled(b);
		mBtnSpeak.setEnabled(b);
	}

	protected boolean mMessageChanged = true;
	private TextWatcher mOnTextChangedListener = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			mMessageChanged = true;
		}

	};

	private void postMsg(String sText, String vId, int vLen) {

		// mPostPackage = new PostPackage(this, mPostTextListener);
		mJsonMessage.owner = 0;
		mJsonMessage.text = sText;
		mJsonMessage.type = "";
		mJsonMessage.vLen = vLen;
		mJsonMessage.vId = vId;

		if (mMessageChanged) {
			mMessageChanged = false;
			Util.handkey++;
		}

		// 握手
		mJsonMessage.handkey = Util.handkey;

		mJsonMessage.id = 0;
		mJsonMessage.cmd = JsonQuestion.SOLVE;

		mPostPackageEngine = new PostPackageEngine(context, mJsonMessage, handler);
		handler.sendEmptyMessage(104);
		mPostPackageEngine.post();

		// if (mPostPackage.post(mJsonMessage, getString(R.string.host_ip),
		// true)) {
		//
		// setButtonEnabled(false);
		// WaitingActivity.waiting(this, 0);
		// }
	}

	private void postVoiceDataCallBack(String fileId) {
		setButtonEnabled(true);
		if (fileId == null) {
			handler.sendEmptyMessage(104);
			return;
		}
		if (fileId.length() == 0) {
			handler.sendEmptyMessage(104);
			return;
		}

		byte[] voice = (byte[]) mDialog.getObject();
		String msg = mEditMsg.getText().toString().trim();
		postMsg(msg, fileId, voice.length);

	}

	private void postPackageCallBack(ResultPackage result) {
		if (result.isNetSucceed()) {
			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (ret != null) {
				if (ret.succeed == true) {
					mHasVoice = false;
					Util.handkey++;// 更新handkey
					finish();
					Toast.makeText(UserPropose.this, getResources().getString(R.string.userpropose_success),
							Toast.LENGTH_SHORT).show();
				} else {
					new AlertDialog.Builder(UserPropose.this).setTitle("提示").setMessage(ret.explain)
							.setPositiveButton("确定", null).show();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mDialog.close();
			mInterpretView.dismissPopWindow();
		}
		return super.onKeyDown(keyCode, event);
	}
	// public IHttpPostListener mPostTextListener = new IHttpPostListener() {
	//
	// @Override
	// public void onNetPostResult(PostPackage owner, ResultPackage result) {
	// // TODO Auto-generated method stub
	// if (result.isNetSucceed()) {
	// mHasVoice = false;
	// JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
	// if (ret != null) {
	// if (ret.succeed == true) {
	// finish();
	// } else {
	// new
	// AlertDialog.Builder(UserPropose.this).setTitle("提示").setMessage(ret.explain)
	// .setPositiveButton("确定", null).show();
	// }
	// }
	// }
	//
	// setButtonEnabled(true);
	// WaitingActivity.stop();
	//
	// }
	// };

	// @Override
	// public void onData(String fileId) {
	// // TODO Auto-generated method stub
	// setButtonEnabled(true);
	// if (fileId == null) {
	//
	// return;
	// }
	// if (fileId.length() == 0) {
	//
	// return;
	// }
	//
	// byte[] voice = (byte[]) mDialog.getObject();
	// String msg = mEditMsg.getText().toString().trim();
	// postMsg(msg, fileId, voice.length);
	// }
	//
	// @Override
	// public void onBegin() {
	// // TODO Auto-generated method stub
	// WaitingActivity.waiting(this, 0);
	// }
	//
	// @Override
	// public void onEnd() {
	// // TODO Auto-generated method stub
	// WaitingActivity.stop();
	// }

}
