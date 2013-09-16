package com.ztspeech.simutalk2.weibo;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.qa.MainActivity;
import com.ztspeech.simutalk2.trans.InterpretActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditContent extends Activity implements OnClickListener{

	private EditText edtContent;
	private Button btnSend;
	private Button btnCancle;
	private TextView tvCount;
	private int count = 140;
	private Intent intent;
	private int action = 0;
	private String str = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pop_editweibocontent);
		intent = getIntent();
		action = intent.getIntExtra("action", 0);
		str = intent.getStringExtra("content");
		edtContent = (EditText) findViewById(R.id.edtContent);
		btnSend = (Button) findViewById(R.id.btnSend);
		btnCancle = (Button) findViewById(R.id.btnCancle);
		tvCount = (TextView) findViewById(R.id.tvCount);
		btnSend.setOnClickListener(this);
		btnCancle.setOnClickListener(this);
		edtContent.setText(str);
		//tvCount.setText((count-str.length())+"/"+count);
		tvCount.setText("还可以输入"+(count-str.length())+"字");
		edtContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//tvCount.setText((count-edtContent.getText().toString().length())+"/"+count);
				tvCount.setText("还可以输入"+(count-edtContent.getText().toString().length())+"字");
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == btnSend){
			if(MainActivity.iTencent.isBind()||
					MainActivity.iSina.isBind()||
					MainActivity.iRenren.isBinder()||
					MainActivity.ikaixin.isBinder()){
				Intent intentR = new Intent(EditContent.this,intent.getClass());
				intentR.putExtra("content", edtContent.getText().toString().trim());
				intentR.putExtra("isBind", "yes");
				intentR.putExtra("action", action);
				EditContent.this.setResult(000011, intentR);
				finish();
			}else{
				Intent intentR = new Intent(EditContent.this,intent.getClass());
				intentR.putExtra("content", edtContent.getText().toString().trim());
				intentR.putExtra("isBind", "no");
				intentR.putExtra("action", action);
				EditContent.this.setResult(000011, intentR);
				finish();
			}
			
		}else if(v == btnCancle){
			finish();
		}
	}
}
