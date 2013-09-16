package com.ztspeech.simutalk2.trans;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;

public class AboutActivity extends BaseActivity {

	private TextView tvAbout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.slideinright, R.anim.slideoutleft);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.activity_about, "关于紫冬口译");
		tvAbout = (TextView) findViewById(R.id.tvAbout);

		tvAbout.setText("紫冬口译\n版本号：" + getVersionName() + "\n\n" + "北京紫冬锐意\n语音科技有限公司\n版权所有\n\n"
				+ "Copyright (c) 2012, ZTSpeech \nCo. Ltd. All rights Reserved.\n");
	}

	public String getVersionName() {

		// 获取packagemanager的实例
		PackageManager packageManager = this.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			String name = this.getPackageName();
			packInfo = packageManager.getPackageInfo(name, 0);
			return packInfo.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
