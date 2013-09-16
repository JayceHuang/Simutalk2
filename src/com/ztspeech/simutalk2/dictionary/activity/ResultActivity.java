package com.ztspeech.simutalk2.dictionary.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialog;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.adapter.UserInputLvAdapter;
import com.ztspeech.simutalk2.dictionary.adapter.WordsLvAdapter;
import com.ztspeech.simutalk2.dictionary.adapter.WordsSimpleLvAdapter;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Words;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.qa.MainActivity;
import com.ztspeech.simutalk2.qa.view.InterpretView;
import com.ztspeech.simutalk2.weibo.EditContent;
import com.ztspeech.simutalk2.weibo.IRenren;
import com.ztspeech.simutalk2.weibo.ISina;
import com.ztspeech.simutalk2.weibo.ITencent;
import com.ztspeech.simutalk2.weibo.Ikaixin;

public class ResultActivity extends BaseActivity implements OnClickListener, TextWatcher, OnItemClickListener,
		OnEngineListener, OnTouchListener {
	private int page;
	private ListView lvInputRecord;
	private ListView lvSimple;
	private ListView lvWords;
	private EditText edtWord;
	private Button btnSure;
	private List listSimple = null;
	private List listInputRecord = null;
	private List listWords = null;
	private Integer childId;
	private Integer categroyId;
	private String strWords;
	private Intent fromWhere;
	private SQLiteDom sqliteDom = null;
	private TextView tvNoResult;
	private WordsLvAdapter wLv;
	private UserInputLvAdapter ulv;
	private WordsSimpleLvAdapter wslv;

	private UserInfo mUser = UserInfo.getInstanse();
	private UnisayRecognizerDialog mDialog = null;
	private Button btnLuyin = null;
	private Integer chOrEn = 0;
	private String from;
	// =========================================================
	// private PopupWindow mRecognizerWindow = null;
	private InterpretView mInterpretView;

	private PopupWindow popMore;
	private GridView gdv;

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

				break;
			default:
				break;
			}
		}

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
		registerBoradcastReceiver();

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
		fromWhere = getIntent();
		from = fromWhere.getStringExtra("from");
		categroyId = fromWhere.getIntExtra("categroyId", 0);
		strWords = fromWhere.getStringExtra("strWords");
		childId = fromWhere.getIntExtra("childId", 0);
		String title = fromWhere.getStringExtra("title");
		setTitleAndContentView(R.layout.activity_result, title);
		lvWords = (ListView) findViewById(R.id.lvWords);
		lvInputRecord = (ListView) findViewById(R.id.lvInputRecord);
		lvSimple = (ListView) findViewById(R.id.lvSimple);
		edtWord = (EditText) findViewById(R.id.edtInputWords);
		edtWord.setText(strWords);
		btnSure = (Button) findViewById(R.id.btnSearch);
		btnSure.setOnClickListener(this);
		edtWord.addTextChangedListener(this);
		edtWord.setOnTouchListener(this);
		tvNoResult = (TextView) findViewById(R.id.tvNoResult);
		lvWords.setOnItemClickListener(this);
		lvInputRecord.setOnItemClickListener(this);
		lvSimple.setOnItemClickListener(this);

		btnLuyin = (Button) findViewById(R.id.btnLuyin);
		btnLuyin.setOnClickListener(this);
		mInterpretView = new InterpretView(this, mHandlerKeyboard, this);
		mDialog = new UnisayRecognizerDialog(this, "", this, mInterpretView.mNewRecognizerViewListenerInterface);

		init(fromWhere.getStringExtra("from"));
	}

	public void init(String from) {
		if (childId == 0 && categroyId == 0) {
			childId = null;
			categroyId = null;
			// list = sqliteDom.getSimilarResult(null, null, childId,1);
		} else if (childId != 0 && categroyId == 0) {
			categroyId = null;
			// list = sqliteDom.getSimilarResult(null, null, childId,1);
		} else if (childId == 0 && categroyId != 0) {
			childId = null;
			// list = sqliteDom.getSimilarResult(categroyId,null, null,1);
		} else if (childId != 0 && categroyId != 0) {
			// list = sqliteDom.getSimilarResult(null, null, childId,1);
		}
		if (from.equals("search")) {
			lvInputRecord.setVisibility(View.VISIBLE);
			if (edtWord.getText().toString().equals("") || edtWord.getText().toString() == null) {
				getUserInput();
			}
			if (wLv == null) {
				listWords = searchWordsBystrWords(strWords);
				wLv = new WordsLvAdapter(this, listWords, childId);
				wLv.setCHorEN(chOrEn);
				wLv.addFooter(lvWords);
				lvWords.setAdapter(wLv);
			} else {
				listWords.clear();
				listWords.addAll(searchWordsBystrWords(strWords));
				wLv.setCHorEN(chOrEn);
				wLv.addFooter(lvWords);
				wLv.notifyDataSetChanged();
			}
		} else if (from.equals("detail")) {
			lvWords.setVisibility(View.VISIBLE);
			if (wLv == null) {
				listWords = searchWordsBystrWords(strWords);
				wLv = new WordsLvAdapter(this, listWords, childId);
				wLv.setCHorEN(chOrEn);
				wLv.addFooter(lvWords);
				lvWords.setAdapter(wLv);
			} else {
				listWords.clear();
				listWords.addAll(searchWordsBystrWords(strWords));
				wLv.setCHorEN(chOrEn);
				wLv.addFooter(lvWords);
				wLv.notifyDataSetChanged();
			}
		}
	}

	public void setTitleAndContentView(int layout, String title) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(title);
		btnCancle = (ImageButton) findViewById(R.id.btnLeft);
		btnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String str = edtWord.getText().toString();
				if (str.equals("")) {
					ResultActivity.this.finish();
				} else {
					edtWord.setText("");
				}
			}
		});
	}

	@Override
	public void onEngineEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEngineResult(List arg0, int arg1, String id) {
		mDialog.close();
		mInterpretView.dismissPopWindow();
		if (arg0 == null) {
			return;
		}
		if (arg0.size() > 0) {
			String text = (String) ((String) arg0.get(0)).replace(".", "").replace("。", "").replace("?", "")
					.replace("？", "").replace("!", "").replace("！", "");
			edtWord.setText(text);
		}

	}

	@Override
	public void onEngineStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
		if (parent == lvInputRecord) {
			if (position == listInputRecord.size()) {
				sqliteDom.deleteAllUserInput();
				lvInputRecord.setVisibility(View.GONE);
				tvNoResult.setVisibility(View.VISIBLE);
				tvNoResult.setText("- 无历史记录 -");
			} else {
				edtWord.setText(" ");
				lvInputRecord.setVisibility(View.GONE);
				lvSimple.setVisibility(View.GONE);
				lvWords.setVisibility(View.VISIBLE);

				page = 1;
				if (wLv == null) {
					listWords = searchWordsBystrWords(listInputRecord.get(position).toString());
					wLv = new WordsLvAdapter(this, listWords, childId);
					wLv.setCHorEN(chOrEn);
					wLv.addFooter(lvWords);
					lvWords.setAdapter(wLv);
				} else {
					listWords.clear();
					listWords.addAll(searchWordsBystrWords(listInputRecord.get(position).toString()));
					wLv.setCHorEN(chOrEn);
					wLv.addFooter(lvWords);
					wLv.notifyDataSetChanged();
				}
			}
		} else if (parent == lvSimple) {
			lvInputRecord.setVisibility(View.GONE);
			lvSimple.setVisibility(View.GONE);
			lvWords.setVisibility(View.VISIBLE);
			if (listSimple.size() > 0) {
				sqliteDom.saveUserInput(edtWord.getText().toString().trim());
				// edtWord.setText(listSimple.get(position))
			}
			page = 1;
			if (wLv == null) {
				listWords = new ArrayList();
				listWords.add(listSimple.get(position));
				wLv = new WordsLvAdapter(this, listWords, childId);
				wLv.setCHorEN(chOrEn);
				lvWords.setAdapter(wLv);
			} else {
				listWords.clear();
				listWords.add(listSimple.get(position));
				wLv.setCHorEN(chOrEn);
				wLv.removeFooter(lvWords);
				wLv.notifyDataSetChanged();
			}
		} else if (parent == lvWords) {
			if (position == listWords.size()) {
				int count1 = listWords.size();
				addLvItemFenYe();
				int count2 = listWords.size();
				if (count1 == count2) {
					wLv.removeFooter(lvWords);
				}
				wLv.notifyDataSetChanged();

			}
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		strWords = edtWord.getText().toString().trim();
		if (strWords.equals("")) {
			imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
			if (from.equals("search")) {
				lvInputRecord.setVisibility(View.VISIBLE);
				lvSimple.setVisibility(View.GONE);
				lvWords.setVisibility(View.GONE);
				tvNoResult.setVisibility(View.GONE);
				if (ulv == null) {
					getUserInput();
				} else {
					listInputRecord.clear();
					listInputRecord.addAll(sqliteDom.getUserInput());
					ulv.notifyDataSetChanged();
					if (listInputRecord.size() == 0) {
						lvInputRecord.setVisibility(View.GONE);
						tvNoResult.setVisibility(View.VISIBLE);
						tvNoResult.setText("- 无历史记录 -");
					}
				}
			} else if (from.equals("detail")) {
				lvInputRecord.setVisibility(View.GONE);
				lvSimple.setVisibility(View.GONE);
				lvWords.setVisibility(View.VISIBLE);
				tvNoResult.setVisibility(View.GONE);
				if (wLv == null) {
					listWords = searchWordsBystrWords(strWords);
					wLv = new WordsLvAdapter(this, listWords, childId);
					wLv.setCHorEN(chOrEn);
					wLv.addFooter(lvWords);
					lvWords.setAdapter(wLv);
				} else {
					listWords.clear();
					listWords.addAll(searchWordsBystrWords(strWords));
					wLv.addFooter(lvWords);
					wLv.setCHorEN(chOrEn);
					wLv.notifyDataSetChanged();
				}
			}
		} else {
			chOrEn = new PublicArithmetic().isWhat(strWords);
			lvInputRecord.setVisibility(View.GONE);
			lvSimple.setVisibility(View.VISIBLE);
			lvWords.setVisibility(View.GONE);
			getList(edtWord.getText().toString());
		}

	}

	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (v == btnSure) {
			strWords = edtWord.getText().toString().trim();
			if (!"".equals(strWords) && strWords != null) {
				imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
				if (listSimple != null) {
					if (listSimple.size() > 0) {
						sqliteDom.saveUserInput(strWords);
					}
					page = 1;
					lvSimple.setVisibility(View.GONE);
					lvInputRecord.setVisibility(View.GONE);
					lvWords.setVisibility(View.VISIBLE);
					if (wLv == null) {
						listWords = listSimple;
						wLv = new WordsLvAdapter(this, listWords, childId);
						wLv.setCHorEN(chOrEn);
						wLv.addFooter(lvWords);
						lvWords.setAdapter(wLv);
					} else {
						listWords.clear();
						listWords.addAll(listSimple);
						wLv.setCHorEN(chOrEn);
						wLv.addFooter(lvWords);
						wLv.notifyDataSetChanged();
					}
				}
			} else {
				Toast.makeText(ResultActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
			}

		} else if (v == btnLuyin) {
			imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
			if (mUser.s2sType.equals(UserInfo.S2T_CH2EN)) {
				mDialog.setToChineseEngine();
			} else if (mUser.s2sType.equals(UserInfo.S2T_EN2CH)) {
				mDialog.setToEnglishEngine();
			}
			mInterpretView.showPopWindowLocation();
			mDialog.show();
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

	public void getList(String str) {
		str = str.trim();
		PublicArithmetic pa = new PublicArithmetic();
		Integer enOrCh = pa.isWhat(str);
		switch (enOrCh) {
		case 0:
		case 3:
			listSimple = searchByChinese(str);
			break;
		case 1:
		case 2:
			listSimple = searchByEnglish(str);
			break;
		}
		if (listSimple.size() > 0) {
			lvSimple.setVisibility(View.VISIBLE);
			tvNoResult.setVisibility(View.GONE);
			wslv = new WordsSimpleLvAdapter(this, listSimple, enOrCh);
			lvSimple.setAdapter(wslv);
		} else {
			lvSimple.setVisibility(View.GONE);
			tvNoResult.setVisibility(View.VISIBLE);
			tvNoResult.setText(getResources().getString(R.string.dictionary_noresult));
		}
	}

	public void getUserInput() {
		listInputRecord = sqliteDom.getUserInput();
		ulv = new UserInputLvAdapter(this, listInputRecord);
		ulv.addFooter(lvInputRecord);
		lvInputRecord.setAdapter(ulv);
		if (listInputRecord.size() == 0) {
			lvInputRecord.setVisibility(View.GONE);
			tvNoResult.setVisibility(View.VISIBLE);
			tvNoResult.setText("- 无历史记录 -");
		}
	}

	public List searchWordsBystrWords(String strWords) {
		if (strWords != null && !"".equals(strWords)) {
			strWords = strWords.trim();
			chOrEn = new PublicArithmetic().isWhat(strWords);
		}
		List newList = null;
		if ("".equals(strWords) || strWords == null) {
			newList = sqliteDom.getSimilarResult(null, null, childId, 1);
		} else {
			switch (chOrEn) {
			case 0:
			case 3:
				newList = searchByChinese(strWords);
				break;
			case 1:
			case 2:
				newList = searchByEnglish(strWords);
				break;
			}
		}
		return newList;
	}

	public List searchByChinese(String strWords) {
		page = 1;
		List newList = null;
		if (childId == null && categroyId == null) {
			newList = sqliteDom.getSimilarResult(strWords, null, childId, page);
		} else if (childId != null && categroyId == null) {
			newList = sqliteDom.getSimilarResult(strWords, null, childId, page);
		} else if (childId == null && categroyId != null) {
			// newList = sqliteDom.getSimilarResult(categroyId,strWords,
			// null,page);
			newList = sqliteDom.getSimilarResult(strWords, null, childId, page);
		} else if (childId != null && categroyId != null) {
			newList = sqliteDom.getSimilarResult(strWords, null, childId, page);
		}

		return newList;
	}

	public List searchByEnglish(String strWords) {
		page = 1;
		List newList = null;
		if (childId == null && categroyId == null) {
			newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
		} else if (childId != null && categroyId == null) {
			newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
		} else if (childId == null && categroyId != null) {
			// newList = sqliteDom.getSimilarResult(categroyId,null,
			// strWords,page);
			newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
		} else if (childId != null && categroyId != null) {
			newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
		}

		return newList;
	}

	public void addLvItemFenYe() {
		page++;
		strWords = edtWord.getText().toString().trim();

		List newList = null;
		if (childId == null && categroyId != null) {
			if (strWords.equals("")) {
				newList = sqliteDom.getSimilarResult(categroyId, null, null, page);
			} else {
				PublicArithmetic pa = new PublicArithmetic();
				Integer enOrCh = pa.isWhat(strWords);
				switch (enOrCh) {
				case 0:
				case 3:
					newList = sqliteDom.getSimilarResult(categroyId, strWords, null, page);
					break;
				case 1:
				case 2:
					newList = sqliteDom.getSimilarResult(categroyId, null, strWords, page);
					break;
				}
			}
		} else {
			if (strWords.equals("")) {
				newList = sqliteDom.getSimilarResult(null, null, childId, page);
			} else {

				PublicArithmetic pa = new PublicArithmetic();
				Integer enOrCh = pa.isWhat(strWords);
				switch (enOrCh) {
				case 0:
				case 3:
					newList = sqliteDom.getSimilarResult(strWords, null, childId, page);
					break;
				case 1:
				case 2:
					newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
					break;
				}
			}

		}

		listWords.addAll(newList);
	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Util.ACTION_POMENU);
		registerReceiver(receivePopMenuEvent, myIntentFilter);
	}

	public BroadcastReceiver receivePopMenuEvent = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Util.ACTION_POMENU)) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
				// showPopMenu(intent.getParcelableExtra("word"),intent.getIntExtra("button",
				// 0));
				// showDialogMenu(intent.getParcelableExtra("word"),
				// intent.getIntExtra("button", 0));

				showPopMore(intent.getParcelableExtra("word"));
			}
		}
	};

	public void showDialogMenu(Parcelable word, final int buttonNo) {
		final Words words = (Words) word;
		AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
		CharSequence[] items = new CharSequence[4];
		int i = 0;
		items[i++] = getResources().getString(R.string.trans_share_fullscreen);
		items[i++] = getResources().getString(R.string.trans_share_send);
		items[i++] = getResources().getString(R.string.trans_share_copy);
		items[i++] = getResources().getString(R.string.trans_share_collect);
		builder.setTitle("选择");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					Intent intent = new Intent(ResultActivity.this, ShowWhiteBordActivity.class);
					intent.putExtra("english", words.getEnglish());
					intent.putExtra("chinese", words.getChinese());
					intent.putExtra("chOrEn", chOrEn);
					startActivity(intent);
					break;
				case 1:
					if (chOrEn == 1) {
						switch (buttonNo) {
						case 1:
							sendSMS(words.getChinese() + "\n" + words.getEnglish());
							break;
						case 2:
							sendSMS(words.getEnglish());
							break;
						}
					} else {
						switch (buttonNo) {
						case 1:
							sendSMS(words.getEnglish() + "\n" + words.getChinese());
							break;
						case 2:
							sendSMS(words.getChinese());
							break;
						}
					}
					break;

				case 2:
					if (chOrEn == 1) {
						switch (buttonNo) {
						case 1:
							shareText(words.getChinese() + "\n" + words.getEnglish());
							break;
						case 2:
							shareText(words.getEnglish());
							break;
						}
					} else {
						switch (buttonNo) {
						case 1:
							shareText(words.getEnglish() + "\n" + words.getChinese());
							break;
						case 2:
							shareText(words.getChinese());
							break;
						}
					}
					break;
				case 3:
					int result = sqliteDom.insertCollecterFromSearch(words);
					switch (result) {
					case 0:
						new AlertDialog.Builder(ResultActivity.this).setTitle(null).setMessage("已加入收藏夹")
								.setPositiveButton("确定", null).show();
						break;
					default:
						Toast.makeText(ResultActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
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
			edtWord.setText(strWords);
		}
		return false;
	}

	ArrayList<Map<String, Object>> functionList = null;

	public void initPopMore(View view) {
		functionList = new ArrayList<Map<String, Object>>();
		gdv = (GridView) view.findViewById(R.id.gdv);
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
		m6.put("iconItem", R.drawable.trans_ci_pop_button_12);
		m6.put("action", 6);
		functionList.add(m6);
		Map<String, Object> m7 = new HashMap<String, Object>();
		m7.put("textItem", "");
		m7.put("iconItem", R.drawable.trans_ci_pop_button_07);
		m7.put("action", 7);
		functionList.add(m7);
		Map<String, Object> m8 = new HashMap<String, Object>();
		m8.put("textItem", "");
		m8.put("iconItem", R.drawable.trans_ci_pop_button_08);
		m8.put("action", 8);
		functionList.add(m8);
		Map<String, Object> m9 = new HashMap<String, Object>();
		m9.put("textItem", "");
		m9.put("iconItem", R.drawable.trans_ci_pop_button_09);
		m9.put("action", 9);
		functionList.add(m9);
		// Map<String, Object> m9 = new HashMap<String, Object>();
		// m9.put("textItem", "");
		// m9.put("iconItem", R.drawable.trans_ci_pop_button_10);
		// m9.put("action", 9);
		// functionList.add(m9);
		// Map<String, Object> m10 = new HashMap<String, Object>();
		// m10.put("textItem", "");
		// m10.put("iconItem", R.drawable.trans_ci_pop_button_11);
		// m10.put("action", 10);
		// functionList.add(m10);

		SimpleAdapter sa = new SimpleAdapter(ResultActivity.this, functionList, R.layout.gdv_interpret_item,
				new String[] { "textItem", "iconItem" }, new int[] { R.id.tvFunction, R.id.imageFunction });
		gdv.setAdapter(sa);
	}

	public void showPopMore(Parcelable word) {
		final Words words = (Words) word;
		View view = LayoutInflater.from(this).inflate(R.layout.pop_interpret_more, null);
		initPopMore(view);
		gdv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (Integer.parseInt(functionList.get(arg2).get("action").toString())) {
				case 0:
					popMore.dismiss();
					Uri smsToUri = Uri.parse("smsto:");// 联系人地址
					Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
					mIntent.putExtra("sms_body", words.getChinese() + "\n" + words.getEnglish());// 短信内容
					startActivity(mIntent);
					break;
				case 1:
					popMore.dismiss();
					Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
					mailIntent.setType("plain/test");

					mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
							words.getChinese() + "\n" + words.getEnglish());
					startActivity(Intent.createChooser(mailIntent, "发送邮件"));
					break;
//				case 2:
//					popMore.dismiss();
//					showPopEditWC(words.getChinese() + "\n" + words.getEnglish(), Integer.parseInt(functionList.get(arg2).get("action").toString()));
////					MainActivity.iSina.sendWeibo(words.getChinese() + "\n" + words.getEnglish(), ResultActivity.this);
//					break;
//				case 3:
//					popMore.dismiss();
//					showPopEditWC(words.getChinese() + "\n" + words.getEnglish(), Integer.parseInt(functionList.get(arg2).get("action").toString()));
////					MainActivity.iTencent.sendWeibo(words.getChinese() + "\n" + words.getEnglish(), ResultActivity.this);
//					break;
//				case 4:
//					popMore.dismiss();
//					showPopEditWC(words.getChinese() + "\n" + words.getEnglish(), Integer.parseInt(functionList.get(arg2).get("action").toString()));
////					MainActivity.iRenren.sendWeibo(words.getChinese() + "\n" + words.getEnglish(), ResultActivity.this);
//					break;
//				case 5:
//					popMore.dismiss();
//					showPopEditWC(words.getChinese() + "\n" + words.getEnglish(), Integer.parseInt(functionList.get(arg2).get("action").toString()));
////					MainActivity.ikaixin.sendWeibo(words.getChinese() + "\n" + words.getEnglish(), ResultActivity.this);
//
//					break;
				case 2:
				case 3:
				case 4:
				case 5:
					popMore.dismiss();
					Intent intentW = new Intent(ResultActivity.this,EditContent.class);
					intentW.putExtra("action", Integer.parseInt(functionList.get(arg2).get("action").toString()));
					intentW.putExtra("content", words.getChinese() + "\n" + words.getEnglish());
					startActivityForResult(intentW, 000011);
					break;
				case 6:
					popMore.dismiss();
					ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clipboard.setText(words.getEnglish() + "\n" + words.getChinese());
					break;
				case 7:
					popMore.dismiss();
					// fullScreenTransData();
					Intent intent = new Intent(ResultActivity.this, ShowWhiteBordActivity.class);
					intent.putExtra("english", words.getEnglish());
					intent.putExtra("chinese", words.getChinese());
					intent.putExtra("chOrEn", chOrEn);
					startActivity(intent);
					break;
				case 8:
					popMore.dismiss();
					int result = sqliteDom.insertCollecterFromSearch(words);
					switch (result) {
					case 0:
						new AlertDialog.Builder(ResultActivity.this).setTitle(null).setMessage("已加入收藏夹")
								.setPositiveButton("确定", null).show();
						break;
					default:
						Toast.makeText(ResultActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
					}
					// SQLiteDom sqliteDom = new SQLiteDom();
					// // if(UserInfo.S2T_CH2EN.equals(mUser.s2sType)){
					// KouyiRecord kk = mTableTransText.getRecords(position);
					// // System.out.println(mTableCH.get(0).text);
					// Collecter collecter = new Collecter();
					// collecter.setChildId(6);
					// collecter.setText1(kk.getSaid());
					// collecter.setText2(kk.getTranslated());
					// int result =
					// sqliteDom.insertCollecterFromKouyi(collecter);
					// switch (result) {
					// case 0:
					// new
					// AlertDialog.Builder(InterpretActivity.this).setTitle(null).setMessage("已加入收藏夹")
					// .setPositiveButton("确定", null).show();
					// break;
					// default:
					// Toast.makeText(ResultActivity.this, "添加成功",
					// Toast.LENGTH_SHORT).show();
					// }
					break;
				case 9:
					popMore.dismiss();
					// shareText();
					if (chOrEn == 1) {
						sendSMS(words.getChinese() + "\n" + words.getEnglish());

					} else {
						sendSMS(words.getEnglish() + "\n" + words.getChinese());

					}
					break;
				case 10:
					popMore.dismiss();
					// deleteSelected(position);
					break;
				case 11:
					popMore.dismiss();
					// mSpeakAdapter.clear();
					// updateListView();
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

	@Override
	protected void onDestroy() {
		unregisterReceiver(receivePopMenuEvent);
		super.onDestroy();
	}
	
	@Override
	protected void onStop() {
		if(TextPlayer.getInstance().isPlaying()){
			TextPlayer.getInstance().stop();
		}
		super.onStop();
	}
	
//	//分享内容编辑框
//		private PopupWindow popEdit;
//		private EditText edtContent;
//		private Button btnSend2;
//		private Button btnCancle2;
//		private TextView tvCount;
//		private int count = 140;
//		public void initPopEditWC(View view){
//			edtContent = (EditText) view.findViewById(R.id.edtContent);
//			btnSend2 = (Button) view.findViewById(R.id.btnSend);
//			btnCancle2 = (Button) view.findViewById(R.id.btnCancle);
//			tvCount = (TextView) view.findViewById(R.id.tvCount);
//			edtContent.setMaxEms(count);
//			
//		}
//		public String showPopEditWC(String str,final int a){
//			
//			View view = LayoutInflater.from(this).inflate(R.layout.pop_editweibocontent, null);
//			initPopEditWC(view);
//			edtContent.setText(str);
//			tvCount.setText((count-str.length())+"/"+count);
//			edtContent.addTextChangedListener(new TextWatcher() {
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count,
//						int after) {
//					
//					
//				}
//				
//				@Override
//				public void afterTextChanged(Editable s) {
//					tvCount.setText((count-edtContent.getText().toString().length())+"/"+count);
//					
//				}
//			});
//			btnSend2.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					popEdit.dismiss();
//					switch(a){
//					case 2:
//						MainActivity.iSina.sendWeibo(edtContent.getText().toString().trim(),
//								ResultActivity.this);
//						break;
//					case 3:
//						MainActivity.iTencent.sendWeibo(edtContent.getText().toString().trim(),
//								ResultActivity.this);
//						break;
//					case 4:
//						MainActivity.iRenren.sendWeibo(edtContent.getText().toString().trim(),
//								ResultActivity.this);
//						break;
//					case 5:
//						MainActivity.ikaixin.sendWeibo(edtContent.getText().toString().trim(),
//								ResultActivity.this);
//						break;
//					}
//					
//				}
//			});
//			btnCancle2.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					popEdit.dismiss();
//					
//				}
//			});
//			popEdit = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			popEdit.setBackgroundDrawable(new BitmapDrawable());
//			popEdit.showAtLocation(findViewById(R.id.interpret_parent), Gravity.CENTER, 0, 0);
//			popEdit.setFocusable(true);
//			popEdit.setOutsideTouchable(false);
//			popEdit.update();
//			return null;
//		}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==resultCode&&resultCode==000011){
			int a = data.getIntExtra("action", 0);
			String str = data.getStringExtra("content");
			switch(a){
			case 2:
				MainActivity.iSina.sendWeibo(str,
						ResultActivity.this);
				break;
			case 3:
				MainActivity.iTencent.sendWeibo(str,
						ResultActivity.this);
				break;
			case 4:
				MainActivity.iRenren.sendWeibo(str,
						ResultActivity.this);
				break;
			case 5:
				MainActivity.ikaixin.sendWeibo(str,
						ResultActivity.this);
				break;
			case 0:
				Util.showToast(this, "发送错误");
				break;
			}
		}
	}
}
