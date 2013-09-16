package com.ztspeech.simutalk2.weibo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Window;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.weibo.sdk.renren.AsyncRenren;
import com.ztspeech.weibo.sdk.renren.Renren;
import com.ztspeech.weibo.sdk.renren.common.AbstractRequestListener;
import com.ztspeech.weibo.sdk.renren.exception.RenrenAuthError;
import com.ztspeech.weibo.sdk.renren.exception.RenrenError;
import com.ztspeech.weibo.sdk.renren.feed.FeedPublishRequestParam;
import com.ztspeech.weibo.sdk.renren.feed.FeedPublishResponseBean;
import com.ztspeech.weibo.sdk.renren.status.StatusSetResponseBean;
import com.ztspeech.weibo.sdk.renren.view.RenrenAuthListener;

public class IRenren {

	// private Context context;
	private static final String API_KEY = "978e1e68cbe74d81aa178a8aa487ffdf";
	private static final String SECRET_KEY = "2eb96ee32f0147159a117ca7c86e96be ";
	private static final String APP_ID = "222082 ";
	private static final String[] DEFAULT_PERMISSIONS = { "publish_feed", "status_update" };
	private static Renren renren;

	private ProgressDialog progress;

	private static IRenren iRenren = null;
	private Handler handler;
	private Context mContext;
	private Handler mHandler;
	private String contentStr = null;

