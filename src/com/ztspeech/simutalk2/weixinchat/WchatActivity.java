package com.ztspeech.simutalk2.weixinchat;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.GetMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.ztspeech.recognizer.EngineResultFlag;
import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialog;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.qa.PostVoiceDataToServerEngine;
import com.ztspeech.simutalk2.qa.WaitingActivity;
import com.ztspeech.simutalk2.qa.view.InterpretView;
import com.ztspeech.translator.Translator;



@SuppressLint("NewApi")
public class WchatActivity extends Activity implements OnClickListener {

	// Intent param define
	public static final int TYPE_NEW = 1;
	public static final int TYPE_OLD = 2;

	// view control
	private EditText mEditMsg;
	private Button mBtnSend;
	private Button mBtnSpeak;
	private Button mBtnCancel;
	private Button mBtnChOrEn;

	private boolean isSpeaking;
	public static boolean isWorking = false;
	
	private UnisayRecognizerDialog mDialog = null;

	private ProgressDialog dialog;
	private Translator mTranslater;
	private UserInfo mUser = UserInfo.getInstanse();
	private int mVoiceLength = 0;
	private static String language;
	private PostVoiceDataToServerEngine mPostVoiceDataToServerEngine;
	private Context context;
	private PostPackageEngine mPostPackageEngine;
	protected boolean mMessageChanged = true;
	private Translator translator;
	
	private InterpretView2 mInterpretView;
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
				setBtnEnable(true);
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
				//postVoiceDataCallBack((String) msg.obj);
				break;
			case 104:
				mPostVoiceDataToServerEngine.dismissLoading();
				LogInfo.LogOutE("haitian", "AskQuestionActivity>>>>>>>>>>>>>>>>>>> dismissLoading");
				break;
			case 200:
				//postPackageCallBack((ResultPackage) msg.obj);
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
	private LayoutInflater inflater;
	private IWXAPI iapi;
	//private UserInfo userInfo = UserInfo.getInstanse();
	Bundle bundle;
	///private SpeakAdapter mSpeakAdapter;
	// =========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.popmore_show, R.anim.popmore_exit);
		//popMore.setAnimationStyle(R.style.popMoreAnimation);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.activity_chat, null);
		setContentView(v);
		iapi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
		this.bundle  = getIntent().getExtras();
		context = this;

		mInterpretView = new InterpretView2(this, handler ,v , this);

		mDialog = new UnisayRecognizerDialog(this, "", mRecognizerListener,
				mInterpretView.mNewRecognizerViewListenerInterface);
		
		translator = new Translator(this, mTranslaterListener);
		mPostVoiceDataToServerEngine = new PostVoiceDataToServerEngine(context, handler);
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnCancel = (Button) findViewById(R.id.btnCancel);
		mBtnSpeak = (Button) findViewById(R.id.btnSpeak);
		mEditMsg = (EditText) findViewById(R.id.editMsg);
		language = mUser.s2sType;

		if (language.equals(UserInfo.S2T_EN2CH)) {

			//mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_z_f);
			//mDialog.setToChineseEngine();
			mDialog.setToEN2CHEngine();
		} else{
			//mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_e_f);
			//mDialog.setToEnglishEngine();
			mDialog.setToCH2ENEngine();
			
		}

		mBtnSend.setOnClickListener(this);
		mBtnSpeak.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		mEditMsg.setOnKeyListener(mEditInputTextKeyListener);
		
