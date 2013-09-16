package com.ztspeech.simutalk2.qa;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.Json;
import cn.ac.ia.directtrans.json.JsonMessage;
import cn.ac.ia.directtrans.json.JsonQuestion;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.QuestionInfo;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizer.speak.VoicePlayer;
import com.ztspeech.recognizer.speak.interf.OnPlayerListener;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialog;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.AskTaskList;
import com.ztspeech.simutalk2.data.GlobalData;
import com.ztspeech.simutalk2.data.MsgDataList;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.MsgInfoData.Define;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.data.UserInfoList;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.AsyncHttpDownloader.OnAsyncHttpDownloaderLisenter;
import com.ztspeech.simutalk2.net.ListViewImageEngine;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.qa.view.AdapterItemView;
import com.ztspeech.simutalk2.qa.view.DataListAdapter;
import com.ztspeech.simutalk2.qa.view.InterpretView;
import com.ztspeech.simutalk2.qa.view.SolveQuestionItemView;

public class SolveQuestionActivity extends UpdateBaseActivity implements OnClickListener {

	// Intent param define
	public static final String PARAM_TYPE = "type";
	public static final String PARAM_ID = "id";
	public static final int TYPE_ASK = 1;
	public static final int TYPE_SOLVE = 2;
	private static final int ACTIVITY_SOLVE = 200;
	// public static final int TYPE_ASK1 = 1;

	// view control
	private ListView mListView;
	private EditText mEditMsg;
	private Button mBtnGiveUp;
	private Button mBtnSend;
	private Button mBtnSpeak;
	private Button mBtnChOrEn;
	private LinearLayout mLayoutSender;
	private QuestionInfo mAskQuestion;
	private UnisayRecognizerDialog mDialog = null;
	private VoicePlayer mPlayer = null;

	// data
	private MsgGroupList mMsgGroupList = MsgGroupList.getInstance();
	private MsgDataList mShowList = new MsgDataList();
	private static boolean isShow = false;
	private static MsgDataList mMsgList;
	private JsonQuestion mJsonMessage = new JsonQuestion();
	// private PostPackage mPostPackage;
	private boolean mHasVoice = false;
	private static String language;
	private ListViewImageEngine listViewImageLoaderEngine;
	private Context context;
	private GetVoiceFromServerEngine mSolveQuestionActivity;
	private PostVoiceDataToServerEngine mPostVoiceDataToServerEngine;
	private PostPackageEngine mPostPackageEngine;
	private int postPackageType = 0;
	// =========================================================
	// private PopupWindow mRecognizerWindow = null;
	private InterpretView mInterpretView;
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
				break;
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
				LogInfo.LogOutE("haitian", "SolveQuestionActivity>>>>>>>>>>>>>>>>>>> msg.obj =" + (String) msg.obj);
				postVoiceDataCallBack((String) msg.obj);
				break;
			case 104:
				mPostVoiceDataToServerEngine.dismissLoading();
				LogInfo.LogOutE("haitian", "SolveQuestionActivity>>>>>>>>>>>>>>>>>>> dismissLoading");
				break;
			case 200:
				postPackageCallBack((ResultPackage) msg.obj, postPackageType);
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

	// =========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.activity_solve_q, null);
		setContentView(v);
		// getWindow().setFormat(PixelFormat.RGBA_8888);
		context = this;
		mInterpretView = new InterpretView(this, handler, v, this);
		mDialog = new UnisayRecognizerDialog(this, "", mRecognizerListener,
				mInterpretView.mNewRecognizerViewListenerInterface);
		mPlayer = new VoicePlayer(this);
		mPlayer.setListener(mPlayerListener);
		mSolveQuestionActivity = new GetVoiceFromServerEngine(context, mPlayer, handler);
		mPostVoiceDataToServerEngine = new PostVoiceDataToServerEngine(context, handler);
		mListView = (ListView) findViewById(R.id.lvQuestions);
		mLayoutSender = (LinearLayout) findViewById(R.id.layoutSender);
		mBtnGiveUp = (Button) findViewById(R.id.btnGiveUp);
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnSpeak = (Button) findViewById(R.id.btnSpeak);
		mEditMsg = (EditText) findViewById(R.id.editMsg);
		mBtnChOrEn = (Button) findViewById(R.id.btnChOrEn);

