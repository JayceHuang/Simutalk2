package com.ztspeech.simutalk2.trans;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ztspeech.recognizer.EngineResultFlag;
import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizer.net.HttpGetQtEv;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialog;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.TransTextTable;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.activity.ShowWhiteBordActivity;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Collecter;
import com.ztspeech.simutalk2.dictionary.entity.KouyiRecord;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.qa.Location;
import com.ztspeech.simutalk2.qa.MainActivity;
import com.ztspeech.simutalk2.qa.WaitingActivity;
import com.ztspeech.simutalk2.qa.view.InterpretView;
import com.ztspeech.simutalk2.trans.speak.AudioClip;
import com.ztspeech.simutalk2.trans.speak.SpeakAdapter;
import com.ztspeech.simutalk2.trans.speak.SpeakItemData;
import com.ztspeech.simutalk2.trans.speak.SpeakView;
import com.ztspeech.simutalk2.trans.speak.SpeakView.IOnSpeakViewAdapterCallback;
import com.ztspeech.simutalk2.weibo.EditContent;
import com.ztspeech.simutalk2.weibo.IRenren;
import com.ztspeech.simutalk2.weibo.ISina;
import com.ztspeech.simutalk2.weibo.ITencent;
import com.ztspeech.simutalk2.weibo.Ikaixin;
import com.ztspeech.translator.Translator;

