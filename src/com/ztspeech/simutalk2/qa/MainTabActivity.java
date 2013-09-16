package com.ztspeech.simutalk2.qa;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.AskTaskList;
import com.ztspeech.simutalk2.data.GlobalData;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.TextPlayer;
import com.ztspeech.simutalk2.dictionary.activity.SearchActivity;
import com.ztspeech.simutalk2.dictionary.activity.SettingActivity;
import com.ztspeech.simutalk2.trans.InterpretActivity;

public class MainTabActivity extends UpdateBaseTabActivity implements OnClickListener, OnTabChangeListener {

	private MsgGroupList mMsgGroupList = MsgGroupList.getInstance();
	private AskTaskList mTaskList = GlobalData.getAskInstance();;

	private TabHost mTabHost = null;
	private TabSpec mTabInteraction = null;
	private TabSpec mTabTransCN = null;
	private TabSpec mTabHistory = null;
	private TabSpec mTabConfig = null;
	private TabSpec mTabItems[] = new TabSpec[4];
	private ImageButton[] mTabBtns = new ImageButton[4];
	private TextView mtvTaskCount;
	private boolean mIsShowQA = false;
	private static MainTabActivity mInstance = null;

	public static MainTabActivity getInstance() {

		return mInstance;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			overridePendingTransition(R.anim.slideinleft, R.anim.slideoutright);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.activity_main_tab);

		mInstance = this;

		mTabBtns[0] = (ImageButton) findViewById(R.id.btnTrans);
		mTabBtns[1] = (ImageButton) findViewById(R.id.btnQA);
		mTabBtns[2] = (ImageButton) findViewById(R.id.btnLib);
		mTabBtns[3] = (ImageButton) findViewById(R.id.btnSettings);
		mtvTaskCount = (TextView) findViewById(R.id.tvTaskCount);

		mTabBtns[0].setOnClickListener(this);
		mTabBtns[1].setOnClickListener(this);
		mTabBtns[2].setOnClickListener(this);
		mTabBtns[3].setOnClickListener(this);
		mtvTaskCount.setVisibility(View.INVISIBLE);
		initView();

		mTaskList.setChanged(true);
		MsgGroupList.getInstance().setMsgChanged(true);
		showTabItem();

	}

	private void showTabItem() {
		Intent intent = this.getIntent();
		int showItem = intent.getIntExtra(MainActivity.MAIN_TAB_DO, MainActivity.MAIN_TAB_SHOW_TRANS);
		mIsShowQA = false;
		switch (showItem) {
		case MainActivity.MAIN_TAB_SHOW_LIB:
			mTabHost.setCurrentTab(2);
			break;
		case MainActivity.MAIN_TAB_SHOW_QA:
			mTabHost.setCurrentTab(1);
			mIsShowQA = true;
			break;
		case MainActivity.MAIN_TAB_SHOW_SETTINGS:
			mTabHost.setCurrentTab(3);
			break;
		case MainActivity.MAIN_TAB_SHOW_TRANS:
		default:
			mTabHost.setCurrentTab(0);
			break;
		}
		onTabChanged(null);
		
	}

	private void initView() {
		mTabHost = this.getTabHost();

		mTabItems[0] = mTabTransCN = mTabHost.newTabSpec("Msg").setIndicator("", null)
				.setContent(new Intent(this, InterpretActivity.class));

		mTabItems[1] = mTabInteraction = mTabHost.newTabSpec("qa").setIndicator("", null)
				.setContent(new Intent(this, InteractionActivity.class));

		mTabItems[2] = mTabHistory = mTabHost.newTabSpec("lib").setIndicator("", null)
				.setContent(new Intent(this, SearchActivity.class));

		mTabItems[3] = mTabConfig = mTabHost.newTabSpec("settings").setIndicator("", null)
				.setContent(new Intent(this, SettingActivity.class));

		mTabHost.addTab(mTabTransCN);
		mTabHost.addTab(mTabInteraction);
		mTabHost.addTab(mTabHistory);
		mTabHost.addTab(mTabConfig);

		mTabHost.setOnTabChangedListener(this);
	}

	public void onClick(View arg0) {

		int nCount = mTabBtns.length;
		for (int i = 0; i < nCount; i++) {
			ImageButton btn = mTabBtns[i];
			if (arg0 == btn) {
				mTabHost.setCurrentTab(i);
				if (i == 1) {
					mIsShowQA = true;
				} else {
					mIsShowQA = false;
				}
				updateTaskCount();
				break;
			}
		}
	}

	/**
	 * 显示任务数量
	 * 
	 * @param count
	 */
	public void updateTaskCount() {

		int count = 0;
		if (mIsShowQA == false) {

			count = mTaskList.getTaskCount() + mMsgGroupList.getNewsCount();
		}

		if (count > 0) {
			mtvTaskCount.setVisibility(View.VISIBLE);
			mtvTaskCount.setText(count + "");
		} else {
			mtvTaskCount.setVisibility(View.INVISIBLE);
		}
	}

	public void onTabChanged(String arg0) {
//		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		if(imm.isActive()){
//			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//		}
		if(TextPlayer.getInstance().isPlaying()){
			TextPlayer.getInstance().stop();
		}
		int tab = mTabHost.getCurrentTab();
		int nCount = mTabBtns.length;
		for (int i = 0; i < nCount; i++) {
			ImageButton btn = mTabBtns[i];
			if (tab == i) {
				btn.setEnabled(false);
			} else {
				btn.setEnabled(true);
			}
		}
	}

	@Override
	public void updateMesage() {

		boolean update = false;
		if (mMsgGroupList.isMsgChanged()) {
			mMsgGroupList.setMsgChanged(false);
			InteractionActivity.isFriendNewsUpdate = true;
			InteractionActivity.isQANewsUpdate = true;
			MsgGroupListActivity.isViewUpdate = true;
			update = true;
		}
		if (mTaskList.isChanged()) {
			mTaskList.setChanged(false);
			InteractionActivity.isTaskListUpdate = true;
			UserStateActivity.isTaskListUpdate = true;
			update = true;
		}
		if (update) {
			updateTaskCount();
		}

	}

}
