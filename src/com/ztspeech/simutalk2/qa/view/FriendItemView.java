package com.ztspeech.simutalk2.qa.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.DataObject;
import com.ztspeech.simutalk2.data.MsgDataList;
import com.ztspeech.simutalk2.data.MsgGroupList;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.net.ListViewImageEngine;

public class FriendItemView extends AdapterItemView {

	public FriendItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FriendItemView(Context context, ListViewImageEngine listViewImageLoaderEngine) {
		super(context);
		this.listViewImageLoaderEngine = listViewImageLoaderEngine;
		// TODO Auto-generated constructor stub
	}

	private MsgGroupList mMsgGroupList = MsgGroupList.getInstance();
	private TextView mtvName;
	private TextView mtvText;
	private TextView mtvNewsCount;
	private TextView mtvMsgTime;
	private ImageView mLayoutUser1;
	private ListViewImageEngine listViewImageLoaderEngine = null;

	public void setData(DataObject data) {

		if (data == null) {
			return;
		}
		this.mData = data;

		mtvName.setText(data.name);

		com.ztspeech.simutalk2.data.FriendData frined = (com.ztspeech.simutalk2.data.FriendData) data;
		// mtvText.setTextSize(mUser.getFontSize());
		MsgDataList msgList = mMsgGroupList.findItem(frined.linkId, MsgInfoData.Define.TYPE_MSG);
		int nNews = 0;
		if (msgList == null) {
			mtvNewsCount.setText("");
			mtvMsgTime.setText("");
			mtvText.setText("");
		} else {
			nNews = msgList.getNewsCount();
			mtvText.setText(msgList.text.split("/")[0]);
			mtvMsgTime.setText(UserInfo.getTimeString(msgList.time));
		}
		LogInfo.LogOut("haitian", "MsgGroupList<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		LogInfo.LogOut("haitian", "frined.photo = " + frined.photoId);
		LogInfo.LogOut("haitian", "MsgGroupList>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		if (listViewImageLoaderEngine != null) {
			listViewImageLoaderEngine.imageLoaderScale(mLayoutUser1, frined.photoId, R.drawable.qa_you_qa_min_friend_head, -1);
		}

		if (nNews > 0) {
			mtvNewsCount.setText(nNews + "");
			mtvNewsCount.setVisibility(View.VISIBLE);
		} else {
			mtvNewsCount.setText("");
			mtvNewsCount.setVisibility(View.INVISIBLE);
		}
	}

	public void init(Context context) {

		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.item_friend, this, true);
		mtvName = (TextView) findViewById(R.id.tvName);
		mtvNewsCount = (TextView) findViewById(R.id.tvMsgCount);
		mtvMsgTime = (TextView) findViewById(R.id.tvMsgTime);
		mtvText = (TextView) findViewById(R.id.tvMsgText);

		mLayoutUser1 = (ImageView) findViewById(R.id.layoutUser1);
		// mLayoutUser2 = (LinearLayout) findViewById(R.id.layoutUser2);

	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}
}
