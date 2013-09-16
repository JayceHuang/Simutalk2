package com.ztspeech.weibo.sdk.kaixin;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ztspeech.simutalk2.R;

/**
 * 
 * @author piu 2011-12-23 PopUpDialog.java
 * 
 */
public class PopUpDialog extends Dialog {

	private Context mContext;
	private ProgressDialog mProgressDialog;
	@SuppressWarnings("unused")
	private Handler mHandle;
	private LoginParameter mParams;
	private KaixinAuthListener mActivityListener;
	private AsyncKaixinListener mAsyncAuthListener = new AsyncKaixinListener() {

		@Override
		public void onRequestComplete(String response, Object obj) {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			mActivityListener.onAuthComplete(null);
		}

		@Override
		public void onRequestError(KaixinError kaixinError, Object obj) {
			// TODO Auto-generated method stub
			// String error, String errorDescription,String errorUri
			if (kaixinError != null) {
				mActivityListener.onAuthError(new KaixinAuthError(kaixinError.getMessage(), kaixinError.getMessage(),
						kaixinError.getRequest()));
			}
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
		}

		@Override
		public void onRequestNetError(Throwable fault, Object obj) {
			if (fault != null) {
				mActivityListener.onAuthError(new KaixinAuthError(fault.getMessage(), fault.getMessage(), null));
			}
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
		}
	};

	public PopUpDialog(Context context, final KaixinAuthListener listener) {
		super(context);
		mContext = context;
		mActivityListener = listener;
	}

	public PopUpDialog(Activity activity, LoginParameter param, final KaixinAuthListener listener) {
		super(activity);
		mActivityListener = listener;
		mContext = activity;
		this.mParams = param;
		this.mHandle = new Handler();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		final LinearLayout loginEntryView = (LinearLayout) getLayoutInflater().inflate(
				R.layout.kaixinloginwithoutwebview, null);
		loginEntryView.setOnClickListener(null);
		loginEntryView.setOnTouchListener(null);
		loginEntryView.setOnLongClickListener(null);
		loginEntryView.setOnKeyListener(null);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		initViews(loginEntryView);
		addContentView(loginEntryView, params);
	}

	private void initViews(LinearLayout loginEntryView) {
		final EditText userNameEditText = (EditText) loginEntryView.findViewById(R.id.kaixin_sdk_login_entry_username);
		final EditText passwordEditText = (EditText) loginEntryView.findViewById(R.id.kaixin_sdk_login_entry_password);
		Button button = (Button) loginEntryView.findViewById(R.id.kaixin_sdk_login_confirm_button);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String userName = userNameEditText.getText().toString();
				String password = passwordEditText.getText().toString();

				if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
					mParams.setUserName(userName);
					mParams.setPassword(password);
					login();
				}
			}
		});
		this.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mActivityListener != null) {
					mActivityListener.onAuthCancelLogin();
				}
			}
		});
	}

	private void login() {
		mProgressDialog = new ProgressDialog(getContext());
		mProgressDialog.setMessage("ÕýÔÚµÇÂ½");
		mProgressDialog.show();
		AsyncKaixin asynckaixin = new AsyncKaixin(Kaixin.getInstance());
		asynckaixin.login(mContext, mParams.getParams(), mAsyncAuthListener, null);
	}
}