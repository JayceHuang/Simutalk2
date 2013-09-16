package com.ztspeech.simutalk2.trans;

import android.os.Bundle;
import android.webkit.WebView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;

public class HelpActivity extends BaseActivity {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		overridePendingTransition(R.anim.slideinright, R.anim.slideoutleft);
		super.onCreate(savedInstanceState);
		String title = null;
		title = getString(R.string.help_title);

		setTitleAndContentView(R.layout.activity_help, title);
		setContentView(R.layout.activity_help);
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.loadUrl(getString(R.string.help_url));
	}
}
