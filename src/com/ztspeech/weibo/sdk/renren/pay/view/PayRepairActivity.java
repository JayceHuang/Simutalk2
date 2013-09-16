package com.ztspeech.weibo.sdk.renren.pay.view;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.weibo.sdk.renren.pay.bean.PayOrder;
import com.ztspeech.weibo.sdk.renren.pay.impl.RenrenPay;

public class PayRepairActivity extends Activity {
	ListView lv = null;
	Button repairBtn = null;
	Button removeBtn = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renren_sdk_pay_repair);
		lv = (ListView) findViewById(R.id.renren_sdk_pay_repair_order_list);
		removeBtn = (Button) findViewById(R.id.renren_sdk_pay_repair_remove_all_button);
		removeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RenrenPay.getInstance().removeAllLocalInfo(PayRepairActivity.this);

			}
		});
		List<PayOrder> p = RenrenPay.getInstance().getStoredPayOrders(this);
		PayRepaiListAdapter adapter = new PayRepaiListAdapter(p);
		lv.setAdapter(adapter);
	}

}
