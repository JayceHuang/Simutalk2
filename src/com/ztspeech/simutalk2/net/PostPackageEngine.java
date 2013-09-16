package com.ztspeech.simutalk2.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import cn.ac.ia.directtrans.json.JsonFunction;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.net.PostPackage.IHttpPostListener;
import com.ztspeech.simutalk2.qa.message.TipDialog;

public class PostPackageEngine implements IHttpPostListener {
	private Context context;
	private JsonFunction mJsonMessage;
	private PostPackage mPostPackage;

	public ProgressDialog loadingDialog;
	public boolean isCancel = false;
	private boolean isDestroy = false;
	private Handler handler;
	private View view;
	private Animation anim;

	public PostPackageEngine(Context context, JsonFunction mJsonMessage, Handler handler) {
		this.context = context;
		this.mJsonMessage = mJsonMessage;
		this.handler = handler;
		anim = new RotateAnimation(0, +3600, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(15000);
		anim.setFillAfter(true);
		anim.setRepeatCount(Animation.INFINITE);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		view = LayoutInflater.from(this.context).inflate(R.layout.dialog_layout_view, null);
	}
	
	public void setJson(JsonFunction mJsonMessage){
		this.mJsonMessage = mJsonMessage;
	}

	public void post() {
		LogInfo.LogOutE("PostPackageEngine", "PostPackageEngine--0");
		mPostPackage = new PostPackage(context, this);
		isDestroy = false;
		showLoading();
		mPostPackage.post(mJsonMessage, context.getString(R.string.host_ip), true);
	}

	public void post(boolean flag) {
		LogInfo.LogOutE("PostPackageEngine", "PostPackageEngine--0");
		mPostPackage = new PostPackage(context, this);
		isDestroy = flag;
		if (!flag) {
			showLoading();
		} else {
			loadingDialog = null;
		}
		mPostPackage.post(mJsonMessage, context.getString(R.string.host_ip), true);
	}

	@Override
	public void isShowTipDialog(String msg) {
		if (!isDestroy) {
			TipDialog.show(context, "提示", msg, "确定");
		}
	}

	@Override
	public void onNetPostResult(PostPackage owner, ResultPackage result) {
		if (!isDestroy) {
			dismissLoading();
		}
		if (isCancel) {
			LogInfo.LogOutE("PostPackageEngine", "PostPackageEngine--onNetPostResult---cancelRequest");
		} else {
			LogInfo.LogOutE("PostPackageEngine", "PostPackageEngine--onNetPostResult");
			handler.sendMessage(Message.obtain(handler, 200, result));
		}
	}

	/**
	 * 隐藏正在获取数据的弹出框提示
	 */
	public void dismissLoading() {
		view.findViewById(R.id.loadinganim).clearAnimation();
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			// loadingDialog = null;
		}
	}

	/**
	 * 取消http线程内请求
	 */
	public void cancelRequest() {
		isCancel = true;
		LogInfo.LogOutE("PostPackageEngine", "PostPackageEngine--cancelRequest");
		if (mPostPackage != null) {
			mPostPackage.cancel();
			mPostPackage = null;
		}
		if (mJsonMessage != null) {
			mJsonMessage = null;
		}
		handler.sendMessage(Message.obtain(handler, 404, context.getString(R.string.cancelnet)));
	}

	/**
	 * 手动取消正在获取数据的弹出框提示时的回调函数
	 */
	public void onLoadingCacel() {
		cancelRequest();
		dismissLoading();
	}

	public void showLoading() {
		/**
		 * 等待画面初始化 有的手机不new不能显示动画
		 */
		if (loadingDialog == null) {
			loadingDialog = new ProgressDialog(context);
			// loadingDialog.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.com_pop_wait));
			loadingDialog.setIndeterminate(true);
			loadingDialog.setCancelable(true);
			loadingDialog.setCanceledOnTouchOutside(false);
			loadingDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					onLoadingCacel();
				}
			});
		}
		isCancel = false;
		loadingDialog.show();
		loadingDialog.setContentView(view);
		view.findViewById(R.id.loadinganim).startAnimation(anim);
		((TextView) view.findViewById(R.id.tv_word)).setText(context.getString(R.string.loading));
		// if (loadingDialog == null) {
		// loadingDialog = new ProgressDialog(context, R.style.mydialog);
		// loadingDialog.setMessage(context.getString(R.string.loading));
		// loadingDialog.setIndeterminate(true);
		// loadingDialog.setCancelable(true);
		// loadingDialog.setOnCancelListener(new OnCancelListener() {
		// @Override
		// public void onCancel(DialogInterface dialog) {
		// onLoadingCacel();
		// }
		// });
		// }
		// isCancel = false;
		// loadingDialog.show();
	}
}