	public IRenren(Context context) {
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
					Util.showToast(mContext, "��Ȩ�ɹ�");
					break;
				case 201:
					Util.showToast(mContext, (String) msg.obj);
					break;
				case 202:
					if(contentStr==null){
						Util.showToast(mContext, (String) msg.obj);
					}else{
						sendWeibo(contentStr, mContext);
					}
					break;
				case 400:
					Util.showToast(mContext, "��Ȩʧ��");
					break;
				case 401:
					Util.showToast(mContext, (String) msg.obj);
					break;
				default:
					break;
				}
			}
		};
	}

	public void setClass() {
		iRenren = null;
	}

	public synchronized static IRenren getInstance(Context context) {
		if (iRenren == null) {
			iRenren = new IRenren(context);
		}
		return iRenren;
	}

	public void init(Context context) {
		renren = new Renren(API_KEY, SECRET_KEY, APP_ID, context);
	}

	public boolean isBinder() {
		return renren.isAccessTokenValid();
	}

	public boolean unBinderRenren(Context context) {
		if (renren.isAccessTokenValid()) {
			renren.logout(context);
		}
		return true;
	}

	// ��Ȩ
	public void bindRenren(Context context, Handler _mHandler) {
		mContext = context;
		setHandler(mContext);
		mHandler = _mHandler;
		if (renren.isAccessTokenValid()) {
			renren.logout(context);
		}
		renren.authorize((Activity) context, DEFAULT_PERMISSIONS, rrAuthListener);
	}

	// ��״̬
	public void sendWeibo(String contentStr, Context context) {
		mContext = context;
		setHandler(mContext);
		if (renren != null) {
			if (renren.isAccessTokenValid()) {
				progress = new ProgressDialog(mContext, R.style.mydialog);
				progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
				progress.setMessage("���ڷ��������Ժ�");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				// StatusSetRequestParam param = new
				// StatusSetRequestParam(contentStr);
				// StatusSetListener listener = new StatusSetListener(mContext);
				RenrenInfo mRenrenInfo = new RenrenInfo();
				mRenrenInfo.description = contentStr;
				FeedPublishRequestParam param = new FeedPublishRequestParam(mRenrenInfo.name, mRenrenInfo.description,
						mRenrenInfo.url, mRenrenInfo.imageUrl, mRenrenInfo.caption, mRenrenInfo.actionName,
						mRenrenInfo.actionLink, mRenrenInfo.description);
				LogInfo.LogOutE("haitian", mRenrenInfo.description);
				FeedPublishListener listener = new FeedPublishListener(mContext);
				try {
					AsyncRenren aRenren = new AsyncRenren(renren);
					// aRenren.publishStatus(param, listener, // �Խ�����м���
					// true); // ������140�ַ������Զ��ض�
					aRenren.publishFeed(param, listener, // �Խ�����м���
							true); // ������140�ַ������Զ��ض�
				} catch (Throwable e) {
					String errorMsg = e.getMessage();
					handler.sendMessage(Message.obtain(handler, 401, errorMsg));
				}
			} else {
				renren.authorize((Activity) mContext, DEFAULT_PERMISSIONS, rrSendAuthListener);
			}
		}
	}

	// ��token
	public void saveStatus(int requestCode, int resultCode, Intent data) {
		if (renren != null) {
			renren.authorizeCallback(requestCode, resultCode, data);
		}
	}

	private RenrenAuthListener rrAuthListener = new RenrenAuthListener() {

		@Override
		public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
			// ��Ȩʧ��
			handler.sendEmptyMessage(400);
			if (mHandler != null) {
				mHandler.sendEmptyMessage(201);
				mHandler = null;
			}
		}

		@Override
		public void onComplete(Bundle values) {
			// ��Ȩ�ɹ���Ӧ����
			handler.sendEmptyMessage(200);
			if (mHandler != null) {
				mHandler.sendEmptyMessage(201);
				mHandler = null;
			}
		}

		@Override
		public void onCancelLogin() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancelAuth(Bundle values) {
			// TODO Auto-generated method stub

		}
	};
	private RenrenAuthListener rrSendAuthListener = new RenrenAuthListener() {

		@Override
		public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
			// ��Ȩʧ��
			handler.sendEmptyMessage(400);
			if (mHandler != null) {
				mHandler.sendEmptyMessage(201);
				mHandler = null;
			}
		}

		@Override
		public void onComplete(Bundle values) {
			// ��Ȩ�ɹ���Ӧ����
			handler.sendEmptyMessage(202);
			if (mHandler != null) {
				mHandler.sendEmptyMessage(201);
				mHandler = null;
			}
		}

		@Override
		public void onCancelLogin() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancelAuth(Bundle values) {
			// TODO Auto-generated method stub

		}
	};

	private class StatusSetListener extends AbstractRequestListener<StatusSetResponseBean> {

		private Context context;

		private Handler handler;

		public StatusSetListener(Context context) {
			this.context = context;
			Looper mainLooper = context.getMainLooper();
			this.handler = new Handler(mainLooper) {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 200:
						Util.showToast(mContext, "��Ȩ�ɹ�");
						break;
					case 201:
						Util.showToast(mContext, (String) msg.obj);
						break;
					case 400:
						Util.showToast(mContext, "��Ȩʧ��");
						break;
					case 401:
						Util.showToast(mContext, (String) msg.obj);
						break;
					default:
						break;
					}
				}
			};
		}

		@Override
		public void onRenrenError(RenrenError renrenError) {
			final int errorCode = renrenError.getErrorCode();
			final String errorMsg = renrenError.getMessage();
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
				}
			});
			if (errorCode == RenrenError.ERROR_CODE_OPERATION_CANCELLED) {
				handler.sendMessage(Message.obtain(handler, 201, "���ͱ�ȡ��"));
			} else {
				handler.sendMessage(Message.obtain(handler, 401, "����ʧ��"));
			}
		}

		@Override
		public void onFault(Throwable fault) {
			final String errorMsg = fault.toString();
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
				}
			});
			handler.sendMessage(Message.obtain(handler, 401, "����ʧ��"));
		}

		@Override
		public void onComplete(StatusSetResponseBean bean) {
			final String responseStr = bean.toString();
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
				}
			});
			handler.sendMessage(Message.obtain(handler, 201, "���ͳɹ�"));
		}
	}

	private class FeedPublishListener extends AbstractRequestListener<FeedPublishResponseBean> {

		private Context context;

		private Handler handler;

		public FeedPublishListener(Context context) {
			this.context = context;
			Looper mainLooper = context.getMainLooper();
			this.handler = new Handler(mainLooper) {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 200:
						Util.showToast(mContext, "��Ȩ�ɹ�");
						break;
					case 201:
						Util.showToast(mContext, (String) msg.obj);
						contentStr = null;
						break;
					case 400:
						Util.showToast(mContext, "��Ȩʧ��");
						break;
					case 401:
						Util.showToast(mContext, (String) msg.obj);
						break;
					default:
						break;
					}
				}
			};
		}

		@Override
		public void onRenrenError(RenrenError renrenError) {
			final int errorCode = renrenError.getErrorCode();
			final String errorMsg = renrenError.getMessage();
			LogInfo.LogOutE("haitian", "onRenrenError = " + errorMsg);
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
				}
			});
			if (errorCode == RenrenError.ERROR_CODE_OPERATION_CANCELLED) {
				handler.sendMessage(Message.obtain(handler, 201, "���ͱ�ȡ��"));
			} else {
				handler.sendMessage(Message.obtain(handler, 401, "����ʧ��"));
			}
		}

		@Override
		public void onFault(Throwable fault) {
			final String errorMsg = fault.toString();
			LogInfo.LogOutE("haitian", "onFault = " + errorMsg);
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
				}
			});
			handler.sendMessage(Message.obtain(handler, 401, "����ʧ��"));
		}

		@Override
		public void onComplete(FeedPublishResponseBean bean) {
			LogInfo.LogOutE("haitian", "onComplete = " + bean.toString());
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (context != null) {
						if (progress != null) {
							progress.dismiss();
						}
					}
				}
			});
			handler.sendMessage(Message.obtain(handler, 201, "���ͳɹ�"));

		}
	}

	class RenrenInfo {
		/**
		 * �����±��⣬���30���ַ���Ϊ�������
		 */
		public String name = "�϶�����";

		/**
		 * �������������ݣ����200���ַ���Ϊ�������
		 */
		public String description;

		/**
		 * �����±����ͼƬָ������ӣ�Ϊ�������
		 */
		public String url = "http://www.ztspeech.com";

		/**
		 * ������ͼƬ��ַ��Ϊ��ѡ����
		 */
		public String imageUrl = "";

		/**
		 * �����¸����⣬���20���ַ���Ϊ��ѡ����
		 */
		public String caption = "";

		/**
		 * �����¶���ģ���İ������20���ַ���Ϊ��ѡ����
		 */
		public String actionName = "";

		/**
		 * �����¶���ģ�����ӣ�Ϊ��ѡ����
		 */
		public String actionLink = "";

		/**
		 * �û�������Զ������ݣ����200���ַ���Ϊ��ѡ����
		 */
		public String message = "�϶�����,�����ĵ�������������";
	}
}
