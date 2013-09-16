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
	 * ����http�߳�������Ĭ��30�볬ʱ����������������������
	 * 
	 * @param url
	 */
	public void httpRequestNewThread(String id, String type) {
		httpRequest = new HttpRequest();
		isCancel = false;
		httpRequest.execute(id, type);
	}

	/**
	 * ����http�߳�������Ĭ��30�볬ʱ����������������������
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
	 * Ĭ��30�볬ʱ��������������ͱ��÷�������������������
	 */
	public byte[] httpRequestThisThread(String id, String type) {
		byte[] ret = null;
		ret = HttpUtils.getServerString(context, id, type);
		return ret;
	}

	/**
	 * Ĭ��30�볬ʱ��������������ͱ��÷�������������������
	 */
	public String httpRequestPostThread(byte[] data, String id, String type) {
		String ret = null;
		ret = HttpUtils.postServerString(context, id, type, data);
		return ret;
	}

	/**
	 * ��http�߳�������֮ǰUI�̵߳��ã�һ��д������ʾ����
	 */
	public void onPreHttp() {
	};

	/**
	 * ��http�߳�������֮�����̵߳��ã�һ��д��������
	 */
	public void onParseHttp(byte[] response) {
	};

	/**
	 * ��http�߳�������֮�����̵߳��ã�һ��д��������
	 */
	public void onParseHttp(String response) {
	};

	/**
	 * ��http�߳�������֮��UI�̵߳��ã�һ��дȡ����ʾ����
	 */
	public void onPostHttp(Object result) {
	};

	/**
	 * ȡ��http�߳�������
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
	 * �������ڻ�ȡ���ݵĵ�������ʾ
	 */
	public void dismissLoading() {
		view.findViewById(R.id.loadinganim).clearAnimation();
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			// loadingDialog = null;
		}
	}

	/**
	 * �ֶ�ȡ�����ڻ�ȡ���ݵĵ�������ʾʱ�Ļص�����
	 */
	public void onLoadingCacel() {
		cancelRequest();
		dismissLoading();
	}

	public void showLoading() {
		/**
		 * �ȴ������ʼ�� �е��ֻ���new������ʾ����
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
