package com.ztspeech.simutalk2.qa;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.JsonEditLinkman;
import cn.ac.ia.directtrans.json.JsonMessage;
import cn.ac.ia.directtrans.json.JsonQuestion;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.JsonSendMessage;
import cn.ac.ia.directtrans.json.QuestionInfo;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.recognizer.EngineResultFlag;
import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizer.speak.VoicePlayer;
import com.ztspeech.recognizer.speak.interf.OnPlayerListener;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialog;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.FriendDataList;
import com.ztspeech.simutalk2.data.MsgDataList;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.ListViewImageEngine;
import com.ztspeech.simutalk2.net.PostPackage;
import com.ztspeech.simutalk2.net.PostPackage.IHttpPostListener;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.qa.message.ProcessMessage;
import com.ztspeech.simutalk2.qa.view.AdapterItemView;
import com.ztspeech.simutalk2.qa.view.DataListAdapter;
import com.ztspeech.simutalk2.qa.view.InterpretView;
import com.ztspeech.simutalk2.qa.view.MessageItemView;
import com.ztspeech.simutalk2.qa.view.MessageItemView.OnMessageClickListener;
import com.ztspeech.simutalk2.trans.speak.SpeakItemData;
import com.ztspeech.translator.Translator;

public class MessageActivity extends UpdateBaseActivity implements OnClickListener, IHttpPostListener, OnTouchListener {

	// Intent param define
	public static final int NOTIFICATION_ID = 10;
	private static final int DO_ASK = 0;
	private static final int DO_SOLVE = 1;
	private static final int DO_MSG = 2;
	private static final int GET_LASTMSG = 100001;
	// view control
	private ListView mListView;
	private EditText mEditMsg;
	private TextView mtvTitle;
	private Button mBtnReturn;
	private Button mBtnSend;
	private Button mBtnAddFriend;
	private Button mBtnSpeak;
	private Button mBtnChOrEn;

	private ProcessMessage mProcessMessage = ProcessMessage.getInstance();
	private FriendDataList mFriends = FriendDataList.getInstance();
	private LinearLayout mLayoutSender;
	private UnisayRecognizerDialog mDialog = null;
	private Translator mTranslater = null;
	private VoicePlayer mPlayer = null;
	private int mListViewHeight = 0;

	// private TTSPlayer mTtsPlayer = null;
	// private LocaleTTS mTts = LocaleTTS.getInstance();

	// data
	private MsgGroupList mMsgGroupList = MsgGroupList.getInstance();
	private JsonEditLinkman mJsonAddFriend = new JsonEditLinkman();
	private JsonSendMessage mJsonMessage = new JsonSendMessage();
	private JsonQuestion mJsonQuestion = new JsonQuestion();
	private static MsgDataList mMsgList;
	private MsgDataList mViewDataList = new MsgDataList();
	private DataListAdapter mListViewAdapter = null;
	// private PostPackage mPostPackage;
	private int mLinkId = 0;
	private int mType = 0;
	private int mDoing = DO_MSG;
	private boolean mHasVoice = false;
	private static boolean isShow = false;
	private static String language;
	private ListViewImageEngine listViewImageLoaderEngine;

	private GetVoiceFromServerEngine mMessageActivityEngine;
	private PostVoiceDataToServerEngine mPostVoiceDataToServerEngine;
	private PostPackageEngine mPostPackageEngine;
	private Context context;
	private int postPackageType = 0;

	
	
	private int nsize = 0; 
	
