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
import com.ztspeech.simutalk2.data.TransTextTable;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.adapter.KouyiRecordLvAdapter;
import com.ztspeech.simutalk2.dictionary.adapter.KouyiSimpleLvAdapter;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Collecter;
import com.ztspeech.simutalk2.dictionary.entity.KouyiRecord;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.qa.MainActivity;
import com.ztspeech.simutalk2.qa.view.InterpretView;
import com.ztspeech.simutalk2.trans.InterpretActivity;
import com.ztspeech.simutalk2.weibo.EditContent;
import com.ztspeech.simutalk2.weibo.IRenren;
import com.ztspeech.simutalk2.weibo.ISina;
import com.ztspeech.simutalk2.weibo.ITencent;
import com.ztspeech.simutalk2.weibo.Ikaixin;

public class KouyiRecordActivity extends BaseActivity implements OnItemClickListener, TextWatcher, OnClickListener,
		OnEngineListener, OnTouchListener {

	private EditText edtWord;
	private Button btnSearch;
	private Intent fromWhere;
	private List list = null;
	private String strWords;
	private ListView lvWords;
	private View parent;
	private Integer childId;
	// private Integer categroyId;
	private int page = 1;
	private SQLiteDom sqliteDom = null;
	private KouyiRecordLvAdapter wLv;
	private KouyiSimpleLvAdapter wslv;
	private Integer chOrEn = 1;
	private ListView lvSimple;

	private TextView tvNoResult;
	private List<KouyiRecord> newList;

	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	private Button button6;
	private PopupWindow popAction;

	private UserInfo mUser = UserInfo.getInstanse();
	private UnisayRecognizerDialog mDialog = null;
	private Button btnLuyin = null;
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
		registerBoradcastReceiver();
		parent = getLayoutInflater().inflate(R.layout.activity_kouyirecord, null);

		mInterpretView = new InterpretView(this, mHandlerKeyboard, this);
		mDialog = new UnisayRecognizerDialog(this, "", this, mInterpretView.mNewRecognizerViewListenerInterface);

		childId = fromWhere.getIntExtra("childId", 0);
		setTitleAndContentView(R.layout.activity_kouyirecord, "口译记录");
		edtWord = (EditText) findViewById(R.id.edtInputWords);
		lvWords = (ListView) findViewById(R.id.lvCategroy);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(this);
		list = new ArrayList();
		getRecordList();
		wLv = new KouyiRecordLvAdapter(this, list, childId, parent);
		// wLv.addFooter(lvWords);
		lvWords.setAdapter(wLv);
		lvWords.setOnItemClickListener(this);
		edtWord.addTextChangedListener(this);
		edtWord.setOnTouchListener(this);

		btnLuyin = (Button) findViewById(R.id.btnLuyin);
		btnLuyin.setOnClickListener(this);

		tvNoResult = (TextView) findViewById(R.id.tvNoResult);
		tvNoResult.setText(getResources().getString(R.string.dictionary_noresult));

		lvSimple = (ListView) findViewById(R.id.lvSimple);
		lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
				lvSimple.setVisibility(View.GONE);
				lvWords.setVisibility(View.VISIBLE);
				list.clear();
				list.add(newList.get(position));
				wLv.notifyDataSetChanged();
			}
		});
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
					KouyiRecordActivity.this.finish();
				} else {
					edtWord.setText("");
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// if (arg2 == list.size()) {
		// page++;
		// int count1 = list.size();
		// getRecordList();
		// int count2 = list.size();
		// if (count1 == count2) {
		// wLv.removeFooter(lvWords);
		// }
		// wLv.notifyDataSetChanged();
		// }

		// Intent intent = new
		// Intent(KouyiRecordActivity.this,ShowWhiteBordActivity.class);
		// intent.putExtra("chinese", ((KouyiRecord)list.get(arg2)).getSaid());
		// intent.putExtra("english",
		// ((KouyiRecord)list.get(arg2)).getTranslated());
		// intent.putExtra("chOrEn", chOrEn);
		// startActivity(intent);
	}

	public void setChOrEN(String str) {
		chOrEn = new PublicArithmetic().isWhat(str);
	}

	public void getRecordList() {
		strWords = edtWord.getText().toString().trim();
		List newList;
		if (page == 1) {
			newList = sqliteDom.getSimilarResultInKouyi(strWords, page);
			list.clear();
			list.addAll(newList);
		} else {
			newList = sqliteDom.getSimilarResultInKouyi(strWords, page);
			list.addAll(newList);
		}
	}

	public void getRecordListAfterDelete() {
		page = 1;
		strWords = edtWord.getText().toString().trim();
		List newList;
		newList = sqliteDom.getSimilarResultInKouyi(strWords, page);
		list.clear();
		list.addAll(newList);
	}

	@Override
	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		switch (v.getId()) {
		case R.id.btnLuyin:
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
		case R.id.btn_record:
			if (!btnClickflag) {
				btnClickflag = true;
				mDialog.show();
				mInterpretView.setBtnRecordEnable(true);
			} else {
				btnClickflag = false;
				mDialog.close();
				mInterpretView.dismissPopWindow();
			}
			break;
		case R.id.btnSearch:
			strWords = edtWord.getText().toString().trim();
			if (!"".equals(strWords) && strWords != null) {
				imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
				page = 1;
				// wLv.removeFooter(lvWords);
				getRecordList();
				wLv.notifyDataSetChanged();
				// wLv.addFooter(lvWords);
				lvSimple.setVisibility(View.GONE);
				lvWords.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(KouyiRecordActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
			}

			break;
		}
		if (list == null || list.size() == 0) {
			tvNoResult.setVisibility(View.VISIBLE);
		} else {
			tvNoResult.setVisibility(View.GONE);
		}
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
				// showActionPop((KouyiRecord)
				// intent.getParcelableExtra("kouyi"),intent.getIntExtra("button",
				// 0));
				// showDialogMenu((KouyiRecord)
				// intent.getParcelableExtra("kouyi"),
				// intent.getIntExtra("button", 0));
				showPopMore((KouyiRecord) intent.getParcelableExtra("kouyi"));
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
			}

		}

	};

	public void initPopMenu(View view) {
		button1 = (Button) view.findViewById(R.id.button1);
		button1.setText("分          享");
		button2 = (Button) view.findViewById(R.id.button2);
		button2.setText("添加到收藏");
		button3 = (Button) view.findViewById(R.id.button3);
		button3.setText("复          制");
		button4 = (Button) view.findViewById(R.id.button4);
		button4.setText("删          除");
		button5 = (Button) view.findViewById(R.id.button5);
		button5.setText("删除全部");
		button6 = (Button) view.findViewById(R.id.button6);
		button6.setText("取             消");
	}

	public void showDialogMenu(KouyiRecord kouyi, final int buttonNo) {
		final Integer recordId = kouyi.getRecordId();
		final KouyiRecord kk = kouyi;
		AlertDialog.Builder builder = new AlertDialog.Builder(KouyiRecordActivity.this);
		CharSequence[] items = new CharSequence[6];
		int i = 0;
		items[i++] = getResources().getString(R.string.trans_share_fullscreen);
		items[i++] = getResources().getString(R.string.trans_share_send);
		items[i++] = getResources().getString(R.string.trans_share_copy);
		items[i++] = getResources().getString(R.string.trans_share_collect);
		items[i++] = getResources().getString(R.string.trans_share_delete);
		items[i++] = getResources().getString(R.string.trans_share_clear);
		builder.setTitle("选择");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0:
					Intent intent = new Intent(KouyiRecordActivity.this, ShowWhiteBordActivity.class);
					intent.putExtra("chinese", kk.getSaid());
					intent.putExtra("english", kk.getTranslated());
					intent.putExtra("chOrEn", chOrEn);
					startActivity(intent);
					break;
				case 1:
					switch (buttonNo) {
					case 1:
						sendSMS(kk.getSaid() + "\n" + kk.getTranslated());
						break;
					case 2:
						sendSMS(kk.getTranslated());
						break;
					}
					break;

				case 3:
					Collecter collecter = new Collecter();
					collecter.setChildId(6);
					collecter.setText1(kk.getSaid());
					collecter.setText2(kk.getTranslated());
					int result = sqliteDom.insertCollecterFromKouyi(collecter);
					switch (result) {
					case 0:
						// Toast.makeText(KouyiRecordActivity.this, "已加入收藏夹",
						// Toast.LENGTH_LONG).show();
						new AlertDialog.Builder(KouyiRecordActivity.this).setTitle(null).setMessage("已加入收藏夹")
								.setPositiveButton("确定", null).show();
						break;
					default:
						Toast.makeText(KouyiRecordActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
						// new
						// AlertDialog.Builder(KouyiRecordActivity.this).setTitle(null).setMessage("添加成功")
						// .setPositiveButton("确定", null).show();
					}
					break;
				case 2:
					switch (buttonNo) {
					case 1:
						shareText(kk.getSaid() + "\n" + kk.getTranslated());
						break;
					case 2:
						shareText(kk.getTranslated());
						break;
					}
					break;
				case 4:
					int index = sqliteDom.deleteRecordByIdReturnIndex(recordId);
					// TransTextTable mTableTransText =
					// MainActivity.mTableTransText;
					// mTableTransText.deleteByText(kk.getSaid(), index);
					getRecordListAfterDelete();
					wLv.notifyDataSetChanged();
					InterpretActivity.setIsDeletefromMyLiberaryTrue();
					break;
				case 5:
					new AlertDialog.Builder(KouyiRecordActivity.this).setTitle("删除全部内容").setView(null)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									TransTextTable mTableTransText2 = MainActivity.mTableTransText;
									sqliteDom.deleteAllRecord();
									list.clear();
									wLv.notifyDataSetChanged();
									mTableTransText2.clear();
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

								}
							}).show();
					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showActionPop(KouyiRecord kouyi, final int buttonNo) {
		final Integer recordId = kouyi.getRecordId();
		final KouyiRecord kk = kouyi;
		View view = LayoutInflater.from(this).inflate(R.layout.pop_actionmenu, null);
		initPopMenu(view);
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();
				switch (buttonNo) {
				case 1:
					sendSMS(kk.getSaid());
					break;
				case 2:
					sendSMS(kk.getTranslated());
					break;
				}
			}
		});
		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();
				switch (buttonNo) {
				case 1:
					shareText(kk.getSaid());
					break;
				case 2:
					shareText(kk.getTranslated());
					break;
				}
			}
		});
		button6.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();

			}
		});
		button5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();
				new AlertDialog.Builder(KouyiRecordActivity.this).setTitle("删除全部内容").setView(null)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								sqliteDom.deleteAllRecord();
								list.clear();
								wLv.notifyDataSetChanged();
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();

			}
		});
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();
				Collecter collecter = new Collecter();
				collecter.setChildId(6);
				collecter.setText1(kk.getSaid());
				collecter.setText2(kk.getTranslated());
				int result = sqliteDom.insertCollecterFromKouyi(collecter);
				switch (result) {
				case 0:
					// Toast.makeText(KouyiRecordActivity.this, "已加入收藏夹",
					// Toast.LENGTH_LONG).show();
					new AlertDialog.Builder(KouyiRecordActivity.this).setTitle(null).setMessage("已加入收藏夹")
							.setPositiveButton("确定", null).show();
					break;
				default:
					Toast.makeText(KouyiRecordActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
					// new
					// AlertDialog.Builder(KouyiRecordActivity.this).setTitle(null).setMessage("添加成功")
					// .setPositiveButton("确定", null).show();
				}

			}
		});
		button4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();
				sqliteDom.deleteRecordById(recordId);
				getRecordListAfterDelete();
				wLv.notifyDataSetChanged();

			}
		});
		popAction = new PopupWindow(view, LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(
				R.dimen.actionmenu_hight_2));
		// BitmapDrawable bg = (BitmapDrawable)
		// getResources().getDrawable(R.drawable.no2_popbg);
		popAction.setBackgroundDrawable(new BitmapDrawable());
		popAction.setAnimationStyle(R.style.PopupAnimation);
		popAction.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		popAction.setFocusable(true);
		popAction.setOutsideTouchable(false);
		popAction.update();
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
	protected void onDestroy() {
		unregisterReceiver(receivePopMenuEvent);
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
		if (!"".equals(strWords) && strWords != null) {
			lvWords.setVisibility(View.GONE);
			lvSimple.setVisibility(View.VISIBLE);
			page = 1;
			strWords = edtWord.getText().toString().trim();
			if (page == 1) {
				newList = sqliteDom.getSimilarResultInKouyi(strWords, page);

			} else {
				newList = sqliteDom.getSimilarResultInKouyi(strWords, page);
			}
			wslv = new KouyiSimpleLvAdapter(this, newList, new PublicArithmetic().isWhat(strWords));
			lvSimple.setAdapter(wslv);
			if (newList == null || newList.size() == 0) {
				tvNoResult.setVisibility(View.VISIBLE);
			} else {
				tvNoResult.setVisibility(View.GONE);
			}
		} else {
			imm.hideSoftInputFromWindow(edtWord.getWindowToken(), 0);
			lvSimple.setVisibility(View.GONE);
			lvWords.setVisibility(View.VISIBLE);
			page = 1;
			getRecordList();
			wLv.notifyDataSetChanged();
			if (list == null || list.size() == 0) {
				tvNoResult.setVisibility(View.VISIBLE);
			} else {
				tvNoResult.setVisibility(View.GONE);
			}
		}

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
		Map<String, Object> m10 = new HashMap<String, Object>();
		m10.put("textItem", "");
		m10.put("iconItem", R.drawable.trans_ci_pop_button_10);
		m10.put("action", 10);
		functionList.add(m10);
		Map<String, Object> m11 = new HashMap<String, Object>();
		m11.put("textItem", "");
		m11.put("iconItem", R.drawable.trans_ci_pop_button_11);
		m11.put("action", 11);
		functionList.add(m11);
		SimpleAdapter sa = new SimpleAdapter(KouyiRecordActivity.this, functionList, R.layout.gdv_interpret_item,
				new String[] { "textItem", "iconItem" }, new int[] { R.id.tvFunction, R.id.imageFunction });
		gdv.setAdapter(sa);
	}

	public void showPopMore(KouyiRecord kouyi) {
		final Integer recordId = kouyi.getRecordId();
		final KouyiRecord kk = kouyi;
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
					mIntent.putExtra("sms_body", kk.getSaid() + "\n" + kk.getTranslated());// 短信内容
					startActivity(mIntent);
					break;
				case 1:
					popMore.dismiss();
					Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
					mailIntent.setType("plain/test");

					mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
					mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, kk.getSaid() + "\n" + kk.getTranslated());
					startActivity(Intent.createChooser(mailIntent, "发送邮件"));
					break;
