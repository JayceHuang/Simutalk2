/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ztspeech.weibo.sdk.kaixin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ztspeech.simutalk2.R;

/**
 * 为开心对话框实现WebView的类。 请求指定URL，并显示在WebView控件上。 开发者不会直接使用该类。
 */
public class KaixinDialog extends Dialog {

	private static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
	private static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	private static final String DIALOG_LOADING = "正在载入...";

	private String mUrl;
	private KaixinDialogListener mListener;
	private ProgressDialog mProgress;
	private WebView mWebView;
	private LinearLayout mContent;

	public KaixinDialog(Context context, String url, KaixinDialogListener listener) {
		super(context);
		mUrl = url;
		mListener = listener;
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
		mWebView.setWebViewClient(new KaixinDialog.KaixinWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(mUrl);
		FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		mWebView.setLayoutParams(fill);
		mContent.addView(mWebView);
	}

	private class KaixinWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			int b = mListener.onPageBegin(url);
			switch (b) {
			case KaixinDialogListener.PROCCESSED:
				KaixinDialog.this.dismiss();
				return true;
			case KaixinDialogListener.DIALOG_PROCCESS:
				return false;
			}
			getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			boolean b = mListener.onPageStart(url);
			if (b) {
				view.stopLoading();
				KaixinDialog.this.dismiss();
				return;
			}
			super.onPageStarted(view, url, favicon);
			mProgress.show();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onReceivedError(errorCode, description, failingUrl);
			mProgress.dismiss();
			KaixinDialog.this.dismiss();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mListener.onPageFinished(url);
			mProgress.dismiss();
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}
	}
}
