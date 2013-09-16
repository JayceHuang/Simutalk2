package com.ztspeech.simutalk2.weibo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv1.OAuthV1Client;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class ITencent {
	private static String[] retArrs = { "分享成功", "参数错误", "频率受限", "鉴权失败", "服务器内部错误" };
	private static String[] errcodeArrs4 = { "分享成功", "", "", "", "有过多脏话", " 禁止访问", "该记录不存在", "", "内容超过最大长度：420字节",
			"包含垃圾信息", "发表太快，被频率限制", "源消息已删除", "源消息审核中", "重复发表", " 未实名认证" };
	private static String[] errcodeArrs3 = { "", "无效TOKEN,被吊销", "请求重放", "access_token不存在", "access_token超时",
			"oauth 版本不对", "oauth 签名方法不对", "参数错误", "处理失败", "验证签名失败", "网络错误", "参数长度不对", "处理失败 ", "处理失败", "处理失败", "处理失败" };

	private static ITencent iTencent = null;

	private static String APP_KEY = "801291349";
	private static String APP_SECRET = "5609263d58ef84286c519f955d3c08f6";
	public static int BIND_TENCENT_CODE = 1000;

	private SharedPreferences sp;
	private String token;
	private String token_secret;
	private Context mContext;
	private OAuthV1 oAuth = null;
	private boolean isNull = false;
	private Handler handler;
	private Handler mHandler;
	private String contentStr = null;

	public synchronized static ITencent getInstance(Context context) {
		if (iTencent == null) {
			iTencent = new ITencent(context);
		}
		return iTencent;

	}

	public void setClass() {
		iTencent = null;
	}

	public ITencent(Context context) {
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
					if (saveStatus((OAuthV1) msg.obj)) {
						if (contentStr == null) {
							Util.showToast(mContext, "授权成功");
						} else {
							sendWeibo(contentStr);
						}
					} else {
						Util.showToast(mContext, "授权失败");
					}
					if (mHandler != null) {
						mHandler.sendEmptyMessage(201);
						mHandler = null;
					}
					break;
				case 201:
					resultTecentWeibo((String) msg.obj);
					break;
				case 400:
					Util.showToast(mContext, "授权失败");
					break;
				case 401:
					Util.showToast(mContext, (String) msg.obj);
					break;
				case 402:
					break;
				default:
					break;
				}
			}
		};
	}

	public void init() {
		sp = mContext.getSharedPreferences("tencent", 0);
		String isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			isNull = false;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						oAuth = getOAuthV1();
						oAuth.setOauthToken(sp.getString("TOKEN_KEY", ""));
						oAuth.setOauthTokenSecret(sp.getString("TOKEN_SECRET_KEY", ""));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			isNull = true;
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						oAuth = getOAuthV1();
						String url = OAuthConstants.OAUTH_V1_AUTHORIZE_URL + "?oauth_token=" + oAuth.getOauthToken();
						sp.edit().putString("url", url).commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}

	public static OAuthV1 getOAuthV1() throws Exception {
		OAuthV1 oAuth = new OAuthV1("null");
		oAuth.setOauthConsumerKey(APP_KEY);
		oAuth.setOauthConsumerSecret(APP_SECRET);
		oAuth = OAuthV1Client.requestToken(oAuth);
		return oAuth;
	}

	public void bindTencent(Context context, Handler _mHandler) {
		mContext = context;
		setHandler(mContext);
		mHandler = _mHandler;
		String url = sp.getString("url", null);
		if (url != null && oAuth != null) {
			new TencentWeiboDialog(mContext, url, oAuth, handler).show();
		} else {
			try {
				oAuth = getOAuthV1();
				String mUrl = OAuthConstants.OAUTH_V1_AUTHORIZE_URL + "?oauth_token=" + oAuth.getOauthToken();
				new TencentWeiboDialog(mContext, mUrl, oAuth, handler).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void resultTecentWeibo(String resultMsg) {
		if (resultMsg != null) {
			Json json = new Json(resultMsg);
			int errcode = json.getInt("errcode");
			int ret = json.getInt("ret");
			if (ret == 0) {
				Util.showToast(mContext, retArrs[ret]);
			} else if (ret == 3) {
				if (errcodeArrs3.length > errcode) {
					Util.showToast(mContext, errcodeArrs3[errcode]);
				} else {
					Util.showToast(mContext, "微博分享失败！");
				}
			} else if (ret == 4) {
				if (errcodeArrs4.length > errcode) {
					Util.showToast(mContext, errcodeArrs4[errcode]);
				} else {
					Util.showToast(mContext, "微博分享失败！");
				}
			} else {
				if (retArrs.length > ret) {
					Util.showToast(mContext, retArrs[ret]);
				} else {
					Util.showToast(mContext, "微博分享失败！");
				}
			}
		} else {
			Util.showToast(mContext, "微博分享失败！");
		}
	}

	public void sendWeibo(final String contentStr) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TAPI tapi = new TAPI("1.0");
				String resultMsg = null;
				try {
					resultMsg = tapi.add(oAuth, "json", contentStr, "127.0.0.1");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					handler.sendMessage(Message.obtain(handler, 201, resultMsg));
				}

			}
		}).start();
	}

	public void sendWeibo(String _contentStr, Context context) {
		this.contentStr = _contentStr;
		mContext = context;
		setHandler(mContext);
		if (isNull) {
			String url = sp.getString("url", null);
			if (url != null && oAuth != null) {
				new TencentWeiboDialog(mContext, url, oAuth, handler).show();
			} else {
				try {
					oAuth = getOAuthV1();
					String mUrl = OAuthConstants.OAUTH_V1_AUTHORIZE_URL + "?oauth_token=" + oAuth.getOauthToken();
					new TencentWeiboDialog(mContext, mUrl, oAuth, handler).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					TAPI tapi = new TAPI("1.0");
					String resultMsg = null;
					try {
						resultMsg = tapi.add(oAuth, "json", contentStr, "127.0.0.1");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						handler.sendMessage(Message.obtain(handler, 201, resultMsg));
					}

				}
			}).start();
		}
	}

	public void setOAuth() {
		oAuth = null;
	}

	public boolean saveStatus(OAuthV1 oAuth2) {
		if (oAuth2 == null) {
			return false;
		} else {
			oAuth = oAuth2;
			try {
				isNull = false;
				oAuth = OAuthV1Client.accessToken(oAuth);
				token = oAuth.getOauthToken();
				token_secret = oAuth.getOauthTokenSecret();
				sp.edit().putString("TOKEN_KEY", token).commit();
				sp.edit().putString("TOKEN_SECRET_KEY", token_secret).commit();
				sp.edit().putString("isBind", "yes").commit();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return true;
		}
	}

	public boolean isBind() {
		sp = mContext.getSharedPreferences("tencent", 0);
		String isBind = sp.getString("isBind", "no");
		if (isBind.equals("yes")) {
			return true;
		} else {
			return false;
		}
	}

	public void getUserApi() {
		// //调用API获取用户信息
		// UserAPI userAPI=new UserAPI(OAuthConstants.OAUTH_VERSION_1);
		// try {
		// String response=userAPI.info(oAuth, "json");//获取用户信息
		// tvResult.setText(response+"\n");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// userAPI.shutdownConnection();
	}
}