		onClick(mBtnSpeak);
	}
	
	
	
	private OnKeyListener mEditInputTextKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == 66) {
				transInputText();
			}
			return false;
		}
	};
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mDialog.close();
			mInterpretView.setRecordLayoutDisp(View.GONE);
			setBtnEnable(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
	private OnEngineListener mRecognizerListener = new OnEngineListener() {

		public void onEngineResult(List list, int flag, String id) {
			mDialog.close();
			mInterpretView.setRecordLayoutDisp(View.GONE);
			setBtnEnable(true);
			if (list == null) {
				mVoiceLength = 0;
				return;
			}
			if (list.size() > 1) {
				mEditMsg.setText((String) list.get(0)+"\n"+(String) list.get(1));
				sendMsg();
			}
		}

		public void onEngineStart() {
			// TODO Auto-generated method stub
		}

		public void onEngineEnd() {
			setBtnEnable(true);
		}
	};

	private void setBtnEnable(boolean flag) {
		mBtnSpeak.setEnabled(flag);
		mEditMsg.setClickable(flag);
		//mBtnChOrEn.setEnabled(flag);
	}

	private void transInputText() {

		mCurrentTransText = mEditMsg.getText().toString().trim() + "";
		if (mCurrentTransText.length() > 0) {

			mEditMsg.setEnabled(false);
			
			if (UserInfo.S2T_CH2EN.equals(this.mUser.s2sType)) {

				this.mTranslater.transCH2EN(mCurrentTransText, false);
			} else {
				this.mTranslater.transEN2CH(mCurrentTransText, false);
			}
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditMsg.getWindowToken(), 0);
		
	}

	
	public void onClick(View v) {

		if (v == mBtnCancel) {
			finish();
		} else if (v == mBtnSend) {
			String msg = mEditMsg.getText().toString().trim();
			if(msg.length()>0){
				if (PublicArithmetic.chTandEnF(msg)) {
					translator.transCH2EN(msg, false);
					dialog = ProgressDialog.show(this, null, "正在连接请稍后...", false, true);
				}else
				{
					translator.transEN2CH(msg, false);
					dialog = ProgressDialog.show(this, null, "正在连接请稍后...", false, true);
				}
			}
			
		} else if (v == mBtnSpeak) {
			isSpeaking = true;
			hideViewInput(mEditMsg);
			mInterpretView.setRecordLayoutDisp(View.VISIBLE);
			mDialog.show();
			
			setBtnEnable(false);
			
		} else if (v == mEditMsg) {
			isSpeaking  = false;
		} else if (v == mBtnChOrEn) {
			if (language.equals(UserInfo.S2T_CH2EN)) {
				mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_e_f);
				language = UserInfo.S2T_EN2CH;
				//mDialog.setToEnglishEngine();
				mDialog.setToEN2CHEngine();
			} else {
				mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_z_f);
				language = UserInfo.S2T_CH2EN;
				//mDialog.setToChineseEngine();
				mDialog.setToCH2ENEngine();
			}
		} else if (v.getId() == R.id.btn_bg || v.getId() == R.id.imagenovoiceanim) {
			mDialog.onRecognizerViewRecord();
			LogInfo.LogOutE("haitian", "---------------stop record------------");
		} else if (v.getId() == R.id.btn_cancel) {

			mDialog.onRecognizerViewCancel();
			mInterpretView.setRecordLayoutDisp(View.GONE);
			setBtnEnable(true);
			// mInterpretView.dismissPopWindow();
		} else if (v.getId() == R.id.btn_record) {

			if (!btnClickflag) {
				btnClickflag = true;
				mDialog.show();
				mInterpretView.setBtnRecordEnable(true);
			} else {
				btnClickflag = false;
				mDialog.close();
				mInterpretView.setRecordLayoutDisp(View.GONE);
				setBtnEnable(true);
			}
			LogInfo.LogOutE("haitian", "------------R.id.btn_record----------------");
		}
	}

	private void hideViewInput(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	private void sendMsg() {
			
			
		String msg = mEditMsg.getText().toString().trim();

		if ((msg.length() == 0)|| (msg.length()<=0) ) {
			return;
		}
		WXTextObject localWXTextObject = new WXTextObject();
		localWXTextObject.text = msg;
		WXMediaMessage localWXMediaMessage = new WXMediaMessage(localWXTextObject);
		localWXMediaMessage.description = msg;
		GetMessageFromWX.Resp localResp = new GetMessageFromWX.Resp();
		localResp.transaction = getTransaction();
		localResp.message = localWXMediaMessage;
		this.iapi.sendResp(localResp);
		finish();
		
		
	
	}


	private String getTransaction() {
		final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
		return req.transaction;
	}
	
	
	private String mCurrentTransText = "";
			
	private OnEngineListener mTranslaterListener = new OnEngineListener() {
		
		public void onEngineResult(List list, int flag, String id) {

			if (flag == EngineResultFlag.OVER) {

			
				if(list ==null){
					return;
				}else if(list.size() >0){
					mCurrentTransText = list.get(0).toString();
					mEditMsg.append("\n"+mCurrentTransText);
					sendMsg();
				}
			}
			mEditMsg.setEnabled(true);
		}

		
		
		@Override
		public void onEngineStart() {
			//WaitingActivity.waiting(AskQuestionActivity.this, 0);
			isWorking = true;
		}
		
		
		
		@Override
		public void onEngineEnd() {
			mBtnSend.setEnabled(true);
			WaitingActivity.stop();
			isWorking = false;
		}
	};


	public void onBegin() {
		//WaitingActivity.waiting(this, 0);
	}

	public void onEnd() {

		//WaitingActivity.stop();
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.popmore_exit, R.anim.popmore_show);
	}
	

}
