package com.ztspeech.simutalk2.qa.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.DataObject;
import com.ztspeech.simutalk2.data.FriendData;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.net.ListViewImageEngine;

public class FindUserItemView extends AdapterItemView implements OnClickListener{

	public FindUserItemView(Context context) {
		super(context);
		this.context = context;
	}

	public FindUserItemView(Context context, ListViewImageEngine listViewImageLoaderEngine) {
		super(context);
		this.listViewImageLoaderEngine = listViewImageLoaderEngine;
		this.context = context;

	}
	private Context context;
	private TextView mtvName;
	private ImageView mLayoutUser1;
	private ImageButton mBtnAddFriend;
	private TextView tvDistance;//显示距离
	private ListViewImageEngine listViewImageLoaderEngine = null;

	public void setData(DataObject data) {

		if (data == null) {
			return;
		}
		this.mData = data;
		FriendData fd = (FriendData) mData;
		mtvName.setText(fd.name);
		if(fd.user.distance==null){
			tvDistance.setVisibility(View.INVISIBLE);
		}else{
			Log.e("wo ca ca ca ca ca ca", fd.user.distance);
			if(fd.user.distance.equals("0.0")){
				tvDistance.setText(100+"米");
			}else{
				tvDistance.setText(Double.valueOf(fd.user.distance)*100+"米");
			}
		}
		com.ztspeech.simutalk2.data.FriendData frined = (com.ztspeech.simutalk2.data.FriendData) data;
		// mtvText.setTextSize(mUser.getFontSize());
		LogInfo.LogOut("haitian", "MsgGroupList<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		LogInfo.LogOut("haitian", "frined.photo = " + frined.photoId);
		LogInfo.LogOut("haitian", "MsgGroupList>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

		if (listViewImageLoaderEngine != null) {
			listViewImageLoaderEngine.imageLoaderScale(mLayoutUser1, frined.photoId, R.drawable.qa_you_qa_min_friend_head, -1);
		}
	}

	public void init(Context context) {

		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.item_finduser, this, true);
		mtvName = (TextView) findViewById(R.id.tvName);
		mBtnAddFriend = (ImageButton) findViewById(R.id.btnAddFriend);
		mLayoutUser1 = (ImageView) findViewById(R.id.layoutUser1);
		tvDistance = (TextView) findViewById(R.id.tvDistance);
		mBtnAddFriend.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == mBtnAddFriend){
			Intent intent = new Intent("com.action.addfriend");
			intent.putExtra("name", mData.name);
			intent.putExtra("id",(int) mData.id);
			context.sendBroadcast(intent);
		}
		
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}
	
	
}
