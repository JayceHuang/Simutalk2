package com.ztspeech.simutalk2.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;

public abstract class HttpBaseEngine {
	public Context context;
	public HttpRequest httpRequest;
	public HttpPost httpPost;
	public ProgressDialog loadingDialog;
	private byte[] data = null;
	public boolean isCancel = false;
	private View view;
	private Animation anim;

	public HttpBaseEngine(Context context) {
		this.context = context;
		anim = new RotateAnimation(0, +3600, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		anim.setDuration(15000);
		anim.setFillAfter(true);
		anim.setRepeatCount(Animation.INFINITE);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		view = LayoutInflater.from(this.context).inflate(R.layout.dialog_layout_view, null);
	}

	/**
	 * 发起http线程内请求，默认30秒超时，最多服务器重试请求两次
	 * 
	 * @param url
	 */
	public void httpRequestNewThread(String id, String type) {
		httpRequest = new HttpRequest();
		isCancel = false;
		httpRequest.execute(id, type);
	}

	/**
	 * 发起http线程内请求，默认30秒超时，最多服务器重试请求两次
	 * 
	 * @param url
	 */
	public void httpRequestPostNewThread(byte[] data, String type) {
		this.data = data;
		isCancel = false;
		httpPost = new HttpPost();
		httpPost.execute("xxx", type);
	}

	public void httpRequestPostNewThread(byte[] data, String id, String type) {
		this.data = data;
		isCancel = false;
		httpPost = new HttpPost();
		httpPost.execute(id, type);
	}

	class HttpRequest extends AsyncTask<String, Void, byte[]> {
		boolean isStop = false;

		@Override
		protected void onPreExecute() {
			onPreHttp();
		}

		@Override
		protected byte[] doInBackground(String... params) {
			byte[] ret = null;
			String id = params[0];
			ret = httpRequestThisThread(id, params[1]);
			if (isStop) {
				return null;
			}
			onParseHttp(ret);
			return ret;
		}

		public void stop() {
			isStop = true;
			cancel(true);
		}

		@Override
		protected void onPostExecute(byte[] result) {
			if (!isStop) {
				onPostHttp(result);
			}

		}
	}

	class HttpPost extends AsyncTask<String, Void, String> {
		boolean isStop = false;

		@Override
		protected void onPreExecute() {
			onPreHttp();
		}

		@Override
		protected String doInBackground(String... params) {
			String ret = null;
			String id = params[0];
			ret = httpRequestPostThread(data, id, params[1]);
			if (isStop) {
				return null;
			}
			onParseHttp(ret);
			return ret;
		}

		public void stop() {
			isStop = true;
			cancel(true);
		}

		@Override
		protected void onPostExecute(String result) {
			if (!isStop) {
				onPostHttp(result);
			}
		}
	}

	/**
	 * 默认30秒超时，最多主服务器和备用服务器各重试请求两次
	 */
	public byte[] httpRequestThisThread(String id, String type) {
		byte[] ret = null;
		ret = HttpUtils.getServerString(context, id, type);
		return ret;
	}

	/**
	 * 默认30秒超时，最多主服务器和备用服务器各重试请求两次
	 */
	public String httpRequestPostThread(byte[] data, String id, String type) {
		String ret = null;
		ret = HttpUtils.postServerString(context, id, type, data);
		return ret;
	}

	/**
	 * 在http线程内请求之前UI线程调用，一般写弹出提示代码
	 */
	public void onPreHttp() {
	};

	/**
	 * 在http线程内请求之后子线程调用，一般写解析代码
	 */
	public void onParseHttp(byte[] response) {
	};

	/**
	 * 在http线程内请求之后子线程调用，一般写解析代码
	 */
	public void onParseHttp(String response) {
	};

	/**
	 * 在http线程内请求之后UI线程调用，一般写取消提示代码
	 */
	public void onPostHttp(Object result) {
	};

	/**
	 * 取消http线程内请求
	 */
	public void cancelRequest() {
		isCancel = true;
		if (httpRequest != null) {
			httpRequest.stop();
			httpRequest = null;
			onPostHttp(null);
		}
		if (httpPost != null) {
			httpPost.stop();
			httpPost = null;
			onPostHttp(null);
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
