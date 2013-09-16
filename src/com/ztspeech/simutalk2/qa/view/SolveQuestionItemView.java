package com.ztspeech.simutalk2.qa.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.DataObject;
import com.ztspeech.simutalk2.data.MsgInfoData;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.data.UserInfoList;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.net.ListViewImageEngine;

public class SolveQuestionItemView extends AdapterItemView implements OnClickListener{

	public SolveQuestionItemView(Context context, OnMessageClickListener listener) {
		super(context);
		mListener = listener;
	}
	
	public SolveQuestionItemView(Context context, OnMessageClickListener listener,
			ListViewImageEngine listViewImageLoaderEngine) {
		super(context);
		mListener = listener;
		this.listViewImageLoaderEngine = listViewImageLoaderEngine;
	}

	public interface OnMessageClickListener {
		public void OnClick(String cmd, MsgInfoData data);
	}

	private TextView mtvName = null;
	private TextView mtvText = null;
	private TextView mtvTime = null;
	private TextView mtvLen = null;
	// private Button mBtnEdit = null;
	private Button mBtnPlay;
	private ImageView mLayoutUser1;
	private ImageView mLayoutUser2;
	private LinearLayout mLayoutLeft;
	private LinearLayout mLayoutRight;
	private ListViewImageEngine listViewImageLoaderEngine = null;
	private UserInfo mUser = UserInfo.getInstanse();

	private OnMessageClickListener mListener = null;
	public void setData(DataObject data) {

		this.mData = data;
		if (data == null) {
			return;
		}
		MsgInfoData item = (MsgInfoData) data;
		
		// 用户信息通一管理
		cn.ac.ia.directtrans.json.UserInfo user = UserInfoList.getInstanse().findById(item.senderId);
		String userName = "";
		String photo = "";
		if(user != null){
			userName = user.name;
			photo = user.photo;
		}		
		
		mtvText.setText(item.text);
		mtvText.setTextSize(mUser.getFontSize());
		mtvTime.setText(UserInfo.getTimeString(item.time));
		mtvName.setText(userName);
		
		if (item.senderId == UserInfo.state.id) {
			mLayoutUser1.setVisibility(View.INVISIBLE);
			//mLayoutUser2.setVisibility(View.VISIBLE);
			mLayoutLeft.setVisibility(View.VISIBLE);
			mLayoutRight.setVisibility(View.GONE);			

			LogInfo.LogOut("haitian", "MessageItemView<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			LogInfo.LogOut("haitian", "UserInfo.state.photo = " + UserInfo.state.photo);
			LogInfo.LogOut("haitian", "MessageItemView>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

			if (listViewImageLoaderEngine != null) {
				listViewImageLoaderEngine.imageLoaderScale(mLayoutUser2, UserInfo.state.photo,
						R.drawable.qa_you_qa_min_friend_head, -1);
			}
		} else {
			mLayoutUser1.setVisibility(View.VISIBLE);
			//mLayoutUser2.setVisibility(View.INVISIBLE);
			mLayoutLeft.setVisibility(View.GONE);
			mLayoutRight.setVisibility(View.VISIBLE);

			LogInfo.LogOut("haitian", "MessageItemView<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			LogInfo.LogOut("haitian", "item.photo = " + photo);
			LogInfo.LogOut("haitian", "MessageItemView>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
			if (listViewImageLoaderEngine != null) {
				listViewImageLoaderEngine.imageLoaderScale(mLayoutUser1, photo, R.drawable.qa_you_qa_min_friend_head, -1);
			}
		}

		if (item.vLen == 0) {
			mBtnPlay.setVisibility(View.GONE);
			mtvLen.setVisibility(View.GONE);
		} else {
			mBtnPlay.setVisibility(View.VISIBLE);
			mtvLen.setVisibility(View.VISIBLE);
			mtvLen.setText(UserInfo.getSbxLen(item.vLen));
		}
	}

	public void init(Context context) {

		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.item_solvequestion, this, true);
		mtvText = (TextView) findViewById(R.id.tvItemText);
		mtvName = (TextView) findViewById(R.id.tvName);
		mtvTime = (TextView) findViewById(R.id.tvAskTime);
		mtvLen = (TextView) findViewById(R.id.tvLen);

		mLayoutUser1 = (ImageView) findViewById(R.id.layoutUser1);
		mLayoutUser2 = (ImageView) findViewById(R.id.layoutUser2);
		mLayoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
		mLayoutRight = (LinearLayout) findViewById(R.id.layoutRight);

		// mBtnEdit = (Button) findViewById(R.id.btnMsgEdit);
		mBtnPlay = (Button) findViewById(R.id.btnPlay);
		// mtvCount = (TextView) findViewById(R.id.tv_msg_data_count);
		// mtvLanguage = (TextView) findViewById(R.id.tv_lv_msg_language);
		// mLayoutUser = (FrameLayout) findViewById(R.id.ll_msg_data_user);
		// mBtnPlay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (mListener == null) {
			return;
		}
		// if( mBtnPlay == v){
		// mListener.OnClick("play",(MsgInfoData) mData);
		// }
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}

}