public class InterpretActivity extends Activity implements OnClickListener, OnScrollListener, OnTouchListener,
		IOnSpeakViewAdapterCallback {

	SQLiteDom sqliteDom = null;

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {

		super.onStop();
		stopAll();
		isWorking = false;
	}

	/** Called when the activity is first created. */
	private SpeakAdapter mSpeakAdapter;
	private int nCurrentSpeakIndex = 0;
	private static InterpretActivity instance;

	private ListView mListView;
	private Translator mTranslater;
	private TextPlayer mPlayer;
	private AudioClip mResultTipAudio;
	// private RecognizerDialog mDialog;

	// new aip
	private UnisayRecognizerDialog mDialog;
	private UserInfo mUser = UserInfo.getInstanse();
	private EditText mEditInput;
	// private Button mBtnRecord;
	private Button mBtnChangeInput;
	private Button mBtnSendInput;
	private Button mBtnLanguage;
	private ImageButton m_btn_record;
	public static boolean isWorking = false;

	public LinearLayout controlLayout;
	private InterpretView mInterpretView;

	private Handler mHandlerKeyboard = new Handler() {

		@Override
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
				mDialog.setToEN2CHEngine();
				transInputText(msg.obj.toString());
				break;
			default:
				break;
			}
		}

	};

	private String mCurrentInLanguage = "";
	private String mCurrentOutLanguage = "";
	private String mCurrentTransText = "";

	private float startX, startY;
	private boolean mIsInputTextModel = false;
	public TransTextTable mTableTransText;
	public static boolean isDeletefromMyLiberary = false;

	private int mListViewHeight = 0;

	// 弹出菜单
	private PopupWindow popMenu;
	private Button btnGood;
	private Button btnBad;
	private Button btnCopy;
	private Button btnFull;
	private Button btnMore;

	// 更多菜单
	private PopupWindow popMore;
	private GridView gdv;

	// 语言切换
	private PopupWindow popCL;
	private Button btnLetter;
	private Button btnEnToCh;
	private Button btnChToEn;

	private LayoutInflater inflater;

	public static InterpretActivity getInstance() {
		return instance;
	}

	public void setAutoTTS(boolean autoTts) {

	}

	public void setOnlyRecoginze(boolean b) {
		mDialog.setListViewShow(false);
		if (mUser.isOnlyRecoginze()) {
			if (UserInfo.S2T_CH2EN.equals(mUser.s2sType)) {
				mDialog.setToChineseEngine();
			} else if (UserInfo.S2T_EN2CH.equals(mUser.s2sType)) {
				mDialog.setToEnglishEngine();
			} else if (UserInfo.S2T_LETTER.equals(mUser.s2sType)) {
				mDialog.setListViewShow(true);
			}
		} else {
			if (UserInfo.S2T_CH2EN.equals(mUser.s2sType)) {
				mDialog.setToCH2ENEngine();
			} else if (UserInfo.S2T_EN2CH.equals(mUser.s2sType)) {
				mDialog.setToEN2CHEngine();
			} else if (UserInfo.S2T_LETTER.equals(mUser.s2sType)) {
				// mDialog.setToEN2CHEngine();
				mDialog.setListViewShow(true);
			}
		}
	}

	public void setS2sType(String sType) {
		mUser.s2sType = sType;
		mUser.save();
		if (sType.equals(UserInfo.S2T_EN2CH)) {

			mBtnLanguage.setBackgroundResource(R.drawable.trans_bottorm_button_ec);
			// mBtnLanguage.setText("英>中");
			nCurrentSpeakIndex = 1;

			mCurrentInLanguage = TransTextTable.LANGUAGE_EN;
			mCurrentOutLanguage = TransTextTable.LANGUAGE_CH;
		} else if (sType.equals(UserInfo.S2T_CH2EN)) {
			mBtnLanguage.setBackgroundResource(R.drawable.trans_bottorm_button_ce);
			// mBtnLanguage.setText("中>英");
			mCurrentInLanguage = TransTextTable.LANGUAGE_CH;
			mCurrentOutLanguage = TransTextTable.LANGUAGE_EN;
			nCurrentSpeakIndex = 0;
		} else {
			mBtnLanguage.setBackgroundResource(R.drawable.trans_bottorm_button_zimu);
			// mBtnLanguage.setText("字  母");
			mCurrentInLanguage = TransTextTable.LANGUAGE_EN;
			mCurrentOutLanguage = TransTextTable.LANGUAGE_CH;
			// mDialog.setListViewShow(true);
		}

		setOnlyRecoginze(false);

		mEditInput.setText("");

		// int nSelection = mSpeakAdapter.getCount();
		// if (nSelection >= 0) {
		// mListView.setSelection(nSelection);
		// }
		// 滚动动画
		// mSpeakAdapter.smoothToBottomWithAnim(mListView);
	}

	public void setUserName(String name) {

		mUser.isChange = true;
		mUser.setUserName(name);
		mDialog.setUserInfo(name, 0, 0);
	}

	private void stopAll() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.slideinright, R.anim.slideoutleft);
		super.onCreate(savedInstanceState);
		if (MainActivity.iSina != null) {
			MainActivity.iSina.setClass();
		}
		if (MainActivity.iRenren != null) {
			MainActivity.iRenren.setClass();
		}
		if (MainActivity.iTencent != null) {
			MainActivity.iTencent.setClass();
		}
		if (MainActivity.ikaixin != null) {
			MainActivity.ikaixin.setClass();
		}
		MainActivity.ikaixin = Ikaixin.getInstance(this);
		MainActivity.ikaixin.init(this);
		MainActivity.iSina = ISina.getInstance(this);
		MainActivity.iSina.init();
		MainActivity.iRenren = IRenren.getInstance(this);
		MainActivity.iRenren.init(this);
		MainActivity.iTencent = ITencent.getInstance(this);
		MainActivity.iTencent.init();
		sqliteDom = new SQLiteDom();
		// sqliteDom.openDataBase2();

		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		instance = this;
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.activity_interpret, null);
		setContentView(v);
		// mInterpretView = new InterpretView(this, mHandlerKeyboard, v, this);
		// // mMainViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		// mDialog = new UnisayRecognizerDialog(this, "", mRecognizerListener,
		// mInterpretView.mNewRecognizerViewListenerInterface);

		mInterpretView = new InterpretView(this, mHandlerKeyboard, this);
		mDialog = new UnisayRecognizerDialog(this, "", mRecognizerListener,
				mInterpretView.mNewRecognizerViewListenerInterface);

		mTranslater = new Translator(this, mTranslaterListener);
		mPlayer = TextPlayer.getInstance();
		mPlayer.setPopContext(this);
		mResultTipAudio = new AudioClip(this, R.raw.show_results);
		mListView = (ListView) findViewById(R.id.scrollView1);
		mEditInput = (EditText) findViewById(R.id.edit_input_text);
		mBtnChangeInput = (Button) findViewById(R.id.btn_change_input);
		mBtnSendInput = (Button) findViewById(R.id.btn_send_input);
		mBtnLanguage = (Button) findViewById(R.id.btn_change_language);
		m_btn_record = (ImageButton) findViewById(R.id.m_btn_record);

		controlLayout = (LinearLayout) findViewById(R.id.control_layout);

		mBtnChangeInput.setOnClickListener(this);
		mBtnSendInput.setOnClickListener(this);
		m_btn_record.setOnClickListener(this);
		m_btn_record.setOnTouchListener(this);
		mBtnLanguage.setOnClickListener(this);

		mEditInput.setOnKeyListener(mEditInputTextKeyListener);
		mBtnSendInput.setText("");
		mListView.setDividerHeight(0);
		mListView.setOnTouchListener(mListViewTouch);

		mListView.setOnScrollListener(this);
		// mListView.setOnItemLongClickListener(mAdapterLongLinstener);
		mListView.setOnItemClickListener(mAdapterLinstener);
		mTableTransText = ((Location)getApplication()).mTableTransText;

		createView();
		readConfig();
		inputUserName();

		mListView.setSelection(mTableTransText.size() - 1);
		// mDialog.setObject("s2s.simutalk.com".getBytes());
		// mDialog.setObject("192.168.154.93".getBytes());
		mDialog.setToContinuous(2);
		
	}

	private boolean btnClickflag = false;

	private OnEngineListener mTranslaterListener = new OnEngineListener() {

		public void onEngineResult(List list, int flag, String id) {

			SpeakItemData data = null;
			if (flag == EngineResultFlag.OVER) {

				data = new SpeakItemData(mUser.s2sType, mCurrentInLanguage, mCurrentTransText, null,
						mCurrentOutLanguage, (String) list.get(0), null, true);
				data.taskId = id;
				onTranslatorViewResult(data);

				if (data.isTransTTS() && mUser.autoTTS) {
					playText(data.trans, mCurrentOutLanguage);
				}
			} else if (flag == EngineResultFlag.NOTHING) {

				onSpeakViewResult(data);

			} else if (flag == EngineResultFlag.CONNECT_ERROR) {

				onSpeakViewError(flag);
			} else if (flag == EngineResultFlag.CANCEL) {
				// doError(flag);
			} else {
				onSpeakViewError(flag);
			}
			mBtnSendInput.setEnabled(true);
		}

		public void onEngineStart() {

			WaitingActivity.waiting(InterpretActivity.this, 0);
			isWorking = true;
		}

		public void onEngineEnd() {
			mBtnSendInput.setEnabled(true);
			WaitingActivity.stop();
			isWorking = false;
		}
	};

	private OnEngineListener mRecognizerListener = new OnEngineListener() {

		private boolean fristR = true;
		private SpeakItemData data = null;
		// private List<String> list_$ = null;
		String mspeak = null;

		public void onEngineResult(List list, int flag, String id) {
			if (flag == EngineResultFlag.WORKING) {
				if (list.size() > 0) {
					String speak = (String) list.get(0);
					if (fristR) {
						// list_$ = new ArrayList<String>();
						// list_$.add(speak);
						mspeak = speak;
						data = new SpeakItemData(mUser.s2sType, mCurrentInLanguage, mspeak, true);
						data.taskId = id;
						if (speak.contains(" / ")) {
							data.mspeak = mspeak;
						}
						data.speak = data.speak.replace(" / ", "");

						onSpeakViewResultByLianxuR(data);
						fristR = false;
					} else {
						String[] empt = speak.split(" / ");
						if (empt.length > 1) {
							empt[1] = " / " + empt[1];
							mspeak = mspeak.split(" / ")[0] + empt[0] + empt[1];
						} else {
							mspeak = mspeak.split(" / ")[0] + empt[0];
						}
						data.speak = mspeak;
						data.mspeak = mspeak;
						data.speak = data.speak.replace(" / ", "");
						// list_$.add(speak);
						// if(speak.contains(" / ")){
						// speak = speak.replace(" / ", "");
						// data.speak = data.speak + speak;
						// }else{
						// for(int i=0;i<list_$.size();i++){
						// if(!list_$.get(i).contains(" / ")){
						// speak+= list_$.get(i);
						// }
						// }
						// data.speak = speak;
						// }
						mSpeakAdapter.update(data);
						mSpeakAdapter.notifyDataSetChanged();
					}

				}
			} else if (flag == EngineResultFlag.OVER) {
				mDialog.close();
				mInterpretView.dismissPopWindow();
				if (list.size() > 0) {
					String speak = (String) list.get(0);
					if (data == null) {
						data = new SpeakItemData(mUser.s2sType, mCurrentInLanguage, speak, true);
						data.taskId = id;
						onSpeakViewResultByLianxuR(data);
					} else {
						String[] empt = speak.split(" / ");
						if (empt.length > 1) {
							// empt[1] = " / "+empt[1];
							mspeak = mspeak.split(" / ")[0] + empt[0] + empt[1];
						} else {
							mspeak = mspeak.split(" / ")[0] + empt[0];
						}

						data.speak = mspeak;
						data.speak = data.speak.replace(" / ", "");
						mSpeakAdapter.update(data);
						data.mspeak = "";
						mSpeakAdapter.notifyDataSetChanged();
					}
				}
				if (mUser.isOnlyRecoginze()) {
					InputStream in = new ByteArrayInputStream((byte[]) mDialog.getObject());
					data.speakStream = in;
					onSpeakViewResult(data);
				}
				if (list.size() > 1) {
					String trans = (String) list.get(1);
					data.trans = trans;
					InputStream in = new ByteArrayInputStream((byte[]) mDialog.getObject());
					data.speakStream = in;
					onSpeakViewResult(data);
					if (mUser.autoTTS && data.isTransTTS()) {
						playText(data);
					}
				}
				data = null;
				fristR = true;

				mspeak = null;
			} else if (flag == EngineResultFlag.NOTHING) {
				mDialog.close();
				mInterpretView.dismissPopWindow();
				onSpeakViewResult(data);
				data = null;
				fristR = true;

				if (data != null) {
					onSpeakViewResult(data);
				}
				mspeak = null;
			} else if (flag == EngineResultFlag.CONNECT_ERROR) {
				mDialog.close();
				mInterpretView.dismissPopWindow();
				//onSpeakViewError(flag);
				data = null;
				fristR = true;

				if (data != null) {
					onSpeakViewResult(data);
				}
				mspeak = null;
			} else if (flag == EngineResultFlag.CANCEL) {
				// doError(flag);
				mDialog.close();
				mInterpretView.dismissPopWindow();
				data = null;
				fristR = true;

				if (data != null) {
					onSpeakViewResult(data);
				}
				mspeak = null;
			} else {
				mDialog.close();
				mInterpretView.dismissPopWindow();
				onSpeakViewError(flag);
				data = null;
				fristR = true;

				if (data != null) {
					onSpeakViewResult(data);
				}
				mspeak = null;
			}
		}

		public void onEngineStart() {
			isWorking = true;
		}

		public void onEngineEnd() {
			// btnCancelDisp(false);
			btnClickflag = false;
			// m_btn_record.setEnabled(true);
			// mDialog.close();
			// mInterpretView.setRecordLayoutDisp(View.GONE);

			if (data != null) {
				onSpeakViewResult(data);
			}
			isWorking = false;
			data = null;
			mspeak = null;
			fristR = true;
		}
	};

	private OnTouchListener mListViewTouch = new OnTouchListener() {

		public boolean onTouch(View arg0, MotionEvent event) {
			closePM();// 关掉popmenu
			// if(TextPlayer.getInstance().isPlaying()){
			// TextPlayer.getInstance().stop();
			// }
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				startX = event.getX();
				startY = event.getY();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {

				float offsetX = event.getX() - startX;
				float offsetY = event.getY() - startY;

				if (offsetX < 0) {
					offsetX = -offsetX;
				}
				if (offsetY < 0) {
					offsetY = -offsetY;
				}

				if ((offsetX - 10) <= offsetY) {
					return false;
				}

				offsetX = event.getX() - startX;
				int nOldIndex = nCurrentSpeakIndex;

				if (offsetX > 20) {
					nCurrentSpeakIndex++;
					if (nCurrentSpeakIndex < 2) {

					} else {
						nCurrentSpeakIndex = 0;
					}
					// mMainViewFlipper.setInAnimation(InterpretActivity.this,
					// R.anim.in_lefttoright);
					// mMainViewFlipper.setOutAnimation(InterpretActivity.this,
					// R.anim.out_lefttoright);
					// mMainViewFlipper.showNext();

				} else if (offsetX < -20) {
					nCurrentSpeakIndex--;
					if (nCurrentSpeakIndex >= 0) {
					} else {
						nCurrentSpeakIndex = 1;
					}

					// mMainViewFlipper.setInAnimation(InterpretActivity.this,
					// R.anim.in_righttoleft);
					// mMainViewFlipper.setOutAnimation(InterpretActivity.this,
					// R.anim.out_righttoleft);
					// mMainViewFlipper.showPrevious();
				}

				if (nOldIndex != nCurrentSpeakIndex) {

					// setS2sType(mSpeakState[nCurrentSpeakIndex].getType());
				}
			}
			return false;
		}
	};

	private void inputUserName() {

		mDialog.setUserInfo(mUser.getUserName(), 0, 0);
	}

	private void readConfig() {

		changeInputTextModel(mIsInputTextModel);
		this.setS2sType(mUser.s2sType);
		this.setFontSize(mUser.getFontIndex());
	}

	public void setFontSize(int fontSize) {

		mUser.setFontIndex(fontSize);
		float size = mUser.getFontSize();
		mSpeakAdapter.setFontSize(size);
		mSpeakAdapter.notifyDataSetChanged();
		// this.updateListView();
	}

	private Runnable mKeyboardRunnable = new Runnable() {
		// s
		public void run() {
			// InputMethodManager imm = (InputMethodManager)
			// getSystemService(INPUT_METHOD_SERVICE);
			int h = mListView.getHeight();
			if (mListViewHeight > h) {
				if (mBtnChangeInput.getVisibility() == View.VISIBLE) {
					if(popMenu!=null){
						popMenu.dismiss();
					}
					mBtnChangeInput.setVisibility(View.GONE);
					mBtnLanguage.setVisibility(View.GONE);
					// controlLayout.setMinimumHeight(120);
					// mEditInput.setLines(2);
					// mEditInput.setMaxLines(2);
					// mEditInput.setSingleLine(false);
				}
			} else {
				if (mBtnChangeInput.getVisibility() == View.GONE) {
					// mEditInput.setLines(1);
					// mEditInput.setMaxLines(1);
					// mEditInput.setSingleLine(true);
					mBtnChangeInput.setVisibility(View.VISIBLE);
					mBtnLanguage.setVisibility(View.VISIBLE);

				}
			}

			mHandlerKeyboard.postDelayed(mKeyboardRunnable, 300);
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		mDialog.setUserInfo(mUser.getUserName(), 0, 0);
		
		if(requestCode==resultCode&&resultCode==000011){
			int a = data.getIntExtra("action", 0);
			String str = data.getStringExtra("content");
			switch(a){
			case 2:
				MainActivity.iSina.sendWeibo(str,
						InterpretActivity.this);
				break;
			case 3:
				MainActivity.iTencent.sendWeibo(str,
						InterpretActivity.this);
				break;
			case 4:
				MainActivity.iRenren.sendWeibo(str,
						InterpretActivity.this);
				break;
			case 5:
				MainActivity.ikaixin.sendWeibo(str,
						InterpretActivity.this);
				break;
			case 0:
				Util.showToast(this, "发送错误");
				break;
			}
		}
		// if (requestCode == ITencent.BIND_TENCENT_CODE) {
		// if (resultCode == OAuthV1AuthorizeWebView.RESULT_CODE) {
		// if (data == null) {
		// Toast.makeText(this, "绑定失败", Toast.LENGTH_SHORT).show();
		// iTencent.setOAuth();
		// } else {
		// boolean result = iTencent.saveStatus(data);
		// if (result) {
		// Toast.makeText(this, "绑定成功", Toast.LENGTH_SHORT).show();
		// } else {
		// Toast.makeText(this, "绑定失败", Toast.LENGTH_SHORT).show();
		// iTencent.setOAuth();
		// }
		// }
		// }
		// }
	}

	private void updateListView() {
		mSpeakAdapter.notifyDataSetChanged(true);
	}

	private void createView() {

		mSpeakAdapter = new SpeakAdapter(this, mTableTransText, mListView);
		mSpeakAdapter.setOnSpeakViewAdapterListener(this);
		mListView.setAdapter(mSpeakAdapter);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (keyCode == 24) {
			return true;
		} else if (keyCode == 25) {
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	private SpeakItemData mSelectedItemData = null;

	private OnItemClickListener mAdapterLinstener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (TextPlayer.getInstance().isPlaying()) {
				TextPlayer.getInstance().stop();
			}
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEditInput.getWindowToken(), 0);
			if (arg2 == mListView.getCount() - 1) {

			} else {
				SpeakView v = (SpeakView) arg1;
				mSelectedItemData = v.data;
				mSpeakAdapter.setFocus(v.data);
				// updateListView();
				// play(v.data);

				showPopMenu(arg1, arg2);
			}

		}
	};

	public void initPopMenu(View view) {
		btnGood = (Button) view.findViewById(R.id.btnGood);
		btnBad = (Button) view.findViewById(R.id.btnBad);
		btnCopy = (Button) view.findViewById(R.id.btnCopy);
		btnFull = (Button) view.findViewById(R.id.btnFull);
		btnMore = (Button) view.findViewById(R.id.btnMore);
	}

	// 功能菜单
	public void showPopMenu(View parentView, final int position) {
		closePM();
		View view = LayoutInflater.from(this).inflate(R.layout.pop_interpret_action, null);
		initPopMenu(view);
		btnMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu.dismiss();
				showPopMore(position);

			}
		});

		btnGood.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu.dismiss();
				commontRecord(position, 1);

			}
		});

		btnBad.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu.dismiss();
				commontRecord(position, 0);

			}
		});
		btnCopy.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clipboard.setText(mSelectedItemData.speak + "\n" + mSelectedItemData.trans);
				popMenu.dismiss();
			}
		});
		popMenu = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popMenu.setBackgroundDrawable(new BitmapDrawable());

		// pop 位置优化
		int yoff = -20;
		if ((parentView.getBottom() + 53) > mListView.getHeight()) {
			yoff = mListView.getHeight() - parentView.getBottom() - 53;
		}
		popMenu.showAsDropDown(parentView, 300, yoff);

		// popMenu.setFocusable(true);
		// popMenu.setOutsideTouchable(false);

		popMenu.update();
	}

	public void closePM() {
		if (popMenu != null) {
			popMenu.dismiss();
		}
	}

	ArrayList<Map<String, Object>> functionList = null;

	public void initPopMore(View view) {
		functionList = new ArrayList<Map<String, Object>>();
		gdv = (GridView) view.findViewById(R.id.gdv);
		// gdv.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// final RelativeLayout rlbg = (RelativeLayout)
		// view.findViewById(R.id.rlBg);
		// // rlbg.setVisibility(View.VISIBLE);
		// rlbg.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // rlbg.setVisibility(View.INVISIBLE);
		// popMore.dismiss();
		// }
		// });

		Map<String, Object> m0 = new HashMap<String, Object>();
		m0.put("textItem", "");
		m0.put("iconItem", R.drawable.trans_ci_pop_button_01);
		m0.put("action", 0);
		functionList.add(m0);
		Map<String, Object> m1 = new HashMap<String, Object>();
		m1.put("textItem", "");
		m1.put("iconItem", R.drawable.trans_ci_pop_button_02);
		m1.put("action", 1);
		functionList.add(m1);
		Map<String, Object> m2 = new HashMap<String, Object>();
		m2.put("textItem", "");
		m2.put("iconItem", R.drawable.trans_ci_pop_button_03);
		m2.put("action", 2);
		functionList.add(m2);
		Map<String, Object> m3 = new HashMap<String, Object>();
		m3.put("textItem", "");
		m3.put("iconItem", R.drawable.trans_ci_pop_button_04);
		m3.put("action", 3);
		functionList.add(m3);
		Map<String, Object> m4 = new HashMap<String, Object>();
		m4.put("textItem", "");
		m4.put("iconItem", R.drawable.trans_ci_pop_button_05);
		m4.put("action", 4);
		functionList.add(m4);
		Map<String, Object> m5 = new HashMap<String, Object>();
		m5.put("textItem", "");
		m5.put("iconItem", R.drawable.trans_ci_pop_button_06);
		m5.put("action", 5);
		functionList.add(m5);
		Map<String, Object> m6 = new HashMap<String, Object>();
		m6.put("textItem", "");
		m6.put("iconItem", R.drawable.trans_ci_pop_button_07);
		m6.put("action", 6);
		functionList.add(m6);
		Map<String, Object> m7 = new HashMap<String, Object>();
		m7.put("textItem", "");
		m7.put("iconItem", R.drawable.trans_ci_pop_button_08);
		m7.put("action", 7);
		functionList.add(m7);
		Map<String, Object> m8 = new HashMap<String, Object>();
		m8.put("textItem", "");
		m8.put("iconItem", R.drawable.trans_ci_pop_button_09);
		m8.put("action", 8);
		functionList.add(m8);
		Map<String, Object> m9 = new HashMap<String, Object>();
		m9.put("textItem", "");
		m9.put("iconItem", R.drawable.trans_ci_pop_button_10);
		m9.put("action", 9);
		functionList.add(m9);
		Map<String, Object> m10 = new HashMap<String, Object>();
		m10.put("textItem", "");
		m10.put("iconItem", R.drawable.trans_ci_pop_button_11);
		m10.put("action", 10);
		functionList.add(m10);

		SimpleAdapter sa = new SimpleAdapter(InterpretActivity.this, functionList, R.layout.gdv_interpret_item,
				new String[] { "textItem", "iconItem" }, new int[] { R.id.tvFunction, R.id.imageFunction });
		gdv.setAdapter(sa);
	}

	// 更多菜单
	public void showPopMore(final int position) {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_interpret_more, null);
		initPopMore(view);
		gdv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int a = Integer.parseInt(functionList.get(arg2).get("action").toString());
				switch (a) {
				case 0:
					popMore.dismiss();
					Uri smsToUri = Uri.parse("smsto:");// 联系人地址
					Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
					mIntent.putExtra("sms_body", mSelectedItemData.speak + "\n" + mSelectedItemData.trans);// 短信内容
					startActivity(mIntent);
					break;
				case 1:
					popMore.dismiss();
					Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
					mailIntent.setType("plain/test");

					mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mSelectedItemData.speak + "\n"
							+ mSelectedItemData.trans);
					startActivity(Intent.createChooser(mailIntent, "发送邮件"));
					break;