//				case 2:
//					popMore.dismiss();
//					showPopEditWC(kk.getSaid() + "\n" + kk.getTranslated(), Integer.parseInt(functionList.get(arg2).get("action").toString()));
////					MainActivity.iSina.sendWeibo(kk.getSaid() + "\n" + kk.getTranslated(), KouyiRecordActivity.this);
//					break;
//				case 3:
//					popMore.dismiss();
//					showPopEditWC(kk.getSaid() + "\n" + kk.getTranslated(), Integer.parseInt(functionList.get(arg2).get("action").toString()));
////					MainActivity.iTencent.sendWeibo(kk.getSaid() + "\n" + kk.getTranslated(), KouyiRecordActivity.this);
//					break;
//				case 4:
//					popMore.dismiss();
//					showPopEditWC(kk.getSaid() + "\n" + kk.getTranslated(), Integer.parseInt(functionList.get(arg2).get("action").toString()));
////					MainActivity.iRenren.sendWeibo(kk.getSaid() + "\n" + kk.getTranslated(), KouyiRecordActivity.this);
//					break;
//				case 5:
//					popMore.dismiss();
//					showPopEditWC(kk.getSaid() + "\n" + kk.getTranslated(), Integer.parseInt(functionList.get(arg2).get("action").toString()));
////					MainActivity.ikaixin.sendWeibo(kk.getSaid() + "\n" + kk.getTranslated(), KouyiRecordActivity.this);
//					break;
				case 2:
				case 3:
				case 4:
				case 5:
					popMore.dismiss();
					Intent intentW = new Intent(KouyiRecordActivity.this,EditContent.class);
					intentW.putExtra("action", Integer.parseInt(functionList.get(arg2).get("action").toString()));
					intentW.putExtra("content", kk.getSaid() + "\n" + kk.getTranslated());
					startActivityForResult(intentW, 000011);
					break;
				case 6:
					popMore.dismiss();
					ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					clipboard.setText(kk.getSaid() + "\n" + kk.getTranslated());
					break;
				case 7:
					popMore.dismiss();
					Intent intent = new Intent(KouyiRecordActivity.this, ShowWhiteBordActivity.class);
					intent.putExtra("chinese", kk.getSaid());
					intent.putExtra("english", kk.getTranslated());
					intent.putExtra("chOrEn", chOrEn);
					startActivity(intent);
					break;
				case 8:
					popMore.dismiss();
					Collecter collecter = new Collecter();
					collecter.setChildId(6);
					collecter.setText1(kk.getSaid());
					collecter.setText2(kk.getTranslated());
					int result = sqliteDom.insertCollecterFromKouyi(collecter);
					switch (result) {
					case 0:
						// Toast.makeText(KouyiRecordActivity.this, "已加入收藏夹",
						// Toast.LENGTH_LONG).show();
						new AlertDialog.Builder(KouyiRecordActivity.this).setTitle(null).setMessage("已加入收藏夹")
								.setPositiveButton("确定", null).show();
						break;
					default:
						Toast.makeText(KouyiRecordActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
						// new
						// AlertDialog.Builder(KouyiRecordActivity.this).setTitle(null).setMessage("添加成功")
						// .setPositiveButton("确定", null).show();
					}
					break;
				case 9:
					popMore.dismiss();
					// shareText();
					sendSMS(kk.getSaid() + "\n" + kk.getTranslated());
					break;
				case 10:
					popMore.dismiss();
					// deleteSelected(position);
					int index = sqliteDom.deleteRecordByIdReturnIndex(recordId);
					getRecordListAfterDelete();
					wLv.notifyDataSetChanged();
					InterpretActivity.setIsDeletefromMyLiberaryTrue();
					break;
				case 11:
					popMore.dismiss();
					// mSpeakAdapter.clear();
					// updateListView();
					new AlertDialog.Builder(KouyiRecordActivity.this).setTitle("删除全部内容").setView(null)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									TransTextTable mTableTransText2 = MainActivity.mTableTransText;
									sqliteDom.deleteAllRecord();
									list.clear();
									wLv.notifyDataSetChanged();
									mTableTransText2.clear();
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			edtWord.setText(strWords);
		}
		return false;
	}
	
	@Override
	protected void onStop() {
		if(TextPlayer.getInstance().isPlaying()){
			TextPlayer.getInstance().stop();
		}
		super.onStop();
	}
	
	
