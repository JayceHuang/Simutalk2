package com.ztspeech.simutalk2.qa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.AskTaskList;
import com.ztspeech.simutalk2.data.GlobalData;
import com.ztspeech.simutalk2.data.MsgGroupList;

public class InteractionActivity extends UpdateBaseTabActivity implements OnTabChangeListener, OnClickListener {

	private TabHost mTabHost = null;
	private TabSpec mTabSpecMsg = null;
	private TabSpec mTabSpecLinkman = null;
	private TabSpec mTabSpecQuestions = null;
	private ImageButton[] mTabBtns = new ImageButton[3];
	private TextView mtvTaskCount;
	private TextView mtvQaNewsCount;
	private TextView mtvFriendNewsCount;

	private AskTaskList mTaskList = GlobalData.getAskInstance();
	private MsgGroupList mMsgGroupList = MsgGroupList.getInstance();

	public static boolean isQANewsUpdate = true;
	public static boolean isFriendNewsUpdate = true;
	public static boolean isTaskListUpdate = true;

	private static InteractionActivity mInstance = null;

	public static InteractionActivity getInstance() {

		return mInstance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interaction);
		// getWindow().setFormat(PixelFormat.RGBA_8888);
		mInstance = this;

		mTabBtns[1] = (ImageButton) findViewById(R.id.btnUser);
		mTabBtns[0] = (ImageButton) findViewById(R.id.btnQA);
		mTabBtns[2] = (ImageButton) findViewById(R.id.btnLinkman);
		mtvTaskCount = (TextView) findViewById(R.id.tvTaskCount);
		mtvQaNewsCount = (TextView) findViewById(R.id.tvQANewsCount);
		mtvFriendNewsCount = (TextView) findViewById(R.id.tvFriendNewsCount);
		mTabBtns[0].setOnClickListener(this);
		mTabBtns[1].setOnClickListener(this);
		mTabBtns[2].setOnClickListener(this);
		mtvTaskCount.setVisibility(View.INVISIBLE);
		mtvQaNewsCount.setVisibility(View.INVISIBLE);
		mtvFriendNewsCount.setVisibility(View.INVISIBLE);

		initView();
	}

	private void initView() {
		mTabHost = this.getTabHost();

		// 新建一个newTabSpec,设置标签和图标(setIndicator),设置内容(setContent)
		mTabSpecMsg = mTabHost.newTabSpec("Msg").setIndicator("我的信息", null)
				.setContent(new Intent(this, UserStateActivity.class));

		mTabSpecQuestions = mTabHost.newTabSpec("question").setIndicator("问答记录", null)
				.setContent(new Intent(this, MsgGroupListActivity.class));

		mTabSpecLinkman = mTabHost.newTabSpec("Friend").setIndicator("联系人", null)
				.setContent(new Intent(this, FriendActivity.class));

		mTabHost.addTab(mTabSpecQuestions);

		mTabHost.addTab(mTabSpecMsg);

		mTabHost.addTab(mTabSpecLinkman);

		// 设置当前现实哪一个标签
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(1); // 0为标签ID

		// 标签切换处理，用setOnTabChangedListener
		onTabChanged(null);
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int nCount = mTabBtns.length;
		for (int i = 0; i < nCount; i++) {
			ImageButton btn = mTabBtns[i];
			if (arg0 == btn) {
				mTabHost.setCurrentTab(i);
				updateState(true);
				break;
			}
		}
	}

	/**
	 * 显示任务数量
	 * 
	 * @param count
	 */
	public void showTaskCount(int count) {

		if (mTabHost.getCurrentTab() == 1) {
			count = 0;
		}
		if (count > 0) {
			mtvTaskCount.setVisibility(View.VISIBLE);
			mtvTaskCount.setText(count + "");
		} else {
			mtvTaskCount.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 显示最新消息数量
	 * 
	 * @param count
	 */
	private void showNewsCount(int count) {

		if (mTabHost.getCurrentTab() == 0) {
			count = 0;
		}

		if (count > 0) {
			mtvQaNewsCount.setVisibility(View.VISIBLE);
			mtvQaNewsCount.setText(count + "");
		} else {
			mtvQaNewsCount.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 显示最新朋友消息数量
	 * 
	 * @param count
	 */
	private void showFriendNewsCount(int count) {

		if (mTabHost.getCurrentTab() == 2) {
			count = 0;
		}
		if (count > 0) {
			mtvFriendNewsCount.setVisibility(View.VISIBLE);
			mtvFriendNewsCount.setText(count + "");
		} else {
			mtvFriendNewsCount.setVisibility(View.INVISIBLE);
		}
	}

	public void onTabChanged(String arg0) {

		int tab = mTabHost.getCurrentTab();
		int nCount = mTabBtns.length;
		for (int i = 0; i < nCount; i++) {
			ImageButton btn = mTabBtns[i];
			if (tab == i) {
				btn.setEnabled(false);
				// btn.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
				// btn.setTextSize(16);
				// btn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				// btn.getPaint().setAntiAlias(true);// 抗锯齿
			} else {
				btn.setEnabled(true);
				// btn.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
				// btn.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
				// btn.setTextSize(14);
			}
		}
	}

	private void updateState(boolean bUpdate) {

		if (isQANewsUpdate || bUpdate) {
			isQANewsUpdate = false;
			showNewsCount(mMsgGroupList.getNewsQACount());
			showFriendNewsCount(mMsgGroupList.getFriendNewsCount());
		}

		if (isTaskListUpdate || bUpdate) {
			isTaskListUpdate = false;
			int nCount = 0;
			nCount = mTaskList.getTaskCount();
			showTaskCount(nCount);
		}
	}

	@Override
	public void updateMesage() {

		updateState(false);

	}

}
