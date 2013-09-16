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
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.adapter.WordsLvAdapter;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Words;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.qa.MainActivity;
import com.ztspeech.simutalk2.weibo.IRenren;
import com.ztspeech.simutalk2.weibo.ISina;
import com.ztspeech.simutalk2.weibo.ITencent;
import com.ztspeech.simutalk2.weibo.Ikaixin;

public class WordsActivity extends BaseActivity implements OnTouchListener, OnItemClickListener {

	private EditText edtWord;
	private Intent fromWhere;
	private List list = null;
	private String strWords;
	private ListView lvWords;
	private Integer childId;
	private Integer categroyId;
	private int page = 1;
	private SQLiteDom sqliteDom = null;
	private WordsLvAdapter wLv;
	private View parent;
	private Integer chOrEn = 0;

	private TextView tvNoResult;

	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	private Button button6;
	private PopupWindow popAction;

	private PopupWindow popMore;
	private GridView gdv;

	// private static int isclose= 0;
	// private static String lastWords = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sqliteDom = new SQLiteDom();

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

		registerBoradcastReceiver();
		parent = getLayoutInflater().inflate(R.layout.activity_words, null);
		fromWhere = getIntent();
		strWords = "";
		childId = fromWhere.getIntExtra("childId", 0);
		categroyId = fromWhere.getIntExtra("categroyId", 0);
		if (childId == 0 && categroyId == 0) {
			childId = null;
			categroyId = null;
		} else if (childId != 0 && categroyId == 0) {
			categroyId = null;
		} else if (childId == 0 && categroyId != 0) {
			childId = null;
		} else if (childId != 0 && categroyId != 0) {
		}
		list = fromWhere.getParcelableArrayListExtra("wordsList");
		strWords = fromWhere.getStringExtra("words");
		setTitleAndContentView(R.layout.activity_search, fromWhere.getStringExtra("title"));
		edtWord = (EditText) findViewById(R.id.edtInputWords);
		edtWord.setText(strWords);
		edtWord.setInputType(InputType.TYPE_NULL);
		edtWord.setOnTouchListener(this);
		lvWords = (ListView) findViewById(R.id.lvCategroy);
		setChOrEN(strWords);
		if (list == null) {
			list = searchWordsBystrWords(strWords);
		}
		wLv = new WordsLvAdapter(this, list, childId);

		wLv.setCHorEN(chOrEn);
		wLv.addFooter(lvWords);
		lvWords.setAdapter(wLv);
		lvWords.setOnItemClickListener(this);