////分享内容编辑框
//	private PopupWindow popEdit;
//	private EditText edtContent;
//	private Button btnSend2;
//	private Button btnCancle2;
//	private TextView tvCount;
//	private int count = 140;
//	public void initPopEditWC(View view){
//		edtContent = (EditText) view.findViewById(R.id.edtContent);
//		btnSend2 = (Button) view.findViewById(R.id.btnSend);
//		btnCancle2 = (Button) view.findViewById(R.id.btnCancle);
//		tvCount = (TextView) view.findViewById(R.id.tvCount);
//		edtContent.setMaxEms(count);
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
//				
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				tvCount.setText((count-edtContent.getText().toString().length())+"/"+count);
//				
//			}
//		});
//		btnSend2.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				popEdit.dismiss();
//				switch(a){
//				case 2:
//					MainActivity.iSina.sendWeibo(edtContent.getText().toString().trim(),
//							KouyiRecordActivity.this);
//					break;
//				case 3:
//					MainActivity.iTencent.sendWeibo(edtContent.getText().toString().trim(),
//							KouyiRecordActivity.this);
//					break;
//				case 4:
//					MainActivity.iRenren.sendWeibo(edtContent.getText().toString().trim(),
//							KouyiRecordActivity.this);
//					break;
//				case 5:
//					MainActivity.ikaixin.sendWeibo(edtContent.getText().toString().trim(),
//							KouyiRecordActivity.this);
//					break;
//				}
//				
//			}
//		});
//		btnCancle2.setOnClickListener(new View.OnClickListener() {
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
						KouyiRecordActivity.this);
				break;
			case 3:
				MainActivity.iTencent.sendWeibo(str,
						KouyiRecordActivity.this);
				break;
			case 4:
				MainActivity.iRenren.sendWeibo(str,
						KouyiRecordActivity.this);
				break;
			case 5:
				MainActivity.ikaixin.sendWeibo(str,
						KouyiRecordActivity.this);
				break;
			case 0:
				Util.showToast(this, "发送错误");
				break;
			}
		}
	}
}
