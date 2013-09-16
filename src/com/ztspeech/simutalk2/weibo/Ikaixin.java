package com.ztspeech.simutalk2.weibo;

import java.io.InputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.weibo.sdk.kaixin.Kaixin;
import com.ztspeech.weibo.sdk.kaixin.KaixinAuthError;
import com.ztspeech.weibo.sdk.kaixin.KaixinAuthListener;
import com.ztspeech.weibo.sdk.kaixin.KaixinError;

public class Ikaixin {

	// private Context context;
	private static final String API_KEY = "846977132847356f74f09ddd08925e42";
	private static final String SECRET_KEY = "186660258ac3bbd70a9738ab1e90a332 ";
	private static final String APP_ID = "100046250 ";
	private static final String[] DEFAULT_PERMISSIONS = { "basic", "create_records" };
	private static Kaixin kaixin;
	private static final int LOGINERROR = 1001;
	private ProgressDialog progress;

	private static Ikaixin iKaixin = null;
	private Handler handler;
	private Context mContext;
	private Handler mHandler;

	public Ikaixin(Context context) {
		mContext = context;
		setHandler(mContext);
	}

	private void setHandler(Context context) {
		Looper mainLooper = context.getMainLooper();
		handler = new Handler(mainLooper) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 200:
					Util.showToast(mContext, "授权成功");
					break;
				case 201:
					Util.showToast(mContext, (String) msg.obj);
					break;
				case 400:
					Util.showToast(mContext, "授权失败");
					break;
				case 401:
					Util.showToast(mContext, (String) msg.obj);
					break;
				case Constant.RESULT_POST_RECORD_OK:
					Util.showToast(mContext, R.string.post_record_success);
					break;
				case LOGINERROR:
					Util.showToast(mContext, (String) msg.obj);
					break;
				case Constant.RESULT_POST_RECORD_FAILED:
					Util.showToast(mContext, R.string.post_record_fail);
					break;
				// 网络出错请重试
				case Constant.RESULT_FAILED_NETWORK_ERR:
					Util.showToast(mContext, R.string.task_failed_network_err);
					break;
				// JSON解析错误，请检查代码
				case Constant.RESULT_FAILED_JSON_PARSE_ERR:
					Util.showToast(mContext, R.string.task_failed_json_parse_err);
					break;
				// 传入的参数错误，请检查代码
				case Constant.RESULT_FAILED_ARG_ERR:
					Util.showToast(mContext, R.string.task_failed_arg_err);
					break;
				// 错误的 URL,请检查请求的URL
				case Constant.RESULT_FAILED_MALFORMEDURL_ERR:
					Util.showToast(mContext, R.string.task_failed_malformed_url_err);
					break;
				// 字符串转换失败，请检查代码
				case Constant.RESULT_FAILED_ENCODER_ERR:
					Util.showToast(mContext, R.string.task_failed_encoder_err);
					break;
				// 请求的任务失败
				case Constant.RESULT_FAILED:
					Util.showToast(mContext, R.string.task_failed);
					break;
				case Constant.RESULT_FAILED_REQUEST_ERR:
					KaixinError kaixinError = (KaixinError) msg.obj;
					Util.showToast(mContext, kaixinError.toString());
					break;
				case Constant.RESULT_USER_CANCEL_PROCESS:
					Util.showToast(mContext, R.string.cancelnet);
					break;
				default:
					break;
				}
			}
		};
	}

	public void setClass() {
		iKaixin = null;
	}

	public synchronized static Ikaixin getInstance(Context context) {
		if (iKaixin == null) {
			iKaixin = new Ikaixin(context);
		}
		return iKaixin;
	}

	public void init(Context context) {
		kaixin = Kaixin.getInstance();
		kaixin.loadStorage(context);
	}

	public boolean isBinder() {
		return kaixin.isBinder(mContext);
	}

	/**
	 * 取消授权
	 * 
	 * @param context
	 */
	public void unbinderKaixin(Context context) {
		kaixin.setAccessToken(null);
		kaixin.setRefreshToken(null);
		kaixin.setAccessExpires(0);
		kaixin.updateStorage(context);
	}

	// 授权
	public void bindKaixin(Context context, Handler _mHandler) {
		mContext = context;
		setHandler(mContext);
		mHandler = _mHandler;
		/**
		 * 完成登录并获取access_token(User-Agent Flow方式)
		 */
		kaixin.authorize(mContext, DEFAULT_PERMISSIONS, authBinderListener);

	}

	private PostRecordTask getDataTask;
	private String contentString;

	// 发状态
	public void sendWeibo(String contentStr, Context context) {
		mContext = context;
		setHandler(mContext);
		contentString = contentStr;
		if (kaixin != null) {
			if (kaixin.isSessionValid()) {
				if (contentStr.length() == 0) {
					Util.showToast(mContext, R.string.post_record_empty_content);
					return;
				}
				InputStream in = mContext.getResources().openRawResource(R.raw.photo1);
				getDataTask = new PostRecordTask(context);
				getDataTask.execute(kaixin, handler, contentStr, in);
			} else {
				/**
				 * 完成登录并获取access_token(User-Agent Flow方式)
				 */
				kaixin.authorize(mContext, DEFAULT_PERMISSIONS, authListener);

			}
		}
	}

	KaixinAuthListener authListener = new KaixinAuthListener() {
		@Override
		public void onAuthCancel(Bundle values) {
			LogInfo.LogOutE("haitian", "onAuthCancel");
		}

		@Override
		public void onAuthCancelLogin() {
			LogInfo.LogOutE("haitian", "onAuthCancelLogin");
		}

		/**
		 * 授权完成
		 * 
		 * @param values
		 *            授权服务器返回的key-value形式的参数集，key为参数名，value为参数值
		 */

		@Override
		public void onAuthComplete(Bundle values) {
			if (contentString.length() == 0) {
				Util.showToast(mContext, R.string.post_record_empty_content);
				return;
			}
			InputStream in = mContext.getResources().openRawResource(R.raw.photo1);
			getDataTask = new PostRecordTask(mContext);
			getDataTask.execute(kaixin, handler, contentString, in);
		}

		@Override
		public void onAuthError(KaixinAuthError kaixinAuthError) {
			LogInfo.LogOutE("haitian", "onAuthError");
			Message msg = Message.obtain();
			msg.obj = kaixinAuthError.getErrorDescription();
			msg.what = LOGINERROR;
			handler.sendMessage(msg);

		}
	};
	KaixinAuthListener authBinderListener = new KaixinAuthListener() {
		@Override
		public void onAuthCancel(Bundle values) {
			LogInfo.LogOutE("haitian", "onAuthCancel");
		}

		@Override
		public void onAuthCancelLogin() {
			LogInfo.LogOutE("haitian", "onAuthCancelLogin");
		}

		/**
		 * 授权完成
		 * 
		 * @param values
		 *            授权服务器返回的key-value形式的参数集，key为参数名，value为参数值
		 */

		@Override
		public void onAuthComplete(Bundle values) {
			handler.sendEmptyMessage(200);
			if (mHandler != null) {
				mHandler.sendEmptyMessage(201);
				mHandler = null;
			}
		}

		@Override
		public void onAuthError(KaixinAuthError kaixinAuthError) {
			LogInfo.LogOutE("haitian", "onAuthError");
			Message msg = Message.obtain();
			msg.obj = kaixinAuthError.getErrorDescription();
			msg.what = LOGINERROR;
			handler.sendMessage(msg);

		}
	};
}