//				case 2:
//					popMore.dismiss();
//					showPopEditWC(mSelectedItemData.speak + "\n" + mSelectedItemData.trans, a);
////					MainActivity.iSina.sendWeibo(mSelectedItemData.speak + "\n" + mSelectedItemData.trans,
////							InterpretActivity.this);
//					break;
//				case 3:
//					popMore.dismiss();
//					showPopEditWC(mSelectedItemData.speak + "\n" + mSelectedItemData.trans, a);
////					MainActivity.iTencent.sendWeibo(mSelectedItemData.speak + "\n" + mSelectedItemData.trans,
////							InterpretActivity.this);
//					break;
//				case 4:
//					popMore.dismiss();
//					showPopEditWC(mSelectedItemData.speak + "\n" + mSelectedItemData.trans, a);
////					MainActivity.iRenren.sendWeibo(mSelectedItemData.speak + "\n" + mSelectedItemData.trans,
////							InterpretActivity.this);
//					break;
//				case 5:
//					popMore.dismiss();
//					showPopEditWC(mSelectedItemData.speak + "\n" + mSelectedItemData.trans, a);
////					MainActivity.ikaixin.sendWeibo(mSelectedItemData.speak + "\n" + mSelectedItemData.trans,
////							InterpretActivity.this);
//					break;
				case 2:
				case 3:
				case 4:
				case 5:
					popMore.dismiss();
					Intent intent = new Intent(InterpretActivity.this,EditContent.class);
					intent.putExtra("action", a);
					intent.putExtra("content", mSelectedItemData.speak + "\n" + mSelectedItemData.trans);
					startActivityForResult(intent, 000011);
					break;
				case 6:
					popMore.dismiss();
					fullScreenTransData();

					break;
				case 7:
					popMore.dismiss();
					SQLiteDom sqliteDom = new SQLiteDom();
					// if(UserInfo.S2T_CH2EN.equals(mUser.s2sType)){
					KouyiRecord kk = mTableTransText.getRecords(position);
					// System.out.println(mTableCH.get(0).text);
					Collecter collecter = new Collecter();
					collecter.setChildId(6);
					collecter.setText1(kk.getSaid());
					collecter.setText2(kk.getTranslated());
					int result = sqliteDom.insertCollecterFromKouyi(collecter);
					switch (result) {
					case 0:
						new AlertDialog.Builder(InterpretActivity.this).setTitle(null).setMessage("已加入收藏夹")
								.setPositiveButton("确定", null).show();
						break;
					default:
						Toast.makeText(InterpretActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
					}
					break;
				case 8:
					popMore.dismiss();
					shareText();
					break;
				case 9:
					popMore.dismiss();
					deleteSelected(position);
					break;
				case 10:
					popMore.dismiss();
					new AlertDialog.Builder(InterpretActivity.this).setTitle("删除全部内容").setView(null)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									mSpeakAdapter.clear();
									updateListView();
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

								}
							}).show();

					break;
				}

			}
		});
		popMore = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popMore.setBackgroundDrawable(new BitmapDrawable());
		popMore.setAnimationStyle(R.style.popMoreAnimation);
		popMore.showAtLocation(findViewById(R.id.interpret_parent), Gravity.CENTER, 0, 0);
		popMore.setFocusable(true);
		popMore.setOutsideTouchable(false);
		popMore.update();
	}

	public void initPopCL(View view) {
		btnLetter = (Button) view.findViewById(R.id.btnLetter);
		btnEnToCh = (Button) view.findViewById(R.id.btnEnglishToChinese);
		btnChToEn = (Button) view.findViewById(R.id.btnChineseToEnglish);
	}

	// 语言切换
	public void showPopChangeLanguage(View parent) {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_interpret_changelanguage, null);
		initPopCL(view);
		btnLetter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setS2sType(UserInfo.S2T_LETTER);
				popCL.dismiss();
			}
		});
		btnEnToCh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setS2sType(UserInfo.S2T_EN2CH);
				popCL.dismiss();
			}
		});
		btnChToEn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setS2sType(UserInfo.S2T_CH2EN);
				popCL.dismiss();
			}
		});
		popCL = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popCL.setBackgroundDrawable(new BitmapDrawable());
		popCL.setAnimationStyle(R.style.PopupAnimation);

		// popCL.showAtLocation(parent, Gravity.BOTTOM,
		// getResources().getDimensionPixelSize(R.dimen.popchange_x),
		// parent.getBottom() - parent.getHeight());
		// popCL.showAtLocation(mBtnLanguage, Gravity.BOTTOM,
		// (int) parent.getLeft() -
		// getResources().getDimensionPixelSize(R.dimen.popchange_x),
		// parent.getBottom()
		// - parent.getHeight());
		popCL.showAtLocation(mBtnLanguage, Gravity.BOTTOM | Gravity.RIGHT, 0, mBtnLanguage.getBottom());
		popCL.setFocusable(true);
		popCL.setOutsideTouchable(false);
		popCL.update();
	}

	private void play(SpeakItemData data, boolean isSpeak) {

		if (TextPlayer.getInstance().isPlaying()) {
			TextPlayer.getInstance().stop();
			return;
		}

		if (isSpeak) {
			if (data.speakStream != null) {
				TextPlayer.getInstance().play(data.speakStream);
			} else {
				playText(data.speak, data.languageSpeak);
			}
		} else {
			if (data.isTransTTS()) {
				playText(data.trans, data.languageTrans);
			}
		}

		/*
		 * switch (data.type) { case SpeakItemData.Define.SPEAK_TYPE_FULL: case
		 * SpeakItemData.Define.SPEAK_TYPE_TEXT: playText(data.text,
		 * data.language); return; case SpeakItemData.Define.SPEAK_TYPE_SPEAK:
		 * if (data.mPlayStream != null) {
		 * TextPlayer.getInstance().play(data.mPlayStream); } else {
		 * playText(data.text, data.language); } break; case
		 * SpeakItemData.Define.SPEAK_TYPE_TRANS:
		 * 
		 * playText(data.text, data.language); break; default: }
		 */
	}

	private void playText(SpeakItemData obj) {
		mPlayer.setPopContext(this);
		if (obj.type.equals(UserInfo.S2T_CH2EN)) {
			mPlayer.playEnglish(obj.trans);
		} else if (obj.type.equals(UserInfo.S2T_EN2CH)) {
			mPlayer.playChinese(obj.trans);
		}
	}

	private void playText(String text, String language) {

		if (TransTextTable.LANGUAGE_CH.equals(language)) {
			mPlayer.playChinese(text);
		} else {
			mPlayer.playEnglish(text);
		}
	}

	private OnKeyListener mEditInputTextKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			if (keyCode == 66) { // 如果用户点击了回车键
				transInputText();
			}
			return false;
		}
	};

	private void selectDo() {

		AlertDialog.Builder builder = new AlertDialog.Builder(InterpretActivity.this);
		CharSequence[] items = new CharSequence[6];
		int i = 0;
		items[i++] = getString(R.string.trans_share_fullscreen);
		items[i++] = getString(R.string.trans_share_send);
		items[i++] = getString(R.string.trans_share_copy);
		items[i++] = getString(R.string.trans_share_collect);
		items[i++] = getString(R.string.trans_share_delete);
		items[i++] = getString(R.string.trans_share_clear);
		builder.setTitle(getString(R.string.trans_share_title));

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					fullScreenTransData();
				} else if (item == 1) {
					shareText();
				} else if (item == 2) {
					ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clipboard.setText(mSelectedItemData.speak + "\n" + mSelectedItemData.trans);
				} else if (item == 3) {
					SQLiteDom sqliteDom = new SQLiteDom();
					// if(UserInfo.S2T_CH2EN.equals(mUser.s2sType)){
					KouyiRecord kk = mTableTransText.getRecords(mSelectedItemData.id);
					// System.out.println(mTableCH.get(0).text);
					Collecter collecter = new Collecter();
					collecter.setChildId(6);
					collecter.setText1(kk.getSaid());
					collecter.setText2(kk.getTranslated());
					int result = sqliteDom.insertCollecterFromKouyi(collecter);
					switch (result) {
					case 0:
						new AlertDialog.Builder(InterpretActivity.this).setTitle(null).setMessage("已加入收藏夹")
								.setPositiveButton("确定", null).show();
						break;
					default:
						Toast.makeText(InterpretActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
					}
				} else if (item == 4) {
					// deleteSelected(position);
				} else {
					mSpeakAdapter.clear();
					updateListView();
				}
			}

		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void fullScreenTransData() {
		Intent intent = new Intent(InterpretActivity.this, ShowWhiteBordActivity.class);

		intent.putExtra("english", mSelectedItemData.trans);
		intent.putExtra("chinese", mSelectedItemData.speak);
		if (mSelectedItemData.type == UserInfo.S2T_CH2EN) {
			intent.putExtra("chOrEn", 0);
		} else if (mSelectedItemData.type == UserInfo.S2T_EN2CH) {
			intent.putExtra("chOrEn", 1);
		}
		// if
		// (TransTextTable.LANGUAGE_CH.equals(mSelectedItemData.languageSpeak))
		// {
		// intent.putExtra("english", mSelectedItemData.trans);
		// intent.putExtra("chinese", mSelectedItemData.speak);
		//
		// if (mSelectedItemData.type == UserInfo.S2T_CH2EN) {
		//
		// intent.putExtra("chOrEn", 0);
		// } else {
		// intent.putExtra("chOrEn", 1);
		// }
		//
		// } else {
		// intent.putExtra("english", mSelectedItemData.speak);
		// intent.putExtra("chinese", mSelectedItemData.trans);
		// if (mSelectedItemData.type == SpeakItemData.Define.SPEAK_TYPE_TRANS)
		// {
		// intent.putExtra("chOrEn", 1);
		// } else {
		// intent.putExtra("chOrEn", 0);
		// }
		//
		// }
		startActivityForResult(intent, 0);
	}

	private void deleteSelected(int nIndex) {

		mSpeakAdapter.removeItem(nIndex, mSelectedItemData);
		mSpeakAdapter.notifyDataSetChanged();
	}

	private void shareText() {

		Intent mailIntent = new Intent(Intent.ACTION_SEND);
		mailIntent.setType("text/plain");
		mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		mailIntent
				.putExtra(android.content.Intent.EXTRA_TEXT, mSelectedItemData.speak + "\n" + mSelectedItemData.trans);
		mailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		if (UserInfo.S2T_CH2EN.equals(mUser.s2sType)) {
			startActivity(Intent.createChooser(mailIntent, "发送"));
		} else {
			startActivity(Intent.createChooser(mailIntent, "Send"));
		}
	}

	public void sendSMS1(String text) {

		Uri smsToUri = Uri.parse("smsto:0");

		Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
		sendIntent.putExtra("sms_body", text);
		sendIntent.setType("vnd.android-dir/mms-sms");
		startActivity(sendIntent);
	}

	private void changeInputTextModel(boolean bText) {

		if (bText) {
			mBtnSendInput.setVisibility(View.VISIBLE);
			mEditInput.setVisibility(View.VISIBLE);
			// mBtnSendInput .setEnabled(true);
			mEditInput.setEnabled(true);
			// mBtnRecord.setVisibility(View.INVISIBLE);
			// mInterpretView.setRecordLayoutDisp(View.INVISIBLE);
			m_btn_record.setVisibility(View.GONE);
			if (mListViewHeight == 0) {
				mListViewHeight = mListView.getHeight();
			}
			mHandlerKeyboard.postDelayed(mKeyboardRunnable, 100);
		} else {
			mBtnSendInput.setVisibility(View.INVISIBLE);
			mEditInput.setVisibility(View.INVISIBLE);

			// mBtnRecord.setVisibility(View.VISIBLE);
			// mInterpretView.setRecordLayoutDisp(View.VISIBLE);
			m_btn_record.setVisibility(View.VISIBLE);
			mHandlerKeyboard.removeCallbacks(mKeyboardRunnable);
		}

		if (mIsInputTextModel) {
			mBtnChangeInput.setBackgroundResource(R.drawable.trans_bottorm_button_mic);
		} else {
			mBtnChangeInput.setBackgroundResource(R.drawable.trans_bottorm_button_pen);
		}
	}

	@Override
	public void onClick(View arg0) {
		closePM();
		TextPlayer.getInstance().stop();
		switch (arg0.getId()) {
		case R.id.btn_change_input:
			mIsInputTextModel = !mIsInputTextModel;
			changeInputTextModel(mIsInputTextModel);
			break;
		case R.id.btn_send_input:
			// iTencent.sendWeibo("腾讯微博测试", this);
			transInputText();
			break;
		case R.id.m_btn_record:
			// mInterpretView.setRecordLayoutDisp(View.VISIBLE);
			// mDialog.show();
			// btnClickflag = true;
			// m_btn_record.setEnabled(false);
			
			
//			mInterpretView.showPopWindowLocation();
//			mDialog.show();
			LogInfo.LogOutE("haitian", "---------------m_btn_record------------");
			break;
		case R.id.btn_change_language:
			// if (UserInfo.S2T_EN2CH.equals(mUser.s2sType)) {
			// setS2sType(UserInfo.S2T_CH2EN);
			// } else {
			// setS2sType(UserInfo.S2T_EN2CH);
			// }
			showPopChangeLanguage(arg0);
			break;
		case R.id.btn_cancel:
			mDialog.onRecognizerViewCancel();
			mInterpretView.dismissPopWindow();
			break;
		case R.id.btn_bg:
		case R.id.imagenovoiceanim:
			mDialog.onRecognizerViewRecord();
			LogInfo.LogOutE("haitian", "---------------stop record------------");
			break;

		// case R.id.btn_cancel:
		// mDialog.onRecognizerViewCancel();
		// mInterpretView.setRecordLayoutDisp(View.GONE);
		// break;
		// case R.id.btn_bg:
		// case R.id.imagenovoiceanim:
		// mDialog.onRecognizerViewRecord();
		// LogInfo.LogOutE("haitian", "---------------stop record------------");
		// break;
		case R.id.btn_record:
			TextPlayer.getInstance().stop();
			// if (!btnClickflag) {
			// btnClickflag = true;
			// mDialog.show();
			// mInterpretView.setBtnRecordEnable(true);
			// } else {
			// btnClickflag = false;
			// mDialog.close();
			// mInterpretView.setRecordLayoutDisp(View.GONE);
			// }
			if (!btnClickflag) {
				btnClickflag = true;
				mDialog.show();
				mInterpretView.setBtnRecordEnable(true);
			} else {
				btnClickflag = false;
				mDialog.close();
				mInterpretView.dismissPopWindow();
			}
			LogInfo.LogOutE("haitian", "---------------btn_record------------");
			break;
		}

	}

	/**
	 * 识别按钮处理
	 * 
	 * @param flag
	 */
	// private void btnDispProcess(boolean flag) {
	// if (flag) {
	// mBtnChangeInput.setVisibility(View.INVISIBLE);
	// mBtnLanguage.setVisibility(View.INVISIBLE);
	// } else {
	// mBtnChangeInput.setVisibility(View.VISIBLE);
	// mBtnLanguage.setVisibility(View.VISIBLE);
	// }
	// }
	private void btnDispProcess(boolean flag) {
		if (flag) {
			mInterpretView.setBtnCancelDisp(View.VISIBLE);
		} else {
			mInterpretView.setBtnCancelDisp(View.GONE);
		}
	}

	private void btnCancelDisp(boolean flag) {
		if (flag) {
			mInterpretView.setBtnCancelDisp(View.VISIBLE);
			mBtnLanguage.setVisibility(View.GONE);
		} else {
			mInterpretView.setBtnCancelDisp(View.GONE);
			mBtnChangeInput.setVisibility(View.VISIBLE);
			mBtnLanguage.setVisibility(View.VISIBLE);
		}
	}

	public static void setIsDeletefromMyLiberaryTrue() {
		isDeletefromMyLiberary = true;
	}

	@Override
	protected void onResume() {

		super.onResume();

		// 输入状态更新
		setOnlyRecoginze(false);
		if (isDeletefromMyLiberary == true) {
			if (mSpeakAdapter != null) {
				mSpeakAdapter.notifyDataSetChanged();
				isDeletefromMyLiberary = false;
			}
		}
		
		if(mUser.isLClickRecord()){
			mDialog.setRecordAutoStop(false);
			m_btn_record.setBackgroundResource(R.drawable.long_put_speak);
		}else{
			mDialog.setRecordAutoStop(true);
			m_btn_record.setBackgroundResource(R.drawable.trans_bottorm_button_speak);
		}
	}

	private void transInputText(String str) {

		mCurrentTransText = str.trim() + "";
		if (mCurrentTransText.length() > 0) {

			mBtnSendInput.setEnabled(false);
			if (UserInfo.S2T_CH2EN.equals(this.mUser.s2sType)) {

				this.mTranslater.transCH2EN(mCurrentTransText, false);
			} else {
				this.mTranslater.transEN2CH(mCurrentTransText, false);
			}
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditInput.getWindowToken(), 0);
	}

	private void transInputText() {

		mCurrentTransText = mEditInput.getText().toString().trim();
		if (mCurrentTransText.length() > 0) {

			mBtnSendInput.setEnabled(false);
			if(PublicArithmetic.chTandEnF(mCurrentTransText)){
				this.mTranslater.transCH2EN(mCurrentTransText, false);
			}else{
				this.mTranslater.transEN2CH(mCurrentTransText, false);
			}
//			if (UserInfo.S2T_CH2EN.equals(this.mUser.s2sType)) {
//
//				this.mTranslater.transCH2EN(mCurrentTransText, false);
//			} else {
//				this.mTranslater.transEN2CH(mCurrentTransText, false);
//			}
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditInput.getWindowToken(), 0);
	}

	public void speakViewAdapterCheckedChange(SpeakItemData data, String cmd) {

		mSelectedItemData = data;
		selectDo();
	}

	public void onSpeakViewWaitBegin() {
		showRecordkCtrls(false);
		isWorking = true;
	}

	public void onSpeakViewWaitEnd() {

		showRecordkCtrls(true);
		isWorking = false;
	}

	private void showRecordkCtrls(boolean bShow) {

		if (bShow) {
			changeInputTextModel(mIsInputTextModel);
			mBtnChangeInput.setEnabled(true);
		} else {
			mBtnChangeInput.setEnabled(false);
			mEditInput.setEnabled(false);
		}
	}

	public void onSpeakViewError(int error) {

		if (error == EngineResultFlag.CONNECT_ERROR) {
			alertDialog("提示", "没有找到网络！", "好");
		} else {
			alertDialog("提示", "网络上传数据错误！" + error, "好");
		}
		onSpeakViewWaitEnd();
	}

	private void alertDialog(String title, String msg, String btn) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton(btn, null).show();
	}

	public void onSpeakViewResultByLianxuR(SpeakItemData data) {
		if (data == null) {
			// alertDialog("提示","请再说一遍","好");
			return;
		}
		mSpeakAdapter.addItem(data);
		mSpeakAdapter.setFocus(data);
		updateListView();
		mSpeakAdapter.smoothToBottomWithAnim(mListView);
	}

	public void onTranslatorViewResult(SpeakItemData data) {
		if (data == null) {
			// alertDialog("提示","请再说一遍","好");
			return;
		}

		mResultTipAudio.play();

		mSpeakAdapter.addItem(data);
		mSpeakAdapter.setFocus(data);
		// mSpeakAdapter.updateAfterSpeak(data);
		mEditInput.setText(data.speak);
		// mSpeakAdapter.notifyDataSetChanged();
		updateListView();
		// int nSelection = mSpeakAdapter.getCount() - 1;
		// mListView.setSelection(nSelection);
		// 滚动动画
		mSpeakAdapter.smoothToBottomWithAnim(mListView);
	}

	public void onSpeakViewResult(SpeakItemData data) {

		if (data == null) {
			// alertDialog("提示","请再说一遍","好");
			return;
		}

		// SQLiteDom sqliteDom = new SQLiteDom();
		// KouyiRecord kouyi = new KouyiRecord();
		// if (data.isExistTrans()) {
		// kouyi.setSaid(data.speak);
		// kouyi.setTranslated(data.trans);
		// }
		// else {
		// kouyi.setSaid(data.speak);
		// kouyi.setTranslated("");
		// }
		//
		// sqliteDom.insertRecord(kouyi);

		mResultTipAudio.play();

		// mSpeakAdapter.addItem(data);
		mSpeakAdapter.updateAfterSpeak(data);
		mSpeakAdapter.setFocus(data);
		mEditInput.setText(data.speak);
		mSpeakAdapter.notifyDataSetChanged();
		// updateListView();
		// int nSelection = mSpeakAdapter.getCount() - 1;
		// mListView.setSelection(nSelection);
		// 滚动动画
		// mSpeakAdapter.smoothToBottomWithAnim(mListView);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	public void setLocaleTTS(boolean localeTTS) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mDialog.close();
			mInterpretView.dismissPopWindow();
			// mInterpretView.dismissPopWindow();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		closePM();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditInput.getWindowToken(), 0);
		super.onPause();
	}

	private void commontRecord(int position, final int flag) {
		if (mSelectedItemData.flag == 3) {
			mSelectedItemData.flag = flag;
			mSpeakAdapter.commont(position, mSelectedItemData);
			new Thread() {
				public void run() {
					HttpGetQtEv httpGetQtEv = new HttpGetQtEv();
					String result = httpGetQtEv.setEvToServer(mSelectedItemData.taskId, flag);
				}
			}.start();
		} else {
			mSelectedItemData.flag = flag;
			mSpeakAdapter.commont(position, mSelectedItemData);
		}
		mSpeakAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(v==m_btn_record){
			if(mUser.isLClickRecord()){
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
				
					mInterpretView.showPopWindowLocation();
					mDialog.show();

					break;
				case MotionEvent.ACTION_UP:

					mDialog.onRecognizerViewRecord();
					
					break;
				}
			}else{
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					mInterpretView.showPopWindowLocation();
					mDialog.show();
					break;
				}
			}
			
		}
		return false;
	}
	
	