	public static boolean isShowNotifitionTip(MsgDataList msgList) {

		if (isShow) {
			if (mMsgList == msgList) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onStart() {

		super.onStart();
		isShow = true;
		mProcessMessage.setTalking(true);
	}

	@Override
	protected void onStop() {
		if (TextPlayer.getInstance().isPlaying()) {
			TextPlayer.getInstance().stop();
		}
		super.onStop();
		mProcessMessage.setTalking(false);
		isShow = false;
	}

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
				LogInfo.LogOutE("haitian", "MessageActivity>>>>>>>>>>>>>>>>>>> msg.obj =" + (String) msg.obj);
				postVoiceDataCallBack((String) msg.obj);
				break;
			case 104:
				mPostVoiceDataToServerEngine.dismissLoading();
				LogInfo.LogOutE("haitian", "MessageActivity>>>>>>>>>>>>>>>>>>> dismissLoading");
				break;
			case 200:
				postPackageCallBack((ResultPackage) msg.obj, postPackageType);
				break;
			case 404:
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			case GET_LASTMSG://播放最后一条消息
				if(UserInfo.getInstanse().autoTTS&&UserInfo.getInstanse().isTranslatetalk()){
					MsgInfoData item = (MsgInfoData) msg.obj;
					String[] str = item.text.split("/");
					if(str.length>1&&item.senderId != UserInfo.state.id){
						if(item.hasVoice()){
							if(!mTextPlayer.isLoadingData){
								speakStr(str[1]);
							}
						}
					}
				}
				break;
			default:
				break;
			}

		};
	};

	private boolean btnClickflag = false;

	// =========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.slideinright, R.anim.slideoutleft);
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message);
		context = this;
		// getWindow().setFormat(PixelFormat.RGBA_8888);
		mInterpretView = new InterpretView(this, handler, this);
		mTranslater = new Translator(this, mTranslaterListener);
		mDialog = new UnisayRecognizerDialog(this, "", mRecognizerListener,
				mInterpretView.mNewRecognizerViewListenerInterface);

		mPlayer = new VoicePlayer(this);
		mPlayer.setListener(mPlayerListener);
		mMessageActivityEngine = new GetVoiceFromServerEngine(context, mPlayer, handler);
		mPostVoiceDataToServerEngine = new PostVoiceDataToServerEngine(context, handler);
		mListView = (ListView) findViewById(R.id.lvQuestion);
		mBtnReturn = (Button) findViewById(R.id.btnReturn);
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnSpeak = (Button) findViewById(R.id.btnSpeak);
		mBtnAddFriend = (Button) findViewById(R.id.btnAddFriend);
		mEditMsg = (EditText) findViewById(R.id.editMsg);
		mtvTitle = (TextView) findViewById(R.id.tvMessageTitle);
		mLayoutSender = (LinearLayout) findViewById(R.id.layoutSender);
		mBtnChOrEn = (Button) findViewById(R.id.btnChOrEn);

		mEditMsg.setOnTouchListener(this);
		mBtnReturn.setOnClickListener(this);
		mBtnSend.setOnClickListener(this);
		mBtnAddFriend.setOnClickListener(this);
		mBtnSpeak.setOnClickListener(this);
		mBtnChOrEn.setOnClickListener(this);
		mBtnReturn.setText("");

		// mEditMsg.setFocusableInTouchMode(false);

		mMsgList = null;
		initListView();

		getIntentData();
		// 取消 消息提示
		NotificationManager noticedManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (noticedManager != null) {
			noticedManager.cancel(NOTIFICATION_ID);
		}

		if (language == null) {
			language = UserInfo.getInstanse().s2sType;
		}

		if (language.equals(UserInfo.S2T_CH2EN)) {

			mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_z_f);
			//mDialog.setToChineseEngine();
			mDialog.setToCH2ENEngine();
		} else {
			mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_e_f);
			//mDialog.setToEnglishEngine();
			mDialog.setToEN2CHEngine();
		}

		mEditMsg.addTextChangedListener(mOnTextChangedListener);
	}

	protected boolean mMessageChanged = true;

	private TextWatcher mOnTextChangedListener = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable arg0) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			mMessageChanged = true;
		}

	};

	public void setEditTextModel() {
		if (mListViewHeight == 0) {
			// int w =
			// View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			// int h =
			// View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
			// mListView.measure(w, h);
			// mListViewHeight = mListView.getMeasuredHeight();
			mListViewHeight = mListView.getHeight();
			// imm = (InputMethodManager)
			// getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		handler.postDelayed(mKeyboardRunnable, 100);
	}

	private Runnable mKeyboardRunnable = new Runnable() {
		// s
		public void run() {
			// InputMethodManager imm = (InputMethodManager)
			// getSystemService(INPUT_METHOD_SERVICE);
			int h = mListView.getHeight();
			if (mListViewHeight > h) {
				if (mBtnSpeak.getVisibility() == View.VISIBLE) {
					mBtnSpeak.setVisibility(View.GONE);
					mBtnChOrEn.setVisibility(View.GONE);
					// controlLayout.setMinimumHeight(120);
					// mEditInput.setLines(2);
					// mEditInput.setMaxLines(2);
					// mEditInput.setSingleLine(false);
				}
			} else {
				if (mBtnSpeak.getVisibility() == View.GONE) {
					// mEditInput.setLines(1);
					// mEditInput.setMaxLines(1);
					// mEditInput.setSingleLine(true);
					mBtnSpeak.setVisibility(View.VISIBLE);
					mBtnChOrEn.setVisibility(View.VISIBLE);

				}
			}

			handler.postDelayed(mKeyboardRunnable, 100);
		}
	};

	private OnPlayerListener mPlayerListener = new OnPlayerListener() {

		public InputStream getPlayWaveData(String text) {

			// InputStream in = getTTSInputStream(text);
			// InputStream in = mTtsDownloader.getTTSInputStream(text);

			return null;
		}

		public void onPlayStart() {

		}

		public void onPlayStop() {

		}

		public void onPlayLoadDataStart() {

		}

		public void onPlayLoadDataEnd() {

		}
	};

	private String transResult = null;
	private OnEngineListener mTranslaterListener = new OnEngineListener(){

		@Override
		public void onEngineEnd() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onEngineResult(List arg0, int arg1, String arg2) {
			if (arg1 == EngineResultFlag.OVER) {
				if(arg0==null){
					return;
				}
				if(arg0.size()>0){
					String msg = mEditMsg.getText().toString().trim();
					postMsg(msg, arg0.get(0).toString(),  "", 0);
				}
			} else if (arg1 == EngineResultFlag.NOTHING) {

			} else if (arg1 == EngineResultFlag.CONNECT_ERROR) {

			} else if (arg1 == EngineResultFlag.CANCEL) {
				// doError(flag);
			} else {

			}
			
		}

		@Override
		public void onEngineStart() {
			// TODO Auto-generated method stub
			
		}
		
	};
	private OnEngineListener mRecognizerListener = new OnEngineListener() {

		public void onEngineResult(List list, int flag, String id) {
			mDialog.close();
			mInterpretView.dismissPopWindow();
			mBtnSpeak.setEnabled(true);

			if (list == null) {
				return;
			}
			if(list.size()>0){
				mHasVoice = true;
				mEditMsg.append((String) list.get(0));
				transResult = null;
			}
			if(list.size()>1){
				transResult= (String) list.get(1);
			}
			// mEditMsg.setText(text);

		}

		public void onEngineStart() {

		}

		public void onEngineEnd() {

			mBtnSpeak.setEnabled(true);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, getString(R.string.qa_msg_menu_clear));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			clearMessage();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void clearMessage() {

		mMsgList = mMsgGroupList.findItem(mLinkId, mType);
		if (mMsgList != null) {
			mMsgGroupList.setMsgChanged(true);
			mMsgList.clear();
		}

		updateListView(true);
	}

	private void getIntentData() {

		mBtnAddFriend.setVisibility(View.INVISIBLE);
		mtvTitle.setBackgroundResource(R.drawable.qa_top_button_03);
		mDoing = DO_MSG;

		Intent intent = this.getIntent();
		long id = intent.getLongExtra(MsgGroupList.PARAM_ID, 0);
		mType = intent.getIntExtra(MsgGroupList.PARAM_TYPE, 0);
		mLinkId = (int) id;

		mMsgList = mMsgGroupList.findItem(mLinkId, mType);
		if (mMsgList == null) {
			return;
		}

		if (mType == MsgInfoData.Define.TYPE_QA) {

			int ownerId = mMsgList.getOwnerId();
			// mBtnMsgClose.setText(getString(R.string.qa_msg_btn_add_friend));
			// mBtnAddFriend.setVisibility(View.INVISIBLE);

			if (ownerId == UserInfo.state.id) {

				mDoing = DO_ASK;
				if (mMsgList.closed()) {
					mtvTitle.setBackgroundResource(R.drawable.qa_you_record_button_normal01);
				} else {
					mtvTitle.setBackgroundResource(R.drawable.qa_you_record_button_normal01);
				}
			} else {

				mDoing = DO_SOLVE;
				if (mMsgList.closed()) {
					mtvTitle.setBackgroundResource(R.drawable.qa_you_record_button_normal02);
				} else {
					mtvTitle.setBackgroundResource(R.drawable.qa_you_record_button_normal02);
				}
			}
		}

		updateListView(true);
	}

	@Override
	public void finish() {

		if (mMsgList != null) {
			mMsgGroupList.setMsgChanged(true);
			mMsgList.setAllLookOver();
		}

		mMsgList = null;
		MsgGroupListActivity.isViewUpdate = true;
		setResult(RESULT_OK, null);
		super.finish();

		overridePendingTransition(R.anim.slideinleft, R.anim.slideoutright);
	}

	private void updateListView(boolean update) {

		if (mMsgList == null) {
			mMsgList = mMsgGroupList.findItem(mLinkId, mType);
			if (mMsgList == null) {
				return;
			}
		}

		if (mMsgList.mIsChanged || update) {
			mMsgList.mIsChanged = false;
			mMsgGroupList.setMsgChanged(true);
			int visible = View.GONE;
			if (mMsgList.enabled()) {
				visible = View.VISIBLE;
			}
			if (mLayoutSender.getVisibility() != visible) {
				mLayoutSender.setVisibility(visible);
			}
			if (mDoing != DO_MSG) {

				visible = View.VISIBLE;
				if (mMsgList.closed()) {
					visible = View.INVISIBLE;
				}

				visible = View.INVISIBLE;
				MsgInfoData msg = mMsgList.getLinkman(UserInfo.state.id);
				if (msg != null) {
					if (null == mFriends.findById(msg.senderId)) {
						visible = View.VISIBLE;
					} else {
						visible = View.INVISIBLE;
					}
				}
				if (mBtnAddFriend.getVisibility() != visible) {
					mBtnAddFriend.setVisibility(visible);
				}
			}
			mViewDataList.clear();
			mMsgList.getList(mViewDataList);
			mListViewAdapter.notifyDataSetChanged();
			int nSelection = mListViewAdapter.getCount() - 1;
			if (nSelection > -1) {
				mListView.setSelection(nSelection);
				//将最后一条发送到主线程
				
				if(nsize!=0){
					Message msg = new Message();
					msg.what = GET_LASTMSG;
					msg.obj = mViewDataList.get(mViewDataList.size()-1);
					handler.sendMessage(msg);
				}
				nsize = mViewDataList.size();
			
			}
			
		}
	}

	public IHttpPostListener mSetQusetionListener = new IHttpPostListener() {

		@Override
		public void onNetPostResult(PostPackage owner, ResultPackage result) {

			if (result.isNetSucceed()) {

				JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
				if (ret != null) {
					if (ret.succeed == true) {

						mMsgGroupList.setState(mLinkId, QuestionInfo.STATE_CLOSE);
						finish();
					} else {
						new AlertDialog.Builder(MessageActivity.this).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", null).show();
					}
				}
			}

			mBtnSend.setEnabled(true);
			WaitingActivity.stop();
		}

		@Override
		public void isShowTipDialog(String msg) {
		}
	};

	private TextPlayer mTextPlayer = TextPlayer.getInstance();
	public void speakStr(String str) {
		int result = new PublicArithmetic().isWhat(str);
		switch (result) {
		case 0:
		case 3:
			mTextPlayer.setPopContext(context);
			if (mTextPlayer.isPlaying()) {
				mTextPlayer.stop();
			} else {
				mTextPlayer.playChinese(str);
			}
			break;
		case 1:
		case 2:
			mTextPlayer.setPopContext(context);
			if (mTextPlayer.isPlaying()) {
				mTextPlayer.stop();
			} else {
				mTextPlayer.playEnglish(str);
			}
			break;
		}
	}
	
	private OnMessageClickListener mItemLinstener = new OnMessageClickListener() {

		@Override
		public void OnClick(String cmd, MsgInfoData data) {
			if ("play".equals(cmd)) {
				mMessageActivityEngine.httpRequestNewThread(data.vId, RequestParam.FILE_TYPE_VOICE);

			}
		}
	};

	private void initListView() {

		listViewImageLoaderEngine = new ListViewImageEngine(mListView);
		mListViewAdapter = new DataListAdapter(this, mViewDataList) {
			@Override
			public AdapterItemView getAdapterItemView(Context context) {

				return new MessageItemView(context, mItemLinstener, listViewImageLoaderEngine);
			}
		};

		mListView.setAdapter(mListViewAdapter);
		mListView.setOnItemClickListener(mAdapterLinstener);
		mListView.setDividerHeight(0);
	}

	private OnItemClickListener mAdapterLinstener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			MessageItemView v = (MessageItemView) arg1;
			MsgInfoData data = (MsgInfoData) v.getData();
			if (data.hasVoice()) {
				if(UserInfo.getInstanse().isTranslatetalk()){
					String[] text = data.text.split("/");
					if(text.length>1){
						speakStr(text[1]);
					}else{
						mMessageActivityEngine.httpRequestNewThread(data.vId, RequestParam.FILE_TYPE_VOICE);
					}
					
				}else{
					mMessageActivityEngine.httpRequestNewThread(data.vId, RequestParam.FILE_TYPE_VOICE);
				}
				// AsyncHttpDownloader download = new
				// AsyncHttpDownloader(mDownloadLisenter);
				// download.setParam(getString(R.string.file_host_ip),
				// UserInfo.appId, UserInfo.state.id + "");
				// download.download(data.vId, RequestParam.FILE_TYPE_VOICE);
			}
		}
	};

	public void onClick(View v) {

		if (v == mBtnReturn) {
			finish();
		} else if (v == mBtnSend) {
			sendMsg();

		} else if (v == mBtnAddFriend) {
			switch (mDoing) {
			case DO_ASK:
				addFriend();
				break;
			case DO_SOLVE:
				addFriend();
				break;
			case DO_MSG:
				// CloseMessage();
				break;
			default:

			}
		} else if (v == mBtnSpeak) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEditMsg.getWindowToken(), 0);
			// mBtnSpeak.setEnabled(false);
			mInterpretView.showPopWindowLocation();
			mDialog.show();
		} else if (v == mBtnChOrEn) {
			if (language.equals(UserInfo.S2T_CH2EN)) {
				mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_e_f);
				language = UserInfo.S2T_EN2CH;
				mDialog.setToEN2CHEngine();
			} else {
				mBtnChOrEn.setBackgroundResource(R.drawable.qa_btn_z_f);
				language = UserInfo.S2T_CH2EN;
				mDialog.setToCH2ENEngine();
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
		} else if (v == mEditMsg) {
			/*
			 * if(mIsInputTextModel) {
			 * 
			 * } else { mBtnSpeak.setEnabled(false); mDialog.show(); }
			 */
		}
	}

	private void addFriend() {

		MsgInfoData msg = mMsgList.getLinkman(UserInfo.state.id);
		if (msg == null) {
			return;
		}

		// mPostPackage = new PostPackage(this, mAddFriendListener);
		mJsonAddFriend.setInviteLinkman(msg.senderId);
		mJsonAddFriend.name = msg.name;

		postPackageType = 2;
		mPostPackageEngine = new PostPackageEngine(context, mJsonAddFriend, handler);
		mPostPackageEngine.post();

		// if (mPostPackage.post(mJsonAddFriend, getString(R.string.host_ip),
		// true)) {
		//
		// WaitingActivity.waiting(this, 0);
		// }
	}

	// public IHttpPostListener mAddFriendListener = new IHttpPostListener() {
	//
	// @Override
	// public void onNetPostResult(PostPackage owner, ResultPackage result) {
	//
	// if (result.isNetSucceed()) {
	//
	// JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
	// if (ret != null) {
	// if (ret.succeed == true) {
	//
	// ProcessMessage pro = ProcessMessage.getInstance();
	// pro.process(ret);
	// mBtnMsgClose.setVisibility(View.INVISIBLE);
	// } else {
	// new
	// AlertDialog.Builder(MessageActivity.this).setTitle("提示").setMessage(ret.explain)
	// .setPositiveButton("确定", null).show();
	// }
	// }
	// }
	//
	// WaitingActivity.stop();
	// }
	// };

	private void postPackageCallBack(ResultPackage result, int postPackageType) {
		if (result.isNetSucceed()) {
			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (postPackageType == 0) {
				if (ret != null) {
					if (ret.succeed == true) {
						mHasVoice = false;

						// Util.handkey++;// 成功后更新handkey

						ProcessMessage pro = ProcessMessage.getInstance();
						pro.process(ret, false);
						updateListView(true);

						mEditMsg.setText("");
						
						LogInfo.LogOutE("PostPackageEngine", "PostPackageEngine--ret.succeed == true");
					} else {
						new AlertDialog.Builder(MessageActivity.this).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", null).show();
						LogInfo.LogOutE("PostPackageEngine", "PostPackageEngine--ret.succeed == false");
					}
				}
			} else if (postPackageType == 1) {
				if (ret != null) {
					if (ret.succeed == true) {

						mMsgGroupList.setState(mLinkId, QuestionInfo.STATE_CLOSE);
						finish();
					} else {
						new AlertDialog.Builder(MessageActivity.this).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", null).show();
					}
				}
			} else if (postPackageType == 2) {
				if (ret != null) {
					if (ret.succeed == true) {

						ProcessMessage pro = ProcessMessage.getInstance();
						pro.process(ret, false);
						mBtnAddFriend.setVisibility(View.INVISIBLE);
						Toast.makeText(context, "添加好友成功", Toast.LENGTH_LONG).show();
					} else {
						new AlertDialog.Builder(MessageActivity.this).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", null).show();
					}
				}

			}
		}
	}

	protected void CloseQuestion() {

		MsgInfoData data = mMsgList.getFirstItem();
		// mPostPackage = new PostPackage(this, mSetQusetionListener);
		mJsonQuestion.id = mLinkId;
		mJsonQuestion.owner = data.senderId;
		mJsonQuestion.cmd = JsonQuestion.MARK;

		postPackageType = 1;
		mPostPackageEngine = new PostPackageEngine(context, mJsonQuestion, handler);
		mPostPackageEngine.post();

		// if (mPostPackage.post(mJsonQuestion, getString(R.string.host_ip),
		// true)) {
		//
		// mBtnSend.setEnabled(false);
		// WaitingActivity.waiting(this, WAIT_POST_MSG);
		// }
	}

	private void sendMsg() {

		String msg = mEditMsg.getText().toString().trim();
		if (msg.length() == 0 && mHasVoice == false) {
			return;
		}
		if (mHasVoice) {
			postSbx();
		} else {
			if(UserInfo.getInstanse().isTranslatetalk()){
				if (PublicArithmetic.chTandEnF(msg.trim())) {
					mTranslater.transCH2EN(msg, false);
				} else {
					mTranslater.transEN2CH(msg, false);
				}
			}else{
				postMsg(msg, "", 0);
			}
			
		}
	}

	private void postMsg(String sText, String vId, int vLen) {
		mJsonMessage.linkId = mLinkId;
		mJsonMessage.text = sText;
		mJsonMessage.vLen = vLen;
		mJsonMessage.vId = vId;

		if (mMessageChanged) {
			mMessageChanged = false;
			Util.handkey++;
		}

		// 握手key值
		mJsonMessage.handkey = Util.handkey;

		switch (mDoing) {
		case DO_ASK:
		case DO_SOLVE:
			mJsonMessage.cmd = JsonMessage.Function.SOLVED;
			break;
		case DO_MSG:
		default:
			mJsonMessage.cmd = JsonMessage.Function.MSG;
		}

		postPackageType = 0;
		mPostPackageEngine = new PostPackageEngine(context, mJsonMessage, handler);
		mPostPackageEngine.post();
	}
	
	private void postMsg(String sText,String sTrans, String vId, int vLen) {
		mJsonMessage.linkId = mLinkId;
		mJsonMessage.text = sText+"/"+sTrans;
		mJsonMessage.vLen = vLen;
		mJsonMessage.vId = vId;

		if (mMessageChanged) {
			mMessageChanged = false;
			Util.handkey++;
		}

		// 握手key值
		mJsonMessage.handkey = Util.handkey;

		switch (mDoing) {
		case DO_ASK:
		case DO_SOLVE:
			mJsonMessage.cmd = JsonMessage.Function.SOLVED;
			break;
		case DO_MSG:
		default:
			mJsonMessage.cmd = JsonMessage.Function.MSG;
		}

		postPackageType = 0;
		mPostPackageEngine = new PostPackageEngine(context, mJsonMessage, handler);
		mPostPackageEngine.post();
	}

	private void postVoiceDataCallBack(String fileId) {
		if (fileId == null) {
			setButtonEnabled(true);
			return;
		}
		if (fileId.length() == 0) {
			setButtonEnabled(true);
			return;
		}
		byte[] voice = (byte[]) mDialog.getObject();
		String msg = mEditMsg.getText().toString().trim();

		if (mMessageChanged) {
			mMessageChanged = false;
			Util.handkey++;
		}

		// 握手key值
		mJsonMessage.handkey = Util.handkey;

		mJsonMessage.linkId = mLinkId;
		if(UserInfo.getInstanse().isTranslatetalk()){
			mJsonMessage.text = msg+"/"+transResult;
		}else{
			mJsonMessage.text = msg;
		}
		mJsonMessage.vLen = voice.length;
		mJsonMessage.vId = fileId;

		switch (mDoing) {
		case DO_ASK:
		case DO_SOLVE:
			mJsonMessage.cmd = JsonMessage.Function.SOLVED;
			break;
		case DO_MSG:
		default:
			mJsonMessage.cmd = JsonMessage.Function.MSG;
		}
		postPackageType = 0;
		mPostPackageEngine = new PostPackageEngine(context, mJsonMessage, handler);
		mPostPackageEngine.post();
		handler.sendEmptyMessage(104);
	}

	private void postSbx() {

		byte[] voice = (byte[]) mDialog.getObject();
		mPostVoiceDataToServerEngine.httpRequestPostNewThread(voice, RequestParam.FILE_TYPE_VOICE);
	}

	@Override
	public void onNetPostResult(PostPackage owner, ResultPackage result) {

		if (result.isNetSucceed()) {

			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (ret != null) {
				if (ret.succeed == true) {

					ProcessMessage pro = ProcessMessage.getInstance();
					pro.process(ret, false);
				} else {
					new AlertDialog.Builder(this).setTitle("提示").setMessage(ret.explain).setPositiveButton("确定", null)
							.show();
				}
			}
		} else {

		}

		mBtnSend.setEnabled(true);
		WaitingActivity.stop();
	}

	@Override
	public void updateMesage() {
		updateListView(false);
	}

	private void setButtonEnabled(boolean b) {

		mBtnSend.setEnabled(b);
		mBtnSpeak.setEnabled(b);
	}

	@Override
	public void isShowTipDialog(String msg) {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mDialog.close();
			mInterpretView.dismissPopWindow();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			setEditTextModel();
		}
		return false;
	}
}
