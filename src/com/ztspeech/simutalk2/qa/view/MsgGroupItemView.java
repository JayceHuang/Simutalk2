package com.ztspeech.simutalk2.qa.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.DataObject;
import com.ztspeech.simutalk2.data.MsgDataList;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.data.UserInfoList;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.net.ListViewImageEngine;

public class MsgGroupItemView extends AdapterItemView {

	private TextView mtvName1;
	private TextView mtvName2;
	private LinearLayout mllBack;
	private ImageView mImageUser1;
	private ImageView mImageUser2;

	private TextView mtvMsgText;
	private TextView mtvMsgTime;
	private TextView mtvMsgCount;
	private TextView mtvMsgState;

//	private LinearLayout mLayoutLeft;
	//private LinearLayout mLayoutRight;
	
	private LinearLayout mLayoutUser1;
	private LinearLayout mLayoutUser2;
	private UserInfo mUser = UserInfo.getInstanse();
	private ListViewImageEngine listViewImageLoaderEngine = null;

	public MsgGroupItemView(Context context) {
		super(context);
	}

	public MsgGroupItemView(Context context,
			ListViewImageEngine listViewImageLoaderEngine) {
		super(context);
		this.listViewImageLoaderEngine = listViewImageLoaderEngine;
	}

	public void setData(DataObject data) {

		if (data == null) {
			return;
		}
		this.mData = data;

		MsgDataList d = (MsgDataList) data;
		cn.ac.ia.directtrans.json.UserInfo user = UserInfoList.getInstanse()
				.findById(d.senderId);
		String userName = data.name;
		String photo = "";
		if (user != null) {
			userName = user.name;
			photo = user.photo;
		}
		mtvMsgText.setText(d.text);
		mtvMsgText.setTextSize(mUser.getFontSize());
		mtvMsgTime.setText(UserInfo.getTimeString(d.time));
		int nNews = d.getNewsCount();
		if (d.senderId == UserInfo.state.id) {
			mLayoutUser1.setVisibility(GONE);
			mLayoutUser2.setVisibility(VISIBLE);
//			mLayoutLeft.setVisibility(VISIBLE);
//			mLayoutRight.setVisibility(GONE);
			
			mllBack.setBackgroundResource(R.drawable.qa_you_qa_min_dont_bg);
			mtvName2.setText(userName);

			LogInfo.LogOut("haitian",
					"MsgGroupItemView<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			LogInfo.LogOut("haitian", "UserInfo.state.photo = "
					+ UserInfo.state.photo);
			LogInfo.LogOut("haitian",
					"MsgGroupItemView>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			if (listViewImageLoaderEngine != null) {
				listViewImageLoaderEngine.imageLoaderScale(mImageUser2,
						UserInfo.state.photo,
						R.drawable.qa_you_qa_min_friend_head, nNews);
			}

		} else {
			mLayoutUser1.setVisibility(VISIBLE);
			mLayoutUser2.setVisibility(GONE);
//			mLayoutLeft.setVisibility(GONE);
//			mLayoutRight.setVisibility(VISIBLE);
			
			mllBack.setBackgroundResource(R.drawable.trans_font_bg_ce_1_normal);
			mtvName1.setText(userName);

			LogInfo.LogOut("haitian",
					"MsgGroupItemView<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			LogInfo.LogOut("haitian", "d.photo = " + photo);
			LogInfo.LogOut("haitian",
					"MsgGroupItemView>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			if (listViewImageLoaderEngine != null) {
				listViewImageLoaderEngine.imageLoaderScale(mImageUser1, photo,
						R.drawable.qa_you_qa_min_friend_head, nNews);
			}
		}

		if (nNews > 0) {
			mtvMsgCount.setText(nNews + "");
			mtvMsgCount.setVisibility(View.VISIBLE);
		} else {
			mtvMsgCount.setText("");
			mtvMsgCount.setVisibility(View.GONE);
		}

		if (d.closed()) {
			mtvMsgState.setBackgroundResource(R.drawable.qa_state_3);
		} else if (d.IsSolved()) {
			mtvMsgState.setBackgroundResource(R.drawable.qa_state_2);
		} else {
			mtvMsgState.setBackgroundResource(R.drawable.qa_state_1);
		}
	}

	public void init(Context context) {

		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.item_questions, this,
				true);

//		mLayoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
	//	mLayoutRight = (LinearLayout) findViewById(R.id.layoutRight);
		
		mtvName1 = (TextView) findViewById(R.id.tvName1);
		mtvName2 = (TextView) findViewById(R.id.tvName2);

		mLayoutUser1 = (LinearLayout) findViewById(R.id.llUser1);
		mLayoutUser2 = (LinearLayout) findViewById(R.id.llUser2);

		mllBack = (LinearLayout) findViewById(R.id.llBack);
		mImageUser1 = (ImageView) findViewById(R.id.imageUser1);
		mImageUser2 = (ImageView) findViewById(R.id.imageUser2);

		mtvMsgText = (TextView) findViewById(R.id.tvMsgText);
		mtvMsgTime = (TextView) findViewById(R.id.tvMsgTime);
		mtvMsgCount = (TextView) findViewById(R.id.tvMsgCount);
		mtvMsgState = (TextView) findViewById(R.id.tvMsgState);
		// mtvLanguage = (TextView) findViewById(R.id.tv_lv_msg_language);
		// mLayoutUser = (FrameLayout) findViewById(R.id.ll_msg_data_user);

		// resource[0][0] =0;
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}

}
