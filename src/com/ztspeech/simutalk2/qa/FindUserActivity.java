package com.ztspeech.simutalk2.qa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.Json;
import cn.ac.ia.directtrans.json.JsonData;
import cn.ac.ia.directtrans.json.JsonEditLinkman;
import cn.ac.ia.directtrans.json.JsonFindLinkman;
import cn.ac.ia.directtrans.json.JsonFindNearby;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.JsonSendMessage;
import cn.ac.ia.directtrans.json.UserInfo;
import cn.ac.ia.directtrans.json.UserState;

import com.baidu.location.LocationClient;
import com.ztspeech.recognizer.OnEngineListener;
import com.ztspeech.recognizerDialog.UnisayRecognizerDialog;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.FriendData;
import com.ztspeech.simutalk2.data.FriendDataList;
import com.ztspeech.simutalk2.dictionary.activity.ResultActivity;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.ListViewImageEngine;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.qa.message.ProcessMessage;
import com.ztspeech.simutalk2.qa.view.AdapterItemView;
import com.ztspeech.simutalk2.qa.view.DataListAdapter;
import com.ztspeech.simutalk2.qa.view.FindUserItemView;
import com.ztspeech.simutalk2.qa.view.InterpretView;

/**
 * 查找、添加好友
 * 
 * 
 */
public class FindUserActivity extends Activity implements OnClickListener, OnEngineListener {

	// view control
	private ListView mListView;
	// private EditText mEditFind;
	private Button mBtnReturn;
	private Button mBtnFind;
	private EditText edtName;
	private TextView tvNoResult;
	private Button btnFindNearby;
	private UnisayRecognizerDialog mDialog = null;
	private Button btnLuyin = null;

	// private FriendDataList mFriends = FriendDataList.getInstance();

	private com.ztspeech.simutalk2.data.UserInfo mUser = com.ztspeech.simutalk2.data.UserInfo.getInstanse();
	private JsonSendMessage mJsonMessage = new JsonSendMessage();
	private JsonFindLinkman mFindUsers = new JsonFindLinkman();
	// private MsgDataList mViewList = new MsgDataList(0);
	private FriendDataList searchUsersList = new FriendDataList();
	private FriendDataList mFriends = FriendDataList.getInstance();
	private JsonEditLinkman mJsonAddFriend = new JsonEditLinkman();

	// private PostPackage mPostPackage;
	private ListViewImageEngine listViewImageLoaderEngine;
	private PostPackageEngine mPostPackageEngine;
	private Context context;
	private int postPackageType = 0;
	private int isAddingFriendId;
	// =========================================================
	// private PopupWindow mRecognizerWindow = null;
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
			case 200:
				postPackageCallBack((ResultPackage) msg.obj, postPackageType);
				break;
			case 404:
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			case Location.GET_LOCATION:
				getLocation(msg);
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

	
	private Vibrator mVibrator01 =null;
	private LocationClient mLocClient;
	public void initLocationSDK(){
		mLocClient = ((Location)getApplication()).mLocationClient;
		((Location)getApplication()).setHandler(handler);
		mVibrator01 =(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		((Location)getApplication()).mVibrator01 = mVibrator01;
	}
	
	// =========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.slideinright, R.anim.slideoutleft);

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find_user);
		registerBoradcastReceiver();
		context = this;
		// getWindow().setFormat(PixelFormat.RGBA_8888);

		mListView = (ListView) findViewById(R.id.lvQuestion);
		mBtnReturn = (Button) findViewById(R.id.btnReturn);
		mBtnFind = (Button) findViewById(R.id.btnFind);
		edtName = (EditText) findViewById(R.id.edtName);
		tvNoResult = (TextView) findViewById(R.id.tvNoResult);
		btnFindNearby = (Button) findViewById(R.id.btnFindNearby);
		btnFindNearby.setOnClickListener(this);
		mBtnReturn.setOnClickListener(this);
		mBtnFind.setOnClickListener(this);
		mBtnReturn.setText("");
		tvNoResult.setText(getResources().getString(R.string.dictionary_noresult));
		btnLuyin = (Button) findViewById(R.id.btnLuyin);
		btnLuyin.setOnClickListener(this);
		mInterpretView = new InterpretView(this, handler, this);
		mDialog = new UnisayRecognizerDialog(this, "", this, mInterpretView.mNewRecognizerViewListenerInterface);

