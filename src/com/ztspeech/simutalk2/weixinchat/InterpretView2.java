package com.ztspeech.simutalk2.weixinchat;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztspeech.recognizer.EngineResultFlag;
import com.ztspeech.recognizer.interf.NewRecognizerViewListenerInterface;
import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.dictionary.util.Util.R_String;
import com.ztspeech.simutalk2.qa.view.DirWordSelectDialog;
import com.ztspeech.simutalk2.qa.view.DirWordSelectDialog.SimuTalkSendDialogListener;

public class InterpretView2 {
	private RelativeLayout record_layout;
	private ImageButton mBtnRecord;
	private TextView text_status;
	private Button btnCancel;
	private AnimationDrawable rocketAnimation;
	private AnimationDrawable noVoiceAnimation;
	private ImageView imageWaite, imageNoVoiceAnim;
	private Handler mHandlerKeyboard;
	private Context mContext;
	private ArrayList<String> datas = new ArrayList<String>();
	private int[] recordViewArr = new int[] { R.drawable.record_r1_c1,
			R.drawable.record_r4_c1, R.drawable.record_r7_c1,
			R.drawable.record_r11_c1, R.drawable.record_r15_c1,
			R.drawable.record_r19_c1, R.drawable.record_r23_c1,
			R.drawable.record_r27_c1 };
	// private int[] recordBgArr = new int[] {
	// R.drawable.trans_bottorm_button_recording1,
	// R.drawable.trans_bottorm_button_recording2,
	// R.drawable.trans_bottorm_button_recording3,
	// R.drawable.trans_bottorm_button_recording4,
	// R.drawable.trans_bottorm_button_recording5,
	// R.drawable.trans_bottorm_button_recording6,
	// R.drawable.trans_bottorm_button_recording7,
	// R.drawable.trans_bottorm_button_recording8 };
	// =============================================================================================================
	private DirWordSelectDialog resultDialog;
	private LayoutInflater inflater;
	private View v;
	private PopupWindow mRecognizerWindow;
	private boolean isHaveView = false;

	// =====================================================================
	public InterpretView2(Context context, Handler handler, View v, OnClickListener mOnClickListener) {
		mHandlerKeyboard = handler;
		isHaveView = true;
		mContext = context;
		record_layout = (RelativeLayout) v.findViewById(R.id.record_layout);
		mBtnRecord = (ImageButton) v.findViewById(R.id.btn_record);
		//btn_bg = (ImageButton) v.findViewById(R.id.btn_bg);
		imageWaite = (ImageView) v.findViewById(R.id.imageviewanim);
		text_status = (TextView) v.findViewById(R.id.text_status);
		btnCancel = (Button) v.findViewById(R.id.btn_cancel);
		imageNoVoiceAnim = (ImageView) v.findViewById(R.id.imagenovoiceanim);
		mBtnRecord.setOnClickListener(mOnClickListener);
		//btn_bg.setOnClickListener(mOnClickListener);
		btnCancel.setOnClickListener(mOnClickListener);
		imageNoVoiceAnim.setOnClickListener(mOnClickListener);

		imageWaite.setBackgroundResource(R.drawable.recognizer_wait_anim_new);
		rocketAnimation = (AnimationDrawable) imageWaite.getBackground();
		// imageNoVoiceAnim.setBackgroundResource(R.drawable.record_no_voice_anim);
		// noVoiceAnimation = (AnimationDrawable)
		// imageNoVoiceAnim.getBackground();
	}