//	//分享内容编辑框
//	private PopupWindow popEdit;
//	private EditText edtContent;
//	private Button btnSend;
//	private Button btnCancle;
//	private TextView tvCount;
//	private int count = 140;
//	public void initPopEditWC(View view){
//		edtContent = (EditText) view.findViewById(R.id.edtContent);
//		btnSend = (Button) view.findViewById(R.id.btnSend);
//		btnCancle = (Button) view.findViewById(R.id.btnCancle);
//		tvCount = (TextView) view.findViewById(R.id.tvCount);
//		
//	}
//	public String showPopEditWC(String str,final int a){
//		
//		View view = LayoutInflater.from(this).inflate(R.layout.pop_editweibocontent, null);
//		initPopEditWC(view);
//		edtContent.setText(str);
//		tvCount.setText((count-str.length())+"/"+count);
//		edtContent.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				tvCount.setText((count-edtContent.getText().toString().length())+"/"+count);
//				
//			}
//		});
//		btnSend.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				popEdit.dismiss();
//				switch(a){
//				case 2:
//					MainActivity.iSina.sendWeibo(edtContent.getText().toString().trim(),
//							InterpretActivity.this);
//					break;
//				case 3:
//					MainActivity.iTencent.sendWeibo(edtContent.getText().toString().trim(),
//							InterpretActivity.this);
//					break;
//				case 4:
//					MainActivity.iRenren.sendWeibo(edtContent.getText().toString().trim(),
//							InterpretActivity.this);
//					break;
//				case 5:
//					MainActivity.ikaixin.sendWeibo(edtContent.getText().toString().trim(),
//							InterpretActivity.this);
//					break;
//				}
//				
//			}
//		});
//		btnCancle.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				popEdit.dismiss();
//				
//			}
//		});
//		popEdit = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		popEdit.setBackgroundDrawable(new BitmapDrawable());
//		popEdit.showAtLocation(findViewById(R.id.interpret_parent), Gravity.CENTER, 0, 0);
//		popEdit.setFocusable(true);
//		popEdit.setOutsideTouchable(false);
//		popEdit.update();
//		return null;
//	}
}