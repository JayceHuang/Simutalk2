package com.ztspeech.simutalk2.trans;

import com.ztspeech.simutalk2.data.UserInfo;

import com.ztspeech.simutalk2.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		String sName = mEditName.getText().toString().trim();
		if(sName.length() < 1){

			showAlert();
			return;
		}
		
    	mUser.setUserName(sName);
    	mUser.save();
		super.finish();
	}

	private UserInfo mUser = UserInfo.getInstanse();
	private Button mBtnOk = null;
	private EditText mEditName = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.login);

		mEditName = (EditText) findViewById(R.id.edit_user_name);
		mBtnOk = (Button) findViewById(R.id.btn_set_name);
		mBtnOk.setOnClickListener(this);
		
		mEditName.setText(mUser.getUserName());
	}
	
	private void showAlert(){

	     new  AlertDialog.Builder(this)  		 
	         .setTitle("提示")  		   
	         .setMessage("请输入用户名称！" )  		   
	         .setPositiveButton("好",  null )  		   
	         .show();  		
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_set_name: {
				finish();
			}
			break;
		}
	}
}