		initLocationSDK();
		initListView();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.slideinleft, R.anim.slideoutright);
	}

	private void postPackageCallBack(ResultPackage result, int postPackageType) {
		if (result.isNetSucceed()) {
			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (postPackageType == 0) {
				if (ret != null) {
					if (ret.succeed == true) {
						// 查找用户结果
						searchUsersList.clear();
						int nSize = ret.items.size();
						if (nSize > 0) {

							for (int i = 0; i < nSize; i++) {
								JsonData data = ret.items.get(i);
								UserInfo user = Json.fromJson(data.json, UserInfo.class);
								UserState us = new UserState();
								us.id = user.id;
								us.name = user.name;
								us.photo = user.photo;
								FriendData msg = new FriendData(us);
								if (mFriends.findById(us.id) == null&&us.id!=mUser.state.id) {
									searchUsersList.add(msg);
								}
							}
							updateListView();
							// 提示是否有结果
							if (searchUsersList.size() > 0) {
								tvNoResult.setVisibility(View.GONE);
							} else {
								tvNoResult.setVisibility(View.VISIBLE);
							}
						} else {
							tvNoResult.setVisibility(View.VISIBLE);
						}

					} else {
						new AlertDialog.Builder(FindUserActivity.this).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", null).show();
					}
				}
			}else if (postPackageType == 10) {
				mPostPackageEngine.dismissLoading();
				if (ret != null) {
					if (ret.succeed == true) {
						// 查找附近的人
						searchUsersList.clear();
						int nSize = ret.items.size();
						if (nSize > 0) {

							for (int i = 0; i < nSize; i++) {
								JsonData data = ret.items.get(i);
								UserInfo user = Json.fromJson(data.json, UserInfo.class);
								UserState us = new UserState();
								us.id = user.id;
								us.name = user.name;
								us.photo = user.photo;
								us.distance = user.distance;
								FriendData msg = new FriendData(us);
								if (mFriends.findById(us.id) == null&&us.id!=mUser.state.id) {
									searchUsersList.add(msg);
								}
							}
							updateListView();
							// 提示是否有结果
							if (searchUsersList.size() > 0) {
								tvNoResult.setVisibility(View.GONE);
							} else {
								tvNoResult.setVisibility(View.VISIBLE);
							}
						} else {
							tvNoResult.setVisibility(View.VISIBLE);
						}

					} else {
						new AlertDialog.Builder(FindUserActivity.this).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", null).show();
					}
				}
			} else if (postPackageType == 1 || postPackageType == 2) {
				if (ret != null) {
					if (ret.succeed == true) {
						ProcessMessage pro = ProcessMessage.getInstance();
						pro.process(ret, false);
						searchUsersList.deleteByUserId(isAddingFriendId);
						updateListView();
						Toast.makeText(context, "添加好友成功", Toast.LENGTH_LONG).show();

					} else {
						new AlertDialog.Builder(FindUserActivity.this).setTitle("提示").setMessage(ret.explain)
								.setPositiveButton("确定", null).show();
					}
				}

			}
		}
	}

	// public IHttpPostListener mFindUsersListener = new IHttpPostListener() {
	//
	// @Override
	// public void onNetPostResult(PostPackage owner, ResultPackage result) {
	//
	// if (result.isNetSucceed()) {
	// JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
	// if (ret != null) {
	// if (ret.succeed == true) {
	//
	// finish();
	// } else {
	// new
	// AlertDialog.Builder(FindUserActivity.this).setTitle("提示").setMessage(ret.explain)
	// .setPositiveButton("确定", null).show();
	// }
	// }
	// }
	//
	// WaitingActivity.stop();
	// }
	// };

	private DataListAdapter mListViewAdapter = new DataListAdapter(this, searchUsersList) {
		@Override
		public AdapterItemView getAdapterItemView(Context context) {

			return new FindUserItemView(context, listViewImageLoaderEngine);
		}
	};

	private void initListView() {

		listViewImageLoaderEngine = new ListViewImageEngine(mListView);
		mListView.setAdapter(mListViewAdapter);
		mListView.setDividerHeight(0);
	}

	public void onClick(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (v == mBtnReturn) {
			finish();
		} else if (v == mBtnFind) {
			imm.hideSoftInputFromWindow(edtName.getWindowToken(), 0);
			String username = edtName.getText().toString().trim();
			if (username.length() == 0) {
				Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
				return;
			}
			findUser(username);
		} else if (v == btnLuyin) {
			imm.hideSoftInputFromWindow(edtName.getWindowToken(), 0);
			if (mUser.s2sType.equals(com.ztspeech.simutalk2.data.UserInfo.S2T_CH2EN)) {
				mDialog.setToChineseEngine();
			} else if (mUser.s2sType.equals(com.ztspeech.simutalk2.data.UserInfo.S2T_EN2CH)) {
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
		} else if(v.getId() == R.id.btnFindNearby){
			if(!mLocClient.isStarted()){
				mLocClient.start();
				mPostPackageEngine = new PostPackageEngine(context, null, handler);
				mPostPackageEngine.showLoading();
			}
		}
	}

	/**
	 * 查找用户结果
	 */
	private void findUser(String username) {
		mFindUsers.text = username;
		postPackageType = 0;
		mPostPackageEngine = new PostPackageEngine(context, mFindUsers, handler);
		mPostPackageEngine.post();
	}

	/**
	 * 查找附近好友
	 */
	private void findNearby(String latitude,String longitude) {
		JsonFindNearby mFindNearby = new JsonFindNearby();
		mFindNearby.longitude = longitude;
		mFindNearby.latitude = latitude;
		mFindNearby.text = "";
		postPackageType = 10;
		mPostPackageEngine.setJson(mFindNearby);
		mPostPackageEngine.post(false);
	}
	
	private void updateListView() {

		mListViewAdapter.notifyDataSetChanged();

	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("com.action.addfriend");
		registerReceiver(receiveAddFriendEvent, myIntentFilter);
	}

	public BroadcastReceiver receiveAddFriendEvent = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("com.action.addfriend")) {
				addFriend(intent.getIntExtra("id", 0), intent.getStringExtra("name"));
			}

		}

	};

	private void addFriend(int id, String name) {

		System.out.println(id + ">>>>>>>>>" + name);
		if (id != 0 && name != null && name.length() > 0) {
			mJsonAddFriend.setInviteLinkman(id);
			isAddingFriendId = id;
			mJsonAddFriend.name = name;

			postPackageType = 1;
			mPostPackageEngine = new PostPackageEngine(context, mJsonAddFriend, handler);
			mPostPackageEngine.post();
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiveAddFriendEvent);
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
			edtName.setText(text);
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
	/**
	 * 获取当前位置坐标
	 * @param msg
	 */
	public void getLocation(Message msg){
		mLocClient.stop();
		HashMap mlocation = (HashMap)msg.obj;
		
		findNearby(mlocation.get("latitude").toString(), mlocation.get("longitude").toString());
//		new AlertDialog.Builder(FindUserActivity.this).setTitle(null).setMessage("latitude"+mlocation.get("latitude").toString()+"\n longitude"+mlocation.get("longitude").toString())
//		.setPositiveButton("确定", null).show();
	}
}