		mBtnGiveUp.setOnClickListener(this);
		mBtnSend.setOnClickListener(this);
		mBtnSpeak.setOnClickListener(this);
		// mEditMsg .setFocusableInTouchMode(false);
		mBtnChOrEn.setOnClickListener(this);
		mEditMsg.addTextChangedListener(mOnTextChangedListener);

		listViewImageLoaderEngine = new ListViewImageEngine(mListView);
		mListView.setAdapter(mListViewAdapter);
		mListView.setOnItemClickListener(mAdapterLinstener);
		mListView.setDividerHeight(0);

		mMsgList = null;
		getIntentData();

		if (language == null) {
			language = UserInfo.getInstanse().s2sType;
		}

		if (language.equals(UserInfo.S2T_CH2EN)) {

			mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_z_f);
			mDialog.setToChineseEngine();
		} else {
			mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_e_f);
			mDialog.setToEnglishEngine();
		}
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

	private void postPackageCallBack(ResultPackage result, int postPackageType) {
		if (result.isNetSucceed()) {
			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (postPackageType == 0) {
				if (ret != null) {
					if (ret.succeed == true) {
						mHasVoice = false;

						Util.handkey++;// 更新握手key值
						JsonMessage q = Json.fromJson(ret.json, JsonMessage.class);
						if (q != null) {

							mMsgList = mMsgGroupList.findItem(mAskQuestion.id, MsgInfoData.Define.TYPE_QA);
							if (mMsgList == null) {
								MsgInfoData data = new MsgInfoData();
								data.text = mAskQuestion.text;
								data.time = mAskQuestion.time;
								data.senderId = mAskQuestion.senderId;
								data.name = mAskQuestion.senderName;
								data.vLen = mAskQuestion.vLen;
								data.vId = mAskQuestion.vId;
								data.linkId = mAskQuestion.id;
								data.state = QuestionInfo.STATE_MARK;
								data.look_over = Define.LOOK_OVER;
								data.setCmd(JsonMessage.Function.SOLVED);

								mMsgGroupList.addMsg(data);
								mMsgGroupList.addMsgToDB(data);
								mMsgList = mMsgGroupList.findItem(mAskQuestion.id, MsgInfoData.Define.TYPE_QA);

								UserInfo.state.solve++;
								UserInfo.getInstanse().setChange(true);
							} else {
								mMsgList.setState(mAskQuestion.id, QuestionInfo.STATE_MARK);
							}

							AskTaskList list = GlobalData.getAskInstance();
							list.deleteTopTask();
							list.setChanged(true);

							// 用户信息统一管理

							MsgInfoData data = new MsgInfoData();
							data.setData(q);
							data.state = QuestionInfo.STATE_MARK;
							data.look_over = Define.LOOK_OVER;
							mMsgGroupList.addMsg(data);
							mMsgGroupList.addMsgToDB(data);
							mEditMsg.setText("");
							mMsgList.setChanged(true);
							updateMesage();
							Intent intent = new Intent(SolveQuestionActivity.this, UserStateActivity.class);
							// intent.putExtra("result", "succeed");
							SolveQuestionActivity.this.setResult(ACTIVITY_SOLVE, intent);
							finish();
							Toast.makeText(SolveQuestionActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
						}
						finish();
					} else {
						new AlertDialog.Builder(SolveQuestionActivity.this).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										finish();
									}
								}).show();
					}
				}

			} else if (postPackageType == 1) {
				if (ret != null) {
					if (ret.succeed == true) {

						// 删除放弃的问题
						AskTaskList list = GlobalData.getAskInstance();
						list.deleteTopTask();
						list.setChanged(true);

						// SolveQuestionActivity.this.finish();
						// Toast.makeText(context,
						// context.getString(R.string.userGiveUpQuestion),
						// Toast.LENGTH_LONG)
						// .show();
					} else {
						// SolveQuestionActivity.this.finish();
						// new
						// AlertDialog.Builder(SolveQuestionActivity.this).setTitle("提示")
						// .setMessage(ret.explain).setPositiveButton("确定",
						// null).show();
					}
				}
			}
		}
	}

	public static boolean isShowNotifitionTip(MsgDataList msgList) {
		if (isShow) {
			if (mMsgList == msgList) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isShow = true;
	}

	@Override
	protected void onPause() {

		super.onPause();
		isShow = false;
	}

	private void getIntentData() {

		mAskQuestion = UserStateActivity.mQuestionInfo;
		if (mAskQuestion != null) {
			MsgInfoData data = new MsgInfoData();
			data.name = mAskQuestion.senderName;
			data.time = mAskQuestion.time;
			data.text = mAskQuestion.text;
			data.vLen = mAskQuestion.vLen;
			data.vId = mAskQuestion.vId;
			data.senderId = mAskQuestion.senderId;
			mShowList.add(data);
			UserInfoList.getInstanse().update(mAskQuestion);
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

	private DataListAdapter mListViewAdapter = new DataListAdapter(this, mShowList) {
		@Override
		public AdapterItemView getAdapterItemView(Context context) {
			// TODO Auto-generated method stub
			return new SolveQuestionItemView(context, null, listViewImageLoaderEngine);
		}
	};
	@SuppressWarnings("unused")
	private OnAsyncHttpDownloaderLisenter mDownloadLisenter = new OnAsyncHttpDownloaderLisenter() {

		@Override
		public void onData(byte[] data) {

			if (data == null) {
				Toast.makeText(SolveQuestionActivity.this, getString(R.string.qa_msg_download_error),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (data.length < 1000) {
				Toast.makeText(SolveQuestionActivity.this, getString(R.string.qa_msg_download_error),
						Toast.LENGTH_SHORT).show();
				return;
			}
			InputStream s = new ByteArrayInputStream(data);
			mPlayer.play(s);
		}

		@Override
		public void onBegin() {

			WaitingActivity.waiting(SolveQuestionActivity.this, 0);
		}

		@Override
		public void onEnd() {

			WaitingActivity.stop();
		}

	};

	private OnItemClickListener mAdapterLinstener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			SolveQuestionItemView v = (SolveQuestionItemView) arg1;
			MsgInfoData data = (MsgInfoData) v.getData();
			if (data.hasVoice()) {
				mSolveQuestionActivity.httpRequestNewThread(data.vId, RequestParam.FILE_TYPE_VOICE);
				// AsyncHttpDownloader download = new
				// AsyncHttpDownloader(mDownloadLisenter);
				// download.setParam(getString(R.string.file_host_ip),
				// UserInfo.appId, UserInfo.state.id + "");
				// download.download(data.vId, RequestParam.FILE_TYPE_VOICE);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void setBtnEnable(boolean flag) {
		mBtnSpeak.setEnabled(flag);
		mEditMsg.setClickable(flag);
		mBtnChOrEn.setEnabled(flag);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == mBtnGiveUp) {
			// finish();
			GiveUpQuestion();
			setBtnEnable(true);
			SolveQuestionActivity.this.finish();
		} else if (v == mBtnSend) {
			sendMsg();
		} else if (v == mBtnSpeak) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEditMsg.getWindowToken(), 0);
			// mBtnSpeak.setEnabled(false);
			mInterpretView.setRecordLayoutDisp(View.VISIBLE);
			setBtnEnable(false);
			mDialog.show();
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
		} else if (v == mEditMsg) {

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

	private void sendMsg() {
		// TODO Auto-generated method stub

		String msg = mEditMsg.getText().toString().trim();
		if (msg.length() == 0 && mHasVoice == false) {
			return;
		}

		if (mHasVoice) {

			byte[] voice = (byte[]) mDialog.getObject();
			mPostVoiceDataToServerEngine.httpRequestPostNewThread(voice, RequestParam.FILE_TYPE_VOICE);
			// AsyncHttpPost post = new AsyncHttpPost(this);
			// post.setHost(getString(R.string.file_host_ip), "drt",
			// UserInfo.state.id + "");
			// post.postVoice(voice, RequestParam.FILE_TYPE_VOICE);
			// setButtonEnabled(false);
		} else {
			postMsg(msg, "", 0);

		}
	}

	private void setButtonEnabled(boolean b) {
		// TODO Auto-generated method stub
		mBtnSend.setEnabled(b);
		mBtnSpeak.setEnabled(b);
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
		// mPostPackage = new PostPackage(this, mPostTextListener);
		mJsonMessage.owner = mAskQuestion.senderId;
		mJsonMessage.text = msg;
		mJsonMessage.type = "";
		mJsonMessage.vLen = voice.length;
		mJsonMessage.vId = fileId;

		mJsonMessage.id = mAskQuestion.id;
		mJsonMessage.cmd = JsonQuestion.SOLVE;

		if (mMessageChanged) {
			mMessageChanged = false;
			Util.handkey++;
		}

		// 握手key值
		mJsonMessage.handkey = Util.handkey;

		postPackageType = 0;
		mPostPackageEngine = new PostPackageEngine(context, mJsonMessage, handler);
		mPostPackageEngine.post();
		handler.sendEmptyMessage(104);

		// if (mPostPackage.post(mJsonMessage, getString(R.string.host_ip),
		// true)) {
		// setButtonEnabled(false);
		// } else {
		// setButtonEnabled(true);
		// LogInfo.LogOutE("haitian",
		// "-----------SolveQuestionActivity-----------");
		// handler.sendEmptyMessage(104);
		// }
	}

	private void postMsg(String sText, String vId, int vLen) {

		// mPostPackage = new PostPackage(this, mPostTextListener);
		mJsonMessage.owner = mAskQuestion.senderId;
		mJsonMessage.text = sText;
		mJsonMessage.type = "";
		mJsonMessage.vLen = vLen;
		mJsonMessage.vId = vId;

		mJsonMessage.id = mAskQuestion.id;
		mJsonMessage.cmd = JsonQuestion.SOLVE;

		if (mMessageChanged) {
			mMessageChanged = false;
			Util.handkey++;
		}

		// 握手key值
		mJsonMessage.handkey = Util.handkey;

		postPackageType = 0;
		mPostPackageEngine = new PostPackageEngine(context, mJsonMessage, handler);
		mPostPackageEngine.post();

		// if (mPostPackage.post(mJsonMessage, getString(R.string.host_ip),
		// true)) {
		//
		// setButtonEnabled(false);
		// WaitingActivity.waiting(this, 0);
		// }
	}

	private OnEngineListener mRecognizerListener = new OnEngineListener() {

		@SuppressWarnings("rawtypes")
		public void onEngineResult(List list, int flag, String id) {
			mDialog.close();
			mInterpretView.setRecordLayoutDisp(View.GONE);
			setBtnEnable(true);
			if (list == null) {
				return;
			}
			for (int i = 0; i < list.size(); i++) {
				mHasVoice = true;
			}
			// String text = "";
			for (int i = 0; i < list.size(); i++) {
				mEditMsg.append((String) list.get(i));
			}
			// mEditMsg.setText(text);
		}

		public void onEngineStart() {
			// TODO Auto-generated method stub

		}

		public void onEngineEnd() {
			// TODO Auto-generated method stub
			setBtnEnable(true);
		}
	};

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
	//
	// JsonMessage q = Json.fromJson(ret.json, JsonMessage.class);
	// if (q != null) {
	//
	// mMsgList = mMsgGroupList.findItem(mAskQuestion.id,
	// MsgInfoData.Define.TYPE_QA);
	// if (mMsgList == null) {
	// MsgInfoData data = new MsgInfoData();
	// data.text = mAskQuestion.text;
	// data.time = mAskQuestion.time;
	// data.senderId = mAskQuestion.senderId;
	// data.name = mAskQuestion.senderName;
	// data.vLen = mAskQuestion.vLen;
	// data.vId = mAskQuestion.vId;
	// data.linkId = mAskQuestion.id;
	// data.photo = mAskQuestion.photo;
	// data.state = QuestionInfo.STATE_MARK;
	// data.look_over = Define.LOOK_OVER;
	// data.setCmd(JsonMessage.Function.SOLVED);
	//
	// mMsgGroupList.addMsg(data);
	// mMsgGroupList.addMsgToDB(data);
	// mMsgList = mMsgGroupList.findItem(mAskQuestion.id,
	// MsgInfoData.Define.TYPE_QA);
	//
	// UserInfo.state.solve++;
	// UserInfo.getInstanse().setChange(true);
	// } else {
	// mMsgList.setState(mAskQuestion.id, QuestionInfo.STATE_MARK);
	// }
	//
	// MsgInfoData data = new MsgInfoData();
	// data.setData(q);
	// data.state = QuestionInfo.STATE_MARK;
	// data.look_over = Define.LOOK_OVER;
	// mMsgGroupList.addMsg(data);
	// mMsgGroupList.addMsgToDB(data);
	// mEditMsg.setText("");
	// mMsgList.setChanged(true);
	// updateMesage();
	// handler.sendEmptyMessage(104);
	// LogInfo.LogOutE("haitian",
	// "1-----------SolveQuestionActivity-----------");
	// finish();
	// Toast.makeText(SolveQuestionActivity.this, "回复成功",
	// Toast.LENGTH_SHORT).show();
	// }
	// } else {
	// LogInfo.LogOutE("haitian",
	// "2-----------SolveQuestionActivity-----------");
	// handler.sendEmptyMessage(104);
	// new
	// AlertDialog.Builder(SolveQuestionActivity.this).setTitle("提示").setMessage(ret.explain)
	// .setPositiveButton("确定", null).show();
	// }
	// }
	// }
	// LogInfo.LogOutE("haitian",
	// "3-----------SolveQuestionActivity-----------");
	// handler.sendEmptyMessage(104);
	// setButtonEnabled(true);
	// WaitingActivity.stop();
	//
	// }
	// };

	// @Override
	// public void onNetPostResult(PostPackage owner, ResultPackage result) {
	// // TODO Auto-generated method stub
	//
	// if (result.isNetSucceed()) {
	// mHasVoice = false;
	//
	// JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
	// if (ret != null) {
	// if (ret.succeed == true) {
	//
	// ProcessMessage pro = ProcessMessage.getInstance();
	// pro.process(ret);
	// updateMesage();
	// } else {
	// new
	// AlertDialog.Builder(this).setTitle("提示").setMessage(ret.explain).setPositiveButton("确定",
	// null)
	// .show();
	// }
	// }
	// } else {
	//
	// }
	//
	// mBtnSend.setEnabled(true);
	// WaitingActivity.stop();
	// finish();
	// }

	@Override
	public void updateMesage() {
		// TODO Auto-generated method stub
		if (mMsgList != null) {

			if (mMsgList.isChanged()) {
				mMsgList.setChanged(false);
				mMsgGroupList.setMsgChanged(true);

				int visible = View.GONE;
				if (mMsgList.enabled()) {
					visible = View.VISIBLE;
				}
				if (mLayoutSender.getVisibility() != visible) {
					mLayoutSender.setVisibility(visible);
				}

				mShowList.clear();
				mMsgList.getList(mShowList);
				mListViewAdapter.notifyDataSetChanged();
				int nSelection = mListViewAdapter.getCount() - 1;
				if (nSelection > -1) {
					mListView.setSelection(nSelection);
				}
			}
		}
	}

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
	// WaitingActivity.waiting(this, 0);
	// }
	//
	// @Override
	// public void onEnd() {
	//
	// WaitingActivity.stop();
	// }

	// public IHttpPostListener mGiveUpSolveListener = new IHttpPostListener() {
	//
	// @Override
	// public void onNetPostResult(PostPackage owner, ResultPackage result) {
	//
	// if (result.isNetSucceed()) {
	//
	// JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
	// if (ret != null) {
	// if (ret.succeed == true) {
	// // SolveQuestionActivity.this.finish();
	//
	// } else {
	// // SolveQuestionActivity.this.finish();
	// // new
	// // AlertDialog.Builder(SolveQuestionActivity.this).setTitle("提示")
	// // .setMessage(ret.explain).setPositiveButton("确定",
	// // null).show();
	//
	// }
	// }
	// }
	// // SolveQuestionActivity.this.finish();
	// }
	// };
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mDialog.close();
			mInterpretView.setRecordLayoutDisp(View.GONE);
			setBtnEnable(true);
			// mInterpretView.dismissPopWindow();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void GiveUpQuestion() {

		// mPostPackage = new PostPackage(this, mGiveUpSolveListener);
		mJsonMessage.cmd = JsonQuestion.GIVE_UP;
		mJsonMessage.type = "";
		updateMesage();

		postPackageType = 1;
		mPostPackageEngine = new PostPackageEngine(context, mJsonMessage, handler);
		mPostPackageEngine.post(true);

		// if (mPostPackage.post(mJsonMessage, getString(R.string.host_ip),
		// true)) {
		// // WaitingActivity.waiting(this , 0);
		// }
	}

	@Override
	protected void onStop() {
		if (TextPlayer.getInstance().isPlaying()) {
			TextPlayer.getInstance().stop();
		}
		super.onStop();
	}
}