	public InterpretView2(Context context, Handler handler, OnClickListener mOnClickListener) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		isHaveView = false;
		v = inflater.inflate(R.layout.recognizer_view, null);
		mRecognizerWindow = new PopupWindow(v, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mRecognizerWindow.setFocusable(true);
		mRecognizerWindow.setOutsideTouchable(false);
		mHandlerKeyboard = handler;
		mContext = context;
		record_layout = (RelativeLayout) v.findViewById(R.id.record_layout);
		mBtnRecord = (ImageButton) v.findViewById(R.id.btn_record);
		//btn_bg = (ImageButton) v.findViewById(R.id.btn_bg);
		imageWaite = (ImageView) v.findViewById(R.id.imageviewanim);
		text_status = (TextView) v.findViewById(R.id.text_status);
		btnCancel = (Button) v.findViewById(R.id.btn_cancel);
		imageNoVoiceAnim = (ImageView) v.findViewById(R.id.imagenovoiceanim);
		mBtnRecord.setOnClickListener(mOnClickListener);
		//btn_bg.setOnClickListener(mOnClickListener);
		btnCancel.setOnClickListener(mOnClickListener);
		imageNoVoiceAnim.setOnClickListener(mOnClickListener);
		imageWaite.setBackgroundResource(R.drawable.recognizer_wait_anim_new);
		rocketAnimation = (AnimationDrawable) imageWaite.getBackground();
		// imageNoVoiceAnim.setBackgroundResource(R.drawable.record_no_voice_anim);
		// noVoiceAnimation = (AnimationDrawable)
		// imageNoVoiceAnim.getBackground();
	}

	public NewRecognizerViewListenerInterface mNewRecognizerViewListenerInterface = new NewRecognizerViewListenerInterface() {
		long count = 0;

		public void onVoiceValue(int value) {
			int v = 0;
			int mRecordAnimationCount1 = recordViewArr.length;
			if (value > 0) {
				if (value < 5) {
					v = 1;
				} else {
					v = (value * mRecordAnimationCount1) / 100;
					if (v >= mRecordAnimationCount1) {
						v = mRecordAnimationCount1 - 1;
					}
				}
			}
			count++;
			if (count % 10 == 0) {
				mHandlerKeyboard.sendMessage(Message.obtain(mHandlerKeyboard, Util.ON_VOICE_VALUE, v));
			}
		}

		public void onRecordEnd() {
			mHandlerKeyboard.sendEmptyMessage(Util.ON_RECORD_END);
			LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>-onRecordEnd  ");
		}

		public void onRecordBegin() {
			mHandlerKeyboard.sendEmptyMessage(Util.ON_RECORD_BEGIN);
			LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>-onRecordBegin  ");
		}

		public void onRecorderError(int error) {
			mHandlerKeyboard.sendEmptyMessage(Util.ON_RECORDER_ERROR);
			LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>-onRecorderError  ");
		}

		public void onWaitBegin() {
			mHandlerKeyboard.sendEmptyMessage(Util.ON_WAIT_BEGIN);
			LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>-onWaitBegin ");
		}

		public void onWaitEnd() {
			mHandlerKeyboard.sendEmptyMessage(Util.ON_WAIT_END);
			LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>-onWaitEnd ");
		}

		public void onRecognizerError(int flag) {
			LogInfo.LogOut("haitian", ">>>>>>>>>>>>>>>-onRecognizerError flag = " + flag);
			mHandlerKeyboard.sendMessage(Message.obtain(mHandlerKeyboard, Util.ON_RECOGNIZER_ERROR, flag));
		}

		public void setListView(String[] data) {
			datas.clear();
			for (String result : data) {
				datas.add(result);
			}
			mHandlerKeyboard.sendMessage(Message.obtain(mHandlerKeyboard, Util.SET_LIST_VIEW, datas));
		}
	};

