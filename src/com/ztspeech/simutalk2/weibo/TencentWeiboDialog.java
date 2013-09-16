package com.ztspeech.simutalk2.weibo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.weibo.oauthv1.OAuthV1;
import com.ztspeech.simutalk2.R;
import com.ztspeech.weibo.sdk.kaixin.KaixinDialogListener;

/**
 * 用来显示用户认证界面的dialog，封装了一个webview，通过redirect地址中的参数来获取accesstoken
 * 
 * @author xiaowei6@staff.sina.com.cn
 * 
 */
public class TencentWeiboDialog extends Dialog {

	private static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
	private static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	private static final String DIALOG_LOADING = "正在载入...";

	private String mUrl;
	private KaixinDialogListener mListener;
	private ProgressDialog mProgress;
	private WebView mWebView;
	private LinearLayout mContent;
	private Context context;
	private Handler handler;
	private OAuthV1 oAuth;

	public TencentWeiboDialog(Context context, String mUrl, OAuthV1 oAuth, Handler handler) {
		super(context);
		this.context = context;
		this.mUrl = mUrl;
		this.oAuth = oAuth;
		this.handler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgress = new ProgressDialog(getContext(), R.style.mydialog);
		mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mProgress.setMessage(DIALOG_LOADING);
		mProgress.setCanceledOnTouchOutside(false);
		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setUpWebView();

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		float scale = getContext().getResources().getDisplayMetrics().density;
		float[] dimensions = display.getWidth() < display.getHeight() ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;
		addContentView(mContent, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f),
				(int) (dimensions[1] * scale + 0.5f)));
	}

	private void setUpWebView() {
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new TencentWeiboDialog.WeiboWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.requestFocus();
				return false;
			}
		});
		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.requestFocus();
				return false;
			}
		});
		FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		mWebView.setLayoutParams(fill);
		mContent.addView(mWebView);
	}

	protected void onBack() {
		try {
			mProgress.dismiss();
			if (null != mWebView) {
				mWebView.stopLoading();
				mWebView.destroy();
			}
		} catch (Exception e) {
		}
		dismiss();
	}

	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			TencentWeiboDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// Log.e(TAG, "WebView onPageStarted...");
			// Log.e(TAG, "URL = " + url);
			if (url != null) {
				if (url.indexOf("checkType=verifycode") != -1) {
					int start = url.indexOf("checkType=verifycode&v=") + 23;
					String verifyCode = url.substring(start, start + 6);
					oAuth.setOauthVerifier(verifyCode);
					view.destroyDrawingCache();
					handler.sendMessage(Message.obtain(handler, 200, oAuth));
					onBack();
				} else {
					mProgress.show();
				}
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// Log.d(TAG, "onPageFinished URL: " + url);
			super.onPageFinished(view, url);
			if (mProgress.isShowing()) {
				mProgress.dismiss();
			}
			mWebView.setVisibility(View.VISIBLE);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

	}
}
