package com.ztspeech.simutalk2.trans;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.JsonSetUserInfo;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;

public class SetNickNameActivity extends BaseActivity {
	private static final String TAG = "SetNickNameActivity";
	private UserInfo mUser = UserInfo.getInstanse();
	private Context context;
	private InterpretActivity mMain = null;
	private EditText nickNameEditText;
	private Button saveBtn;
	private String username;
	private PostPackageEngine mPostPackageEngine;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				postPackageCallBack((ResultPackage) msg.obj);
				break;
			case 404:
				nickNameEditText.setText(mUser.getUserName());
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}

		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.setting_user_nickname, "设置昵称");
		context = this;
		nickNameEditText = (EditText) findViewById(R.id.nickNameEdite);
		nickNameEditText.setText(mUser.getUserName());
		saveBtn = (Button) findViewById(R.id.saveBtn);
		mMain = InterpretActivity.getInstance();
	}

	public void saveNickName(View v) {
		String sName = nickNameEditText.getEditableText().toString().trim();
		JsonSetUserInfo info = new JsonSetUserInfo();
		info.setUserName(sName);
		username = sName;

		mPostPackageEngine = new PostPackageEngine(context, info, handler);
		mPostPackageEngine.post();
	}

	@Override
	protected void onPause() {
		mUser.save();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mUser.save();
		super.onDestroy();
	}

	private void postPackageCallBack(ResultPackage result) {
		if (result.isNetSucceed()) {

			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (ret != null) {
				if (ret.succeed == true) {
					mMain.setUserName(username);
					mUser.save();
					Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
					this.finish();
				} else {
					nickNameEditText.setText(mUser.getUserName());
					Toast.makeText(context, ret.explain, Toast.LENGTH_SHORT).show();
				}
			} else {
				nickNameEditText.setText(mUser.getUserName());
				Toast.makeText(context, "昵称修改失败！", Toast.LENGTH_SHORT).show();
			}
		} else {
			nickNameEditText.setText(mUser.getUserName());
		}
	}
}