	public void dispRresultList(ArrayList<String> mDatas) {
		if (isHaveView) {
			setRecordLayoutDisp(View.GONE);
		} else {
			dismissPopWindow();
		}
		resultDialog = new DirWordSelectDialog(mContext).setTitle("选择要查的单词").setButton("确定", "取消", null,
				new SimuTalkSendDialogListener() {
					public void onClick() {

					}
				});
		resultDialog.setItems(mDatas, mOnItemClickListener);
		resultDialog.show();

	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			resultDialog.dismiss();
			mHandlerKeyboard.sendMessage(Message.obtain(mHandlerKeyboard, Util.SELECT_RESULT, datas.get(position)));
		}

	};
	private boolean flag = false;

	public void startViewAnim() {
		//btn_bg.setVisibility(View.GONE);
		mBtnRecord.setVisibility(View.GONE);
		imageNoVoiceAnim.setVisibility(View.GONE);
		imageWaite.setVisibility(View.VISIBLE);
		// noVoiceAnimation.stop();
		rocketAnimation.start();
		flag = true;
	}

	public void clearViewAnim() {
		mBtnRecord.setVisibility(View.VISIBLE);
		//btn_bg.setVisibility(View.VISIBLE);
		mBtnRecord.setEnabled(true);
		//btn_bg.setBackgroundResource(R.drawable.trans_bottorm_button_recording1);
		mBtnRecord.setBackgroundResource(R.drawable.weixin_dlg_btnr);
		imageWaite.setVisibility(View.GONE);
		if (flag) {
			rocketAnimation.stop();
		}
		flag = false;
	}

	public void _clearViewAnim() {
		mBtnRecord.setVisibility(View.VISIBLE);
		//btn_bg.setVisibility(View.VISIBLE);
		mBtnRecord.setEnabled(true);
		//btn_bg.setBackgroundResource(R.drawable.trans_bottorm_button_recording1);
		mBtnRecord.setBackgroundResource(R.drawable.weixin_dlg_btnr);
		imageWaite.setVisibility(View.GONE);
		rocketAnimation.stop();
		flag = false;
	}

	public void startNoVoiceAnim() {
		imageNoVoiceAnim.setVisibility(View.VISIBLE);
		// btn_bg.setVisibility(View.GONE);
		mBtnRecord.setVisibility(View.GONE);
		// noVoiceAnimation.start();
	}

	public void clearNoVoiceAnim() {
		//btn_bg.setVisibility(View.VISIBLE);
		mBtnRecord.setVisibility(View.VISIBLE);
//		btn_bg.setBackgroundResource(R.drawable.trans_bottorm_button_recording1);
		mBtnRecord.setBackgroundResource(R.drawable.weixin_dlg_btnr);
		// noVoiceAnimation.stop();
		imageNoVoiceAnim.setVisibility(View.GONE);
	}

	public void setBtnRecordEnable(boolean flag) {
		mBtnRecord.setEnabled(flag);
	}

	public void setBtnVoiceAnimEnable(boolean flag) {
		imageNoVoiceAnim.setClickable(flag);
		//btn_bg.setClickable(flag);
	}

	public void setBtnRecordBg(int index) {
		// btn_bg.setBackgroundResource(recordBgArr[index]);
		// mBtnRecord.setBackgroundResource(recordViewArr[index]);
		imageNoVoiceAnim.setBackgroundResource(recordViewArr[index]);
	}

	public void setTextStatus(int flag) {
		// text_status.setText(txt);
		LogInfo.LogOutE("haitian", "flag =" + flag);
		if (EngineResultFlag.NOTHING == flag) {
			Util.showToast(mContext, R_String.lbl_speak_nothing);
		} else {
			Util.showToast(mContext, "(" + flag + ")" + R_String.lbl_net_error);
		}
		if (isHaveView) {
			setRecordLayoutDisp(View.GONE);
		} else {
			dismissPopWindow();
		}
	}

	public void setTextStatusDisp(int visible) {
		// text_status.setVisibility(visible);
	}

	public void setRecordLayoutDisp(int visible) {
		record_layout.setVisibility(visible);
	}

	public void setBtnCancelDisp(int visible) {
		btnCancel.setVisibility(visible);
	}

	public void showPopWindowLocation() {
		mRecognizerWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}

	public void dismissPopWindow() {
		mRecognizerWindow.dismiss();
	}
	// =============================================================================================================
	// =============================================================================================================

}
