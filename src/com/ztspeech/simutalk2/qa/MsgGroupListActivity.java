package com.ztspeech.simutalk2.qa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import cn.ac.ia.directtrans.json.JsonEditLinkman;
import cn.ac.ia.directtrans.json.JsonFunction;
import cn.ac.ia.directtrans.json.JsonQuestion;
import cn.ac.ia.directtrans.json.JsonRequest;
import cn.ac.ia.directtrans.json.JsonRequestResult;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.FriendDataList;
import com.ztspeech.simutalk2.data.MsgDataList;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.net.ListViewImageEngine;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.qa.message.ProcessMessage;
import com.ztspeech.simutalk2.qa.view.AdapterItemView;
import com.ztspeech.simutalk2.qa.view.DataListAdapter;
import com.ztspeech.simutalk2.qa.view.MsgGroupItemView;

public class MsgGroupListActivity extends UpdateBaseActivity {

	@Override
	protected void onResume() {
		
		super.onResume();
		updateDataList(true);
		isShow = true;
	}

	private static final int ACTIVITY_MESSAGE = 100;
	public static MsgGroupListActivity instance = null;
	public static boolean isViewUpdate = true;
	// view control
	private ListViewImageEngine listViewImageLoaderEngine;
	private ListView mListView;
	private ViewFlipper mViewFlipper;
	private ImageButton mBtnRadio1;
	private ImageButton mBtnRadio2;

	// 弹出菜单
	private PopupWindow mPopMenu;
//	private Button mBtnOpen;
	private Button mBtnDelete;
	private Button mBtnAddFriend;
//	private TextView mTvLine1;
	private TextView mTvLine2;
	
