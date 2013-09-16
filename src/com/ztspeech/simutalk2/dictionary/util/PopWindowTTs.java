package com.ztspeech.simutalk2.dictionary.util;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.qa.WaitingActivity.OnWaitingListener;

public class PopWindowTTs {

	public Context context;
	public boolean isCancel = false;
	private LayoutInflater inflater;
	private View v;
	private PopupWindow mRecognizerWindow;
	private ImageView waitImageView;
	private AnimationDrawable waiteAnimation;
	private Button btn_cancel;
	@SuppressWarnings("unused")
	private boolean flag = false;
	private OnWaitingListener mOnWaitingListener;

	public PopWindowTTs(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = inflater.inflate(R.layout.pop_recognizer_view, null);
		mRecognizerWindow = new PopupWindow(v, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mRecognizerWindow.setFocusable(true);
		mRecognizerWindow.setOutsideTouchable(false);
		// mRecognizerWindow.setBackgroundDrawable(new BitmapDrawable());
		// mRecognizerWindow.setOnDismissListener(onDismissListener);
		waitImageView = (ImageView) v.findViewById(R.id.imageviewanim);
		waitImageView.setBackgroundResource(R.drawable.recognizer_wait_anim);
		waiteAnimation = (AnimationDrawable) waitImageView.getBackground();
		btn_cancel = (Button) v.findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// dismissPopWindow();
				if (mOnWaitingListener != null) {
					mOnWaitingListener.onCancel();
				} else {
					dismissPopWindow();
				}
			}
		});
	}

	public void showLoading() {
		startProgressDialog();
	}

	public void stopProgressDialog() {
		flag = true;
		dismissPopWindow();
	}

	private void startProgressDialog() {
		waiteAnimation.stop();
		mFirst = true;
		waitImageView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
		showPopWindowLocation();
	}

	private void showPopWindowLocation() {
		mRecognizerWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}

	public void setCancel(OnWaitingListener _mOnWaitingListener) {
		mOnWaitingListener = _mOnWaitingListener;
	}

	private void dismissPopWindow() {
		waiteAnimation.stop();
		mFirst = true;
		mRecognizerWindow.dismiss();
	}

	private boolean mFirst = true;
	private OnPreDrawListener mOnPreDrawListener = new OnPreDrawListener() {

		@Override
		public boolean onPreDraw() {
			if (mFirst) {
				waiteAnimation.start();
				mFirst = false;
			}
			return true;
		}

	};
	@SuppressWarnings("unused")
	private OnDismissListener onDismissListener = new OnDismissListener() {
		@Override
		public void onDismiss() {
			// if (!flag) {
			// dismissPopWindow();
			// }
			if (mOnWaitingListener != null) {
				mOnWaitingListener.onCancel();
			}
			flag = false;
		}
	};

}
