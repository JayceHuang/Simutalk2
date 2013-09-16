package com.ztspeech.simutalk2.dictionary.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialog;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.adapter.BaseLvAdapter;
import com.ztspeech.simutalk2.dictionary.adapter.UserInputLvAdapter;
import com.ztspeech.simutalk2.dictionary.adapter.WordsSimpleLvAdapter;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Words;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.qa.view.InterpretView;

public class WordsAutoCompletedActivity extends BaseActivity implements OnClickListener, TextWatcher,
		OnItemClickListener, OnEngineListener {

	private int page;
	private ListView lvWords;
	private EditText edtWord;
	private Button btnSure;
	private List list = null;
	private Integer childId;
	private Integer categroyId;
	private String strWords;
	private Intent fromWhere;
	private SQLiteDom sqliteDom = null;
	private TextView tvNoResult;
	BaseLvAdapter wLv;

	private UserInfo mUser = UserInfo.getInstanse();
	private UnisayRecognizerDialog mDialog = null;
	private Button btnLuyin = null;
	// =========================================================
	// private PopupWindow mRecognizerWindow = null;
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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		sqliteDom = new SQLiteDom();

		super.onCreate(savedInstanceState);
		page = 1;
		setTitleAndContentView(R.layout.activity_words_simple, "搜索");
		fromWhere = getIntent();
		categroyId = fromWhere.getIntExtra("categroyId", 0);
		strWords = fromWhere.getStringExtra("strWords");
		childId = fromWhere.getIntExtra("childId", 0);
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
		lvWords = (ListView) findViewById(R.id.lvWords);
		edtWord = (EditText) findViewById(R.id.edtInputWords);
		edtWord.setText(strWords);
		btnSure = (Button) findViewById(R.id.btnSure);
		btnSure.setOnClickListener(this);
		edtWord.addTextChangedListener(this);
		tvNoResult = (TextView) findViewById(R.id.tvNoResult);
		if (edtWord.getText().toString().equals("") || edtWord.getText().toString() == null) {
			getUserInput();
		} else {
			getList();
		}
		lvWords.setOnItemClickListener(this);

		btnLuyin = (Button) findViewById(R.id.btnLuyin);
		btnLuyin.setOnClickListener(this);
		mInterpretView = new InterpretView(this, mHandlerKeyboard, this);
		mDialog = new UnisayRecognizerDialog(this, "", this, mInterpretView.mNewRecognizerViewListenerInterface);

	}

	@Override
	protected void onResume() {
		fromWhere = getIntent();
		super.onResume();

	}

	@Override
	public void onClick(View v) {

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (v == btnSure) {
			strWords = edtWord.getText().toString().trim();
			if (!"".equals(strWords) && strWords != null) {
				imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
				if (list != null) {
					if (list.size() > 0) {
						sqliteDom.saveUserInput(strWords);
					}
					if (fromWhere.getStringExtra("from").equals("yes")) {
						Intent intent = new Intent(WordsAutoCompletedActivity.this, WordsActivity.class);
						intent.putExtra("words", strWords);
						intent.putExtra("categroyId", categroyId);
						intent.putExtra("title", getResources().getString(R.string.dictionary_result_title));
						startActivity(intent);
					} else {
						Intent intent = new Intent(Util.ACTION_SENDMSG);
						// Intent intent = new
						// Intent(WordsAutoCompletedActivity.this,fromWhere.getClass());
						// intent.putParcelableArrayListExtra("wordsList",
						// wordsList);
						intent.putExtra("words", strWords);
						intent.putExtra("categroyId", categroyId);
						sendBroadcast(intent);
						WordsAutoCompletedActivity.this.finish();
					}
				}
			} else {
				Toast.makeText(WordsAutoCompletedActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
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
			// new AlertDialog.Builder(this).setTitle("选择识别方式").setItems(
			// new String[] { "中文", "英文" }, new
			// DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// switch(which){
			// case 0:
			// mDialog.setToChineseEngine();
			// mDialog.show();
			// break;
			// case 1:
			// mDialog.setToEnglishEngine();
			// mDialog.show();
			// break;
			// }
			//
			// }
			// }).setNegativeButton(
			// null, null).show();

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

	@Override
	public void afterTextChanged(Editable s) {
		strWords = edtWord.getText().toString().trim();
		if (wLv != null) {
			wLv.removeFooter(lvWords);
		}
		if (strWords.equals("")) {
			getUserInput();
		} else {
			getList();
		}

	}

	public void getList() {
		PublicArithmetic pa = new PublicArithmetic();
		Integer enOrCh = pa.isWhat(strWords);
		switch (enOrCh) {
		case 0:
		case 3:
			searchByChinese(strWords);
			// list = sqliteDom.getSimilarResult(strWords, null, childId,1);
			break;
		case 1:
		case 2:
			searchByEnglish(strWords);
			// list = sqliteDom.getSimilarResult(null, strWords, childId,1);
			break;
		}
		if (list.size() > 0) {
			lvWords.setVisibility(View.VISIBLE);
			tvNoResult.setVisibility(View.GONE);
			wLv = new WordsSimpleLvAdapter(this, list, enOrCh);
			lvWords.setAdapter(wLv);
		} else {
			lvWords.setVisibility(View.GONE);
			tvNoResult.setVisibility(View.VISIBLE);
			tvNoResult.setText(getResources().getString(R.string.dictionary_noresult));
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 == list.size()) {
			sqliteDom.deleteAllUserInput();
			lvWords.setVisibility(View.GONE);
			lvWords.setVisibility(View.GONE);
			tvNoResult.setVisibility(View.VISIBLE);
			tvNoResult.setText(getResources().getString(R.string.dictionary_noresult));
		} else {
			if ("".equals(strWords) || strWords == null) {
				if (fromWhere.getStringExtra("from").equals("yes")) {
					Intent intent = new Intent(WordsAutoCompletedActivity.this, WordsActivity.class);
					intent.putExtra("words", list.get(arg2).toString());
					intent.putExtra("categroyId", categroyId);
					intent.putExtra("title", getResources().getString(R.string.dictionary_result_title));
					startActivity(intent);
				} else {
					Intent intent = new Intent(Util.ACTION_SENDMSG);
					intent.putExtra("words", list.get(arg2).toString());
					intent.putExtra("categroyId", categroyId);
					// intent.putExtra("title",
					// getResources().getString(R.string.dictionary_result_title));
					sendBroadcast(intent);
					WordsAutoCompletedActivity.this.finish();
				}

			} else {
				if (fromWhere.getStringExtra("from").equals("yes")) {
					ArrayList<Words> wordsList = new ArrayList<Words>();
					wordsList.add((Words) list.get(arg2));
					Intent intent = new Intent(WordsAutoCompletedActivity.this, WordsActivity.class);
					intent.putParcelableArrayListExtra("wordsList", wordsList);
					switch (new PublicArithmetic().isWhat(strWords)) {
					case 0:
					case 3:
						intent.putExtra("words", wordsList.get(0).getChinese());
						sqliteDom.saveUserInput(edtWord.getText().toString().trim());
						break;
					case 1:
					case 2:
						intent.putExtra("words", wordsList.get(0).getEnglish());
						sqliteDom.saveUserInput(edtWord.getText().toString().trim());
						break;
					}
					intent.putExtra("categroyId", categroyId);
					intent.putExtra("title", getResources().getString(R.string.dictionary_result_title));
					startActivity(intent);
				} else {
					ArrayList<Words> wordsList = new ArrayList<Words>();
					wordsList.add((Words) list.get(arg2));
					Intent intent = new Intent(Util.ACTION_SENDMSG);
					intent.putParcelableArrayListExtra("wordsList", wordsList);
					switch (new PublicArithmetic().isWhat(strWords)) {
					case 0:
					case 3:
						intent.putExtra("words", wordsList.get(0).getChinese());
						sqliteDom.saveUserInput(edtWord.getText().toString().trim());
						break;
					case 1:
					case 2:
						intent.putExtra("words", wordsList.get(0).getEnglish());
						sqliteDom.saveUserInput(edtWord.getText().toString().trim());
						break;
					}
					intent.putExtra("categroyId", categroyId);
					// intent.putExtra("title",
					// getResources().getString(R.string.dictionary_result_title));
					sendBroadcast(intent);
					WordsAutoCompletedActivity.this.finish();
				}
			}
		}
	}

	public void searchByChinese(String strWords) {
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

		list = newList;
	}

	public void searchByEnglish(String strWords) {
		page = 1;
		List newList = null;
		if (childId == null && categroyId == null) {
			newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
		} else if (childId != null && categroyId == null) {
			newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
		} else if (childId == null && categroyId != null) {
			newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
			// newList = sqliteDom.getSimilarResult(categroyId,null,
			// strWords,page);
		} else if (childId != null && categroyId != null) {
			newList = sqliteDom.getSimilarResult(null, strWords, childId, page);
		}

		list = newList;
	}

	public void getUserInput() {
		list = sqliteDom.getUserInput();
		wLv = new UserInputLvAdapter(this, list);
		// wLv = new WordsSimpleLvAdapter(this, list, 100);
		wLv.addFooter(lvWords);
		lvWords.setAdapter(wLv);
		if (list.size() == 0) {
			lvWords.setVisibility(View.GONE);
			tvNoResult.setVisibility(View.VISIBLE);
			tvNoResult.setText("- 无历史记录 -");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mDialog.close();
			mInterpretView.dismissPopWindow();
		}
		return super.onKeyDown(keyCode, event);
	}
}
