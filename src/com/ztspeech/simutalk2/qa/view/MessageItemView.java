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

public class MessageItemView extends AdapterItemView implements OnClickListener {

	public interface OnMessageClickListener {
		public void OnClick(String cmd, MsgInfoData data);
	}

	private TextView mtvName1;
	private TextView mtvName2;
	private TextView mtvText ;
	private TextView mtvTime;
	private TextView mtvLen;
	private TextView mtvText2;
	private LinearLayout mllBack;
	private Button mBtnPlay;
	private ImageView mImageUser1;
	private ImageView mImageUser2;
	private LinearLayout mLayoutUser1;
	private LinearLayout mLayoutUser2;

//	private LinearLayout mLayoutLeft;
//	private LinearLayout mLayoutRight;
	private ListViewImageEngine listViewImageLoaderEngine = null;
	private UserInfo mUser = UserInfo.getInstanse();

	private String[] text = null;
	private OnMessageClickListener mListener = null;

	public MessageItemView(Context context, OnMessageClickListener listener) {
		super(context);
		mListener = listener;
	}

	public MessageItemView(Context context, OnMessageClickListener listener,
			ListViewImageEngine listViewImageLoaderEngine) {
		super(context);
		mListener = listener;
		this.listViewImageLoaderEngine = listViewImageLoaderEngine;
	}

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
		
		//显示对话翻译
		text = item.text.split("/");
		mtvText.setText(text[0]);
		if(mUser.isTranslatetalk()){
			if(text.length>1){
				mtvText2.setText(text[1]);
				mtvText2.setTextSize(mUser.getFontSize());
				mtvText2.setTextColor(getResources().getColor(R.color.item_focused_false));
				mtvText.setTextSize(mUser.getFontSize()-2);
				mtvText.setTextColor(getResources().getColor(R.color.item_focused_true));
			}else{
				mtvText2.setVisibility(View.GONE);
			}
		}else{
			mtvText2.setVisibility(View.GONE);
		}
		
		
		
		mtvTime.setText(UserInfo.getTimeString(item.time));
		if (item.senderId == UserInfo.state.id) {
			mLayoutUser1.setVisibility(View.GONE);
			mLayoutUser2.setVisibility(View.VISIBLE);
		//	mLayoutLeft.setVisibility(View.VISIBLE);
		//	mLayoutRight.setVisibility(View.GONE);

			LogInfo.LogOut("haitian", "MessageItemView<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			LogInfo.LogOut("haitian", "UserInfo.state.photo = " + UserInfo.state.photo);
			LogInfo.LogOut("haitian", "MessageItemView>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

			userName = UserInfo.state.name;
			if (listViewImageLoaderEngine != null) {
				listViewImageLoaderEngine.imageLoaderScale(mImageUser2, UserInfo.state.photo,
						R.drawable.qa_you_qa_min_friend_head, -1);
			}
			
			mtvName2.setText(userName);
			mllBack.setBackgroundResource(R.drawable.qa_you_qa_min_dont_bg);
				
		} else {
			mLayoutUser1.setVisibility(View.VISIBLE);
			mLayoutUser2.setVisibility(View.GONE);
//			mLayoutLeft.setVisibility(View.GONE);
//			mLayoutRight.setVisibility(View.VISIBLE);
			LogInfo.LogOut("haitian", "MessageItemView<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			LogInfo.LogOut("haitian", "item.photo = " + photo);
			LogInfo.LogOut("haitian", "MessageItemView>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			if (listViewImageLoaderEngine != null) {
				listViewImageLoaderEngine.imageLoaderScale(mImageUser1, photo, R.drawable.qa_you_qa_min_friend_head, -1);
			}
			

			mtvName1.setText(userName);			
			mllBack.setBackgroundResource(R.drawable.trans_font_bg_ce_1_normal);		
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

	public void clearCache(){
		mtvText.setTextSize(mUser.getFontSize());
		mtvText.setTextColor(getResources().getColor(R.color.item_focused_false));
		mtvText2.setVisibility(View.VISIBLE);
		mtvText2.setTextSize(mUser.getFontSize()-2);
		mtvText2.setTextColor(getResources().getColor(R.color.item_focused_true));
	}
	
	public void init(Context context) {

		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.item_message, this, true);
		mtvText = (TextView) findViewById(R.id.tvItemText);
		mtvText.setTextSize(mUser.getFontSize());
		mtvText.setTextColor(getResources().getColor(R.color.item_focused_false));
		mtvText2 = (TextView) findViewById(R.id.tvItemText2);
		mtvText2.setTextColor(getResources().getColor(R.color.item_focused_true));
		mtvName1 = (TextView) findViewById(R.id.tvName1);
		mtvName2 = (TextView) findViewById(R.id.tvName2);
		mtvTime = (TextView) findViewById(R.id.tvAskTime);
		mtvLen = (TextView) findViewById(R.id.tvLen);

		mLayoutUser1 = (LinearLayout) findViewById(R.id.llUser1);
		mLayoutUser2 = (LinearLayout) findViewById(R.id.llUser2);
		
		mllBack =  (LinearLayout) findViewById(R.id.llBack);
		mImageUser1 = (ImageView) findViewById(R.id.imageUser1);
		mImageUser2 = (ImageView) findViewById(R.id.imageUser2);
//		mLayoutLeft = (LinearLayout) findViewById(R.id.layoutLeft);
//		mLayoutRight = (LinearLayout) findViewById(R.id.layoutRight);

		// mBtnEdit = (Button) findViewById(R.id.btnMsgEdit);
		mBtnPlay = (Button) findViewById(R.id.btnPlay);
		// mtvCount = (TextView) findViewById(R.id.tv_msg_data_count);
		// mtvLanguage = (TextView) findViewById(R.id.tv_lv_msg_language);
		// mLayoutUser = (FrameLayout) findViewById(R.id.ll_msg_data_user);
		// mBtnPlay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mListener == null) {
			return;
		}
		// if( mBtnPlay == v){
		// mListener.OnClick("play",(MsgInfoData) mData);
		// }
	}
}
