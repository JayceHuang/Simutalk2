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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.JsonEditLinkman;
import cn.ac.ia.directtrans.json.JsonRequestResult;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.FriendData;
import com.ztspeech.simutalk2.data.FriendDataList;
import com.ztspeech.simutalk2.data.MsgDataList;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.net.ListViewImageEngine;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;
import com.ztspeech.simutalk2.qa.message.ProcessMessage;
import com.ztspeech.simutalk2.qa.view.AdapterItemView;
import com.ztspeech.simutalk2.qa.view.DataListAdapter;
import com.ztspeech.simutalk2.qa.view.FriendItemView;

@SuppressLint("HandlerLeak")
public class FriendActivity extends UpdateBaseActivity implements OnClickListener, OnTouchListener {

	// view control
	private ListView mListView;
	private ListViewImageEngine listViewImageLoaderEngine;
	private Button mBtnFind;
	
	// 弹出菜单
	private PopupWindow mPopMenu;
	private Button mBtnDelete;
	private Button mBtnAddFriend;
	private TextView mTvLine2;
	
	
	// data
	private FriendDataList mFriends = FriendDataList.getInstance();
	private FriendData mSelectedItemData;
	private JsonEditLinkman mJsonFriend = new JsonEditLinkman();
	// private PostPackage mPostPackage;
	private static boolean isShow = false;

	private Context context;
	private PostPackageEngine mPostPackageEngine;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				postPackageCallBack((ResultPackage) msg.obj);
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
		setContentView(R.layout.activity_friend);
		context = this.getParent();
		// getWindow().setFormat(PixelFormat.RGBA_8888);
		mListView = (ListView) findViewById(R.id.lvQuestion);
		mBtnFind = (Button) findViewById(R.id.btnFind);
		mBtnFind.setOnClickListener(this);
		initListView();
	}

	private View mPopMenuView;
	
	private View getPopMenuView(){
		
		if(mPopMenuView == null){
			
			mPopMenuView = LayoutInflater.from(this).inflate(R.layout.pop_qa_msg, null);
			mBtnDelete = (Button) mPopMenuView.findViewById(R.id.btnDel);
			mBtnAddFriend = (Button) mPopMenuView.findViewById(R.id.btnAdd);
			mTvLine2 = (TextView) mPopMenuView.findViewById(R.id.tvLine2);
			
			mBtnAddFriend.setVisibility(View.GONE);
			mTvLine2.setVisibility(View.GONE);
			
			mPopMenu = new PopupWindow(mPopMenuView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mPopMenu.setBackgroundDrawable(new BitmapDrawable());
	
			mBtnDelete.setOnClickListener(new View.OnClickListener() {
		
				@Override
				public void onClick(View v) {
					mPopMenu.dismiss();
					deleteFriend();	
				}
			});		
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
	
	
	@Override
	protected void onPause() {
		
		super.onPause();
		isShow = false;
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		mFriends.setChanged(true);
		isShow = true;
	}

	public static boolean isShowNotifitionTip(MsgDataList msgList) {
		if (msgList.type == MsgInfoData.Define.TYPE_QA) {
			return true;
		}

		FriendDataList.getInstance().setChanged(true);
		if (isShow) {
			// return false;
		}

		return true;
	}

	private DataListAdapter mListViewAdapter = new DataListAdapter(this, mFriends) {
		@Override
		public AdapterItemView getAdapterItemView(Context context) {
			
			return new FriendItemView(context, listViewImageLoaderEngine);
		}
	};

	private void initListView() {
		
		listViewImageLoaderEngine = new ListViewImageEngine(mListView);
		mListViewAdapter.setDataList(mFriends);
		mListView.setOnItemClickListener(mAdapterLinstener);
		mListView.setOnItemLongClickListener(mAdapterLongLinstener);

		mListView.setDividerHeight(0);

		mListView.setAdapter(mListViewAdapter);
	}

	private OnItemClickListener mAdapterLinstener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			FriendItemView v = (FriendItemView) arg1;
			mSelectedItemData = (FriendData) v.getData();
			showMesage();
		}
	};

	protected void showMesage() {
		
		Intent intent = new Intent(FriendActivity.this, MessageActivity.class);
		intent.putExtra(MsgGroupList.PARAM_ID, mSelectedItemData.linkId);
		intent.putExtra(MsgGroupList.PARAM_TYPE, MsgInfoData.Define.TYPE_MSG);

		startActivityForResult(intent, 0);
	}

	private OnItemLongClickListener mAdapterLongLinstener = new OnItemLongClickListener() {

		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			
			FriendItemView v = (FriendItemView) arg1;
			mSelectedItemData = (FriendData) v.getData();
			showPopMenu(arg1);
			return true;
		}
	};

	private void deleteFriend() {

		if (mSelectedItemData == null) {
			return;
		}
		// mPostPackage = new PostPackage(this.getParent(),
		// mDeleteFriendListener);
		mJsonFriend.setDeleteLinkman(mSelectedItemData.id);

		mPostPackageEngine = new PostPackageEngine(context, mJsonFriend, handler);
		mPostPackageEngine.post();

		// if (mPostPackage.post(mJsonFriend, getString(R.string.host_ip),
		// true)) {
		//
		// mListView.setEnabled(false);
		// WaitingActivity.waiting(this, 0);
		// }
	}

	private void postPackageCallBack(ResultPackage result) {
		JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
		if (ret != null) {
			if (ret.succeed == true) {

				Toast.makeText(FriendActivity.this, getString(R.string.friend_delete_tip), Toast.LENGTH_SHORT).show();

				ProcessMessage pro = ProcessMessage.getInstance();
				pro.process(ret, false);
				updateMesage();
			} else {
				new AlertDialog.Builder(FriendActivity.this.getParent()).setTitle("提示").setMessage(ret.explain)
						.setPositiveButton("确定", null).show();
			}
		}
	}

	// public IHttpPostListener mDeleteFriendListener = new IHttpPostListener()
	// {
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
	// Toast.makeText(FriendActivity.this,
	// getString(R.string.friend_delete_tip), Toast.LENGTH_SHORT)
	// .show();
	//
	// ProcessMessage pro = ProcessMessage.getInstance();
	// pro.process(ret);
	// updateMesage();
	// } else {
	// new
	// AlertDialog.Builder(FriendActivity.this.getParent()).setTitle("提示").setMessage(ret.explain)
	// .setPositiveButton("确定", null).show();
	// }
	// }
	// }
	// mListView.setEnabled(true);
	// WaitingActivity.stop();
	// }
	// };

	private void updateListView() {

		mListViewAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		InteractionActivity.isQANewsUpdate = true;
		updateListView();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void updateMesage() {

		if (mFriends.isChanged()) {
			mFriends.setChanged(false);
			updateListView();
		}
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(FriendActivity.this, FindUserActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Intent intent = new Intent(FriendActivity.this, FindUserActivity.class);
			startActivityForResult(intent, 0);
		}
		return false;
	}
}