	// data
	private FriendDataList mFriends = FriendDataList.getInstance();
	private MsgGroupList mMsgGroupList = MsgGroupList.getInstance();
	private MsgGroupList mList = new MsgGroupList();
	private MsgDataList mSelectMsgData = null;
	private JsonEditLinkman mJsonAddFriend = new JsonEditLinkman();
	private JsonQuestion mJsonQuestion = new JsonQuestion();
	// private PostPackage mPostPackage;
	private boolean mIsAddFriend = false;
	private float startX, startY;
	private static boolean isShow = false;
	private Context context;
	private PostPackageEngine mPostPackageEngine;
	private int postPackageType = 0;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
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



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_msg_group_list);
		// getWindow().setFormat(PixelFormat.RGBA_8888);
		context = this.getParent();
		mListView = (ListView) findViewById(R.id.lvMsgGroupList);
		mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);
		mBtnRadio1 = (ImageButton) findViewById(R.id.radio0);
		mBtnRadio2 = (ImageButton) findViewById(R.id.radio1);

		mBtnRadio1.setOnClickListener(mCheckedListener);
		mBtnRadio2.setOnClickListener(mCheckedListener);
		mListView.setOnTouchListener(mListViewTouch);
		isViewUpdate = true;
		instance = this;
		initListView();
	}
	
	private View mPopMenuView;
	
	private View getPopMenuView(){
		
		if(mPopMenuView == null){
			
			mPopMenuView = LayoutInflater.from(this).inflate(R.layout.pop_qa_msg, null);
			mBtnDelete = (Button) mPopMenuView.findViewById(R.id.btnDel);
			mBtnAddFriend = (Button) mPopMenuView.findViewById(R.id.btnAdd);
			mTvLine2 = (TextView) mPopMenuView.findViewById(R.id.tvLine2);
			
			mPopMenu = new PopupWindow(mPopMenuView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mPopMenu.setBackgroundDrawable(new BitmapDrawable());
	
			mBtnDelete.setOnClickListener(new View.OnClickListener() {
		
				@Override
				public void onClick(View v) {
					mPopMenu.dismiss();
					deleteSelectData();	
				}
			});
		
			mBtnAddFriend.setOnClickListener(new View.OnClickListener() {
		
				@Override
				public void onClick(View v) {
					mPopMenu.dismiss();
					addToFriend();	
				}
			});
		}

		
		mIsAddFriend = false;

		MsgInfoData msg = mSelectMsgData.getLinkman(UserInfo.state.id);
		if (msg != null) {
			if (null == mFriends.findById(msg.senderId)) {
				mIsAddFriend = true;
			}
		}

		if (mIsAddFriend) {
			mBtnAddFriend.setVisibility(View.VISIBLE);
			mTvLine2.setVisibility(View.VISIBLE);
		} else {
			mBtnAddFriend.setVisibility(View.GONE);
			mTvLine2.setVisibility(View.GONE);
		}
		
		return mPopMenuView;
	}
	public void showPopMenu(View parentView) {
		closePM();

		getPopMenuView();
		
		// pop 位置优化
		int yoff = -15;
		if((parentView.getBottom()+53) > mListView.getHeight() ) {
			yoff =   mListView.getHeight()  -  parentView.getBottom() - 53;
		}		
		mPopMenu.showAsDropDown(parentView, 300, yoff);
		
		mPopMenu.setFocusable(true);
		mPopMenu.setOutsideTouchable(true);		
		mPopMenu.update();
	}
	
	
	public void closePM() {
		if (mPopMenu != null) {
			mPopMenu.dismiss();
		}
	}
	
	private void postPackageCallBack(ResultPackage result, int postPackageType) {
		if (result.isNetSucceed()) {
			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (postPackageType == 0) {
				if (ret != null) {
					if (ret.succeed == true) {
						Toast.makeText(MsgGroupListActivity.this, getString(R.string.qa_add_friend_ok),
								Toast.LENGTH_SHORT).show();
						ProcessMessage pro = ProcessMessage.getInstance();
						pro.process(ret, false);
					} else {
						new AlertDialog.Builder(MsgGroupListActivity.this.getParent()).setTitle("提示")
								.setMessage(ret.explain).setPositiveButton("确定", null).show();
					}
				}
			} else if (postPackageType == 1) {
				if (ret != null) {
					if (ret.succeed == true) {
						mMsgGroupList.delete(mSelectMsgData.id);
						updateDataList(true);
					} else {
						new AlertDialog.Builder(MsgGroupListActivity.this.getParent()).setTitle("提示")
								.setMessage(ret.explain).setPositiveButton("确定", null).show();
					}
				}
			} else if (postPackageType == 2) {
				if (ret != null) {
					if (ret.succeed == true) {
						mMsgGroupList.clearMsg();
						ProcessMessage pro = ProcessMessage.getInstance();
						pro.process(ret, false);
					} else {
						new AlertDialog.Builder(MsgGroupListActivity.this.getParent()).setTitle("提示")
								.setMessage(ret.explain).setPositiveButton("确定", null).show();
					}
				}
			}
		}
	}

	public static boolean isShowNotifitionTip(MsgDataList msgList) {
		if (msgList.type == MsgInfoData.Define.TYPE_QA) {
			if (isShow) {
				return true;
			}
			// return true;
		}

		return true;
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		isShow = false;
	}

	private OnClickListener mCheckedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (mBtnRadio1 == v) {
				mViewFlipper.setInAnimation(MsgGroupListActivity.this, R.anim.in_lefttoright);
				mViewFlipper.setOutAnimation(MsgGroupListActivity.this, R.anim.out_lefttoright);
				mViewFlipper.showNext();
				showAsk();
			} else if (mBtnRadio2 == v) {
				mViewFlipper.setInAnimation(MsgGroupListActivity.this, R.anim.in_righttoleft);
				mViewFlipper.setOutAnimation(MsgGroupListActivity.this, R.anim.out_righttoleft);
				mViewFlipper.showPrevious();
				showSolve();
			}
		}
	};

	private DataListAdapter mListViewAdapter = new DataListAdapter(this) {
		@Override
		public AdapterItemView getAdapterItemView(Context context) {
			
			return new MsgGroupItemView(context, listViewImageLoaderEngine);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		updateDataList(true);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateDataList(boolean update) {

		if (isViewUpdate || update) {
			isViewUpdate = false;

			if (mBtnRadio2.isEnabled()) {
				showAsk();
			} else {
				showSolve();
			}
		}
	}

	private OnTouchListener mListViewTouch = new OnTouchListener() {

		public boolean onTouch(View arg0, MotionEvent event) {

			
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

				if ((offsetX) <= offsetY) {
					return false;
				}
				offsetX = event.getX() - startX;
				if (offsetX > 50) {
					if (mBtnRadio1.isEnabled()) {
						showAsk();
					} else {
						return false;
					}
					
					mViewFlipper.setInAnimation(MsgGroupListActivity.this, R.anim.in_lefttoright);
					mViewFlipper.setOutAnimation(MsgGroupListActivity.this, R.anim.out_lefttoright);
					mViewFlipper.showNext();
				} else if (offsetX < -50) {

					if (mBtnRadio1.isEnabled()) {
						return false;
					} else {
						showSolve();
					}
					
					mViewFlipper.setInAnimation(MsgGroupListActivity.this, R.anim.in_righttoleft);
					mViewFlipper.setOutAnimation(MsgGroupListActivity.this, R.anim.out_righttoleft);
					mViewFlipper.showPrevious();
					
					
				} else {
					return false;
				}

				closePM();
			}
			return false;
		}
	};

	private void showAsk() {
		mBtnRadio1.setEnabled(false);
		mBtnRadio2.setEnabled(true);

		int nCount = mMsgGroupList.size();
		mList.clear();
		for (int i = 0; i < nCount; i++) {
			MsgDataList data = (MsgDataList) mMsgGroupList.get(i);
			if (data.type == MsgInfoData.Define.TYPE_QA) {
				if (data.getOwnerId() == UserInfo.state.id) {
					mList.add(data);
				}
			}
		}
		sortMsgByTime(mList);
		mListViewAdapter.notifyDataSetChanged(); // .notifyDataSetChanged();
	}

	private void sortMsgByTime(MsgGroupList list) {

		MsgDataList temp;
		MsgDataList max = null;

		int nSize = list.size();
		for (int i = 0; i < nSize; i++) {
			temp = (MsgDataList) list.get(i);
			max = temp;
			int nMax = i;
			for (int j = i + 1; j < nSize; j++) {
				MsgDataList data = (MsgDataList) list.get(j);
				if (data.time.getTime() > max.time.getTime()) {
					max = data;
					nMax = j;
				}
			}
			if (nMax != i) {
				list.set(i, max);
				list.set(nMax, temp);
			}
		}
	}

	private void showSolve() {

		mBtnRadio2.setEnabled(false);
		mBtnRadio1.setEnabled(true);

		int nCount = mMsgGroupList.size();
		mList.clear();
		for (int i = 0; i < nCount; i++) {
			MsgDataList data = (MsgDataList) mMsgGroupList.get(i);
			if (data.type == MsgInfoData.Define.TYPE_QA) {
				if (data.getOwnerId() != UserInfo.state.id) {
					mList.add(data);
				}
			}
		}
		sortMsgByTime(mList);
		mListViewAdapter.notifyDataSetChanged();
	}

	private void initListView() {
		
		updateDataList(true);
		mListViewAdapter.setDataList(mList);
		mListView.setDividerHeight(0);
		listViewImageLoaderEngine = new ListViewImageEngine(mListView);
		mListView.setOnItemClickListener(mAdapterLinstener);
		mListView.setOnItemLongClickListener(mAdapterLongLinstener);
		mListView.setAdapter(mListViewAdapter);
	}

	private OnItemClickListener mAdapterLinstener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			MsgGroupItemView v = (MsgGroupItemView) arg1;
			mSelectMsgData = (MsgDataList) v.getData();
			openItem();
			
		}
	};

	private void openItem() {

		showMessage();
	}

	private void showMessage() {
		Intent intent = new Intent(this, MessageActivity.class);
		intent.putExtra(MsgGroupList.PARAM_ID, mSelectMsgData.id);
		intent.putExtra(MsgGroupList.PARAM_TYPE, MsgInfoData.Define.TYPE_QA);
		startActivityForResult(intent, ACTIVITY_MESSAGE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(0, 1, 1, "清空历史记录");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			clearAllData();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnItemLongClickListener mAdapterLongLinstener = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			MsgGroupItemView v = (MsgGroupItemView) arg1;
			mSelectMsgData = (MsgDataList) v.getData();
			showPopMenu(arg1);
			return true;
		}
	};

	private void selectDo() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this.getParent());
		builder.setTitle("选择");
		CharSequence[] items = null;
		mIsAddFriend = false;

		MsgInfoData msg = mSelectMsgData.getLinkman(UserInfo.state.id);
		if (msg != null) {
			if (null == mFriends.findById(msg.senderId)) {
				mIsAddFriend = true;
			}
		}

		if (mIsAddFriend) {
			items = new CharSequence[3];
			items[0] = "打开";
			items[1] = "添加为好友";
			items[2] = "删除";
			// items[3] = "清空全部历史记录";
		} else {
			items = new CharSequence[2];
			items[0] = "打开";
			items[1] = "删除";
			// items[2] = "清空全部历史记录";
		}

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (mIsAddFriend == false && item > 0) {
					item++;
				}

				if (item == 0) {
					openItem();
				} else if (item == 1) {
					addToFriend();
				} else if (item == 2) {
					deleteSelectData();
				} else if (item == 3) {
					clearAllData();
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void addToFriend() {
		
		if (mSelectMsgData == null) {
			return;
		}

		MsgInfoData msg = mSelectMsgData.getLinkman(UserInfo.state.id);
		if (msg == null) {
			return;
		}

		// mPostPackage = new PostPackage(this.getParent(), mAddFriendListener);
		mJsonAddFriend.setInviteLinkman(msg.senderId);
		mJsonAddFriend.name = msg.name;

		postPackageType = 0;
		mPostPackageEngine = new PostPackageEngine(context, mJsonAddFriend, handler);
		mPostPackageEngine.post();

		// if (mPostPackage.post(mJsonAddFriend, getString(R.string.host_ip),
		// true)) {
		//
		// mListView.setEnabled(false);
		// WaitingActivity.waiting(this, 0);
		// }
	}

	private void deleteSelectData() {
		
		if (mSelectMsgData == null) {
			return;
		}

		CloseQuestion();
	}

	private void CloseQuestion() {

		MsgInfoData data = mSelectMsgData.getFirstItem();
		// mPostPackage = new PostPackage(this.getParent(),
		// mSetQusetionListener);
		mJsonQuestion.id = mSelectMsgData.id;
		mJsonQuestion.owner = data.senderId;
		mJsonQuestion.cmd = JsonQuestion.MARK;

		postPackageType = 1;
		mPostPackageEngine = new PostPackageEngine(context, mJsonQuestion, handler);
		mPostPackageEngine.post();

		// if (mPostPackage.post(mJsonQuestion, getString(R.string.host_ip),
		// true)) {
		//
		// WaitingActivity.waiting(this, 0);
		// }
	}

	// public IHttpPostListener mSetQusetionListener = new IHttpPostListener() {
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
	// mMsgGroupList.delete(mSelectMsgData.id);
	// updateDataList(true);
	// } else {
	// new
	// AlertDialog.Builder(MsgGroupListActivity.this.getParent()).setTitle("提示")
	// .setMessage(ret.explain).setPositiveButton("确定", null).show();
	// }
	// }
	// }
	//
	// WaitingActivity.stop();
	// }
	// };

	// public IHttpPostListener mUpdateQusetionListener = new
	// IHttpPostListener() {
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
	// mMsgGroupList.clearMsg();
	// ProcessMessage pro = ProcessMessage.getInstance();
	// pro.process(ret);
	// } else {
	// new
	// AlertDialog.Builder(MsgGroupListActivity.this.getParent()).setTitle("提示")
	// .setMessage(ret.explain).setPositiveButton("确定", null).show();
	// }
	// }
	// }
	//
	// WaitingActivity.stop();
	// }
	// };

	protected void clearAllData() {

		// mPostPackage = new PostPackage(this.getParent(),
		// mUpdateQusetionListener);
		JsonRequest request = new JsonRequest();
		request.function = JsonFunction.QUESTION_LIST;

		postPackageType = 2;
		mPostPackageEngine = new PostPackageEngine(context, request, handler);
		mPostPackageEngine.post();

		// if (mPostPackage.post(request, getString(R.string.host_ip), true)) {
		//
		// WaitingActivity.waiting(this, 0);
		// }
	}

	@Override
	public void updateMesage() {

		updateDataList(false);
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
	// Toast.makeText(MsgGroupListActivity.this,
	// getString(R.string.qa_add_friend_ok),
	// Toast.LENGTH_SHORT).show();
	// ProcessMessage pro = ProcessMessage.getInstance();
	// pro.process(ret);
	//
	// } else {
	// new
	// AlertDialog.Builder(MsgGroupListActivity.this.getParent()).setTitle("提示")
	// .setMessage(ret.explain).setPositiveButton("确定", null).show();
	// }
	// }
	// }
	// mListView.setEnabled(true);
	// WaitingActivity.stop();
	// }
	// };
	
	@Override
	protected void onStop() {
		if(TextPlayer.getInstance().isPlaying()){
			TextPlayer.getInstance().stop();
		}
		super.onStop();
	}
}
