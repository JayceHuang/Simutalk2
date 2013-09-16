package com.ztspeech.weibo.sdk.renren.pay;

import com.ztspeech.weibo.sdk.renren.exception.RenrenError;
import com.ztspeech.weibo.sdk.renren.pay.bean.PayOrder;
import com.ztspeech.weibo.sdk.renren.pay.bean.Payment;

public interface IPayListener {
	public void onStart(Payment o);

	public boolean onComplete(PayOrder o);

	public void onError(RenrenError error);
}