		tvNoResult = (TextView) findViewById(R.id.tvNoResult);
		tvNoResult.setText(getResources().getString(R.string.dictionary_noresult));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Intent intent = new Intent(WordsActivity.this, WordsAutoCompletedActivity.class);
			intent.putExtra("from", "no");
			intent.putExtra("childId", childId);
			intent.putExtra("strWords", strWords);
			startActivity(intent);
			// overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		}
		return false;
	}

	public void setChOrEN(String str) {
		chOrEn = new PublicArithmetic().isWhat(str);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == resultCode && resultCode == Util.REQUESTCODE_AUTOCOMPLETEDWORDS) {
			page = 1;
			list = data.getParcelableArrayListExtra("wordsList");
			strWords = data.getStringExtra("words");
			setChOrEN(strWords);
			if (list == null) {
				list = searchWordsBystrWords(strWords);
			}
			edtWord.setText(strWords);
			wLv.removeFooter(lvWords);
			wLv = new WordsLvAdapter(this, list, childId);
			wLv.setCHorEN(chOrEn);
			wLv.addFooter(lvWords);
			lvWords.setAdapter(wLv);
			setTitle("搜索结果");
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (list == null || list.size() == 0) {
			tvNoResult.setVisibility(View.VISIBLE);
		} else {
			tvNoResult.setVisibility(View.GONE);
		}

		// if (this.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE) {
		// Intent intent = new
		// Intent(WordsActivity.this,ShowWhiteBordActivity.class);
		// intent.putExtra("english",
		// ((Words)list.get((int)lvWords.getSelectedItemId())).getEnglish());
		// intent.putExtra("chinese",
		// ((Words)list.get((int)lvWords.getSelectedItemId())).getChinese());
		// startActivity(intent);
		// Log.e(">>>>>>>>>>>>>>><<<<<<<<", "jalksdjfljasldjkf");
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//
		// }
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Log.e(">>>>>>>>>>>>>>><<<<<<<<", "jalksdjfljasldjkf");
		// if (this.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE) {
		// Intent intent = new
		// Intent(WordsActivity.this,ShowWhiteBordActivity.class);
		// intent.putExtra("english",
		// ((Words)list.get(lvWords.getId())).getEnglish());
		// intent.putExtra("chinese",
		// ((Words)list.get(lvWords.getId())).getChinese());
		// startActivity(intent);
		// }
	}

	public List searchWordsBystrWords(String strWords) {
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

		list.addAll(newList);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 == list.size()) {
			int count1 = list.size();
			addLvItemFenYe();
			int count2 = list.size();
			if (count1 == count2) {
				wLv.removeFooter(lvWords);
			}
			wLv.notifyDataSetChanged();
		} else {
			// Intent intent = new
			// Intent(WordsActivity.this,ShowWhiteBordActivity.class);
			// intent.putExtra("english", ((Words)list.get(arg2)).getEnglish());
			// intent.putExtra("chinese", ((Words)list.get(arg2)).getChinese());
			// intent.putExtra("chOrEn", chOrEn);
			// startActivity(intent);
		}

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

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(Util.ACTION_POMENU);
		myIntentFilter.addAction(Util.ACTION_SENDMSG);
		registerReceiver(receivePopMenuEvent, myIntentFilter);
	}

	public BroadcastReceiver receivePopMenuEvent = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Util.ACTION_POMENU)) {
				// showPopMenu(intent.getParcelableExtra("word"),intent.getIntExtra("button",
				// 0));
				// showDialogMenu(intent.getParcelableExtra("word"),
				// intent.getIntExtra("button", 0));

				showPopMore(0);
			} else if (action.equals(Util.ACTION_SENDMSG)) {
				strWords = intent.getStringExtra("words");
				categroyId = intent.getIntExtra("categroyId", categroyId);
				String title = intent.getStringExtra("title");
				if (title != null && !"".equals(title)) {
					setTitle(title);
				}
				page = 1;
				list = intent.getParcelableArrayListExtra("wordsList");
				setChOrEN(strWords);
				if (list == null) {
					list = searchWordsBystrWords(strWords);
				}
				edtWord.setText(strWords);
				wLv.removeFooter(lvWords);
				wLv = new WordsLvAdapter(WordsActivity.this, list, childId);
				wLv.setCHorEN(chOrEn);
				wLv.addFooter(lvWords);
				lvWords.setAdapter(wLv);
			}

		}

	};

	public void initPopMenu(View view) {
		button1 = (Button) view.findViewById(R.id.button1);
		button1.setText("分          享");
		button2 = (Button) view.findViewById(R.id.button2);
		button2.setText("复             制");
		button3 = (Button) view.findViewById(R.id.button3);
		button3.setText("添加到收藏");
		button4 = (Button) view.findViewById(R.id.button4);
		button4.setVisibility(View.GONE);
		button5 = (Button) view.findViewById(R.id.button5);
		button5.setVisibility(View.GONE);
		button6 = (Button) view.findViewById(R.id.button6);
		button6.setText("取             消");
	}

	public void showDialogMenu(Parcelable word, final int buttonNo) {
		final Words words = (Words) word;
		AlertDialog.Builder builder = new AlertDialog.Builder(WordsActivity.this);
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
					Intent intent = new Intent(WordsActivity.this, ShowWhiteBordActivity.class);
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
						// Toast.makeText(WordsActivity.this, "已加入收藏夹",
						// Toast.LENGTH_LONG).show();
						new AlertDialog.Builder(WordsActivity.this).setTitle(null).setMessage("已加入收藏夹")
								.setPositiveButton("确定", null).show();
						break;
					default:
						Toast.makeText(WordsActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
						// new
						// AlertDialog.Builder(WordsActivity.this).setTitle(null).setMessage("添加成功")
						// .setPositiveButton("确定", null).show();
					}
					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showPopMenu(Parcelable word, final int buttonNo) {
		final Words words = (Words) word;
		View view = LayoutInflater.from(this).inflate(R.layout.pop_actionmenu, null);
		initPopMenu(view);
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();
				if (chOrEn == 1) {
					switch (buttonNo) {
					case 1:
						sendSMS(words.getChinese());
						break;
					case 2:
						sendSMS(words.getEnglish());
						break;
					}
				} else {
					switch (buttonNo) {
					case 1:
						sendSMS(words.getEnglish());
						break;
					case 2:
						sendSMS(words.getChinese());
						break;
					}
				}

			}
		});
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();
				if (chOrEn == 1) {
					switch (buttonNo) {
					case 1:
						shareText(words.getChinese());
						break;
					case 2:
						shareText(words.getEnglish());
						break;
					}
				} else {
					switch (buttonNo) {
					case 1:
						shareText(words.getEnglish());
						break;
					case 2:
						shareText(words.getChinese());
						break;
					}
				}
			}
		});
		button6.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();

			}
		});
		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popAction.dismiss();
				int result = sqliteDom.insertCollecterFromSearch(words);
				switch (result) {
				case 0:
					// Toast.makeText(WordsActivity.this, "已加入收藏夹",
					// Toast.LENGTH_LONG).show();
					new AlertDialog.Builder(WordsActivity.this).setTitle(null).setMessage("已加入收藏夹")
							.setPositiveButton("确定", null).show();
					break;
				default:
					Toast.makeText(WordsActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
					// new
					// AlertDialog.Builder(WordsActivity.this).setTitle(null).setMessage("添加成功")
					// .setPositiveButton("确定", null).show();
				}

			}
		});
		popAction = new PopupWindow(view, LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(
				R.dimen.actionmenu_hight_3));
		// BitmapDrawable bg = (BitmapDrawable)
		// getResources().getDrawable(R.drawable.no2_popbg);
		popAction.setBackgroundDrawable(new BitmapDrawable());
		popAction.setAnimationStyle(R.style.PopupAnimation);
		popAction.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		popAction.setFocusable(true);
		popAction.setOutsideTouchable(false);
		popAction.update();
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

		SimpleAdapter sa = new SimpleAdapter(WordsActivity.this, functionList, R.layout.gdv_interpret_item,
				new String[] { "textItem", "iconItem" }, new int[] { R.id.tvFunction, R.id.imageFunction });
		gdv.setAdapter(sa);
	}

	public void showPopMore(final int position) {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_interpret_more, null);
		initPopMore(view);
		gdv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch (Integer.parseInt(functionList.get(arg2).get("action").toString())) {
				case 0:
					popMore.dismiss();
					// Uri smsToUri = Uri.parse("smsto:");// 联系人地址
					// Intent mIntent = new
					// Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
					// mIntent.putExtra("sms_body", mSelectedItemData.speak +
					// "\n" + mSelectedItemData.trans);// 短信内容
					// startActivity(mIntent);
					break;
				case 1:
					popMore.dismiss();
					// Intent mailIntent = new
					// Intent(android.content.Intent.ACTION_SEND);
					// mailIntent.setType("plain/test");
					//
					// mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					// "");
					// mailIntent.putExtra(android.content.Intent.EXTRA_CC, "");
					// mailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					// "");
					// mailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					// mSelectedItemData.speak + "\n"
					// + mSelectedItemData.trans);
					// startActivity(Intent.createChooser(mailIntent, "发送邮件"));
					break;
				case 2:
					popMore.dismiss();
					// MainActivity.iSina.sendWeibo(mSelectedItemData.speak +
					// "\n" + mSelectedItemData.trans,
					// WordsActivity.this);
					break;
				case 3:
					popMore.dismiss();
					// MainActivity.iTencent.sendWeibo(mSelectedItemData.speak +
					// "\n" + mSelectedItemData.trans,
					// WordsActivity.this);
					break;
				case 4:
					popMore.dismiss();
					// MainActivity.iRenren.sendWeibo(mSelectedItemData.speak +
					// "\n" + mSelectedItemData.trans,
					// WordsActivity.this);
					break;
				case 5:
					popMore.dismiss();

					break;
				case 6:
					popMore.dismiss();
					// fullScreenTransData();

					break;
				case 7:
					popMore.dismiss();
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
					// Toast.makeText(InterpretActivity.this, "添加成功",
					// Toast.LENGTH_SHORT).show();
					// }
					break;
				case 8:
					popMore.dismiss();
					// shareText();
					break;
				case 9:
					popMore.dismiss();
					// deleteSelected(position);
					break;
				case 10:
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
}
