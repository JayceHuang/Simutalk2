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
		setTitleAndContentView(R.layout.activity_about, "�����϶�����");
		tvAbout = (TextView) findViewById(R.id.tvAbout);

		tvAbout.setText("�϶�����\n�汾�ţ�" + getVersionName() + "\n\n" + "�����϶�����\n�����Ƽ����޹�˾\n��Ȩ����\n\n"
				+ "Copyright (c) 2012, ZTSpeech \nCo. Ltd. All rights Reserved.\n");
	}

	public String getVersionName() {

		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = this.getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
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
