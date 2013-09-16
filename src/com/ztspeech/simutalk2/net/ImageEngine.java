package com.ztspeech.simutalk2.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.MD5;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class ImageEngine {

	private static final String SCALE = "SCALE";
	private ImageView mImageView;
	private String mUrl;
	SoftReference<Bitmap> bitmapReference = null;
	/**
	 * 0为原始大小,1为缩略图,2为图标
	 */
	private int mType = 0;
	private static boolean isAppRun;
	private final int ICONW = 100;
	private final int ICONH = 120;
	private int pos = 0;

	private static Object lock = new Object();

	private static boolean mAllowLoad = true;

	private static boolean firstLoad = true;
	private static int mStartLoadLimit = 0;
	private static int mStopLoadLimit = 0;

	private static String host = "http://" + Util.FILE_HOST_IP + "/FilesServer/filesservlet";
	private static String userId = UserInfo.state.id + "";
	private static String app = UserInfo.appId;
	private static String type = RequestParam.FILE_TYPE_PHOTO;
	private static String url;

	private static HashMap<String, ArrayList<ImageObj>> map = new HashMap<String, ArrayList<ImageObj>>();

	static {
		/**
		 * 如果程序第一次运行的话，先清空缓存。
		 */
		if (!isAppRun) {
			clearCache(Util.IMG_CACHE_PATH);// 清空头像缓存
			clearCache(Util.VOICE_CACHE_PATH);// 清空语音数据缓存
			isAppRun = true;
		}
	}

	public static void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
		if (startLoadLimit > stopLoadLimit) {
			return;
		}
		mStartLoadLimit = startLoadLimit;
		mStopLoadLimit = stopLoadLimit;
	}

	public static void restore() {
		mAllowLoad = true;
		firstLoad = true;
	}

	public static void lock() {
		mAllowLoad = false;
		firstLoad = false;
	}

	public static void unlock() {
		mAllowLoad = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	/**
	 * 获取已经下载到本地的原始图片 参数为url,非path
	 */
	public static Bitmap loadLocal(String id) {
		return new ImageEngine().loadImgLocal(id);
	}

	/**
	 * 下载并显示原始图片(图标)
	 */
	public static void setImageBitmap(String id, ImageView v, int defaultDrawable, int pos) {
		url = setUrl(id);
		new ImageEngine().loadImg(url, v, defaultDrawable, pos);
	}

	/**
	 * 下载并显示缩略图片
	 */
	public static void setImageBitmapScale(String id, ImageView v, int defaultDrawable, int pos) {
		url = setUrl(id);
		new ImageEngine().loadImgScale(url, v, defaultDrawable, pos);
	}

	private class ImageObj {
		ImageView imageView;
		int type;// 0为原始大小,1为缩略图
	}

	private Bitmap getBitmap() {
		if (bitmapReference != null) {
			return bitmapReference.get();
		}
		return null;
	}

	private Handler loaderImageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				if (map.containsKey(mUrl)) {
					ArrayList<ImageObj> list = map.get(mUrl);
					map.remove(mUrl);
					try {
						for (ImageObj imageObj : list) {
							if (getBitmap() != null && imageObj.imageView.getTag(R.layout.activity_main).equals(mUrl)) {
								if (imageObj.type == 1) {
									imageObj.imageView.setImageBitmap(findScaleBitmap());
								} else {
									imageObj.imageView.setImageBitmap(getBitmap());
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (msg.what == 1) {
				LogInfo.LogOut("positin=" + pos + "  bitMap=" + getBitmap() + " bitmapReference=" + bitmapReference);
				mImageView.setImageBitmap(getBitmap());
			}
		};
	};

	/**
	 * 获取已经下载到本地的原始图片
	 */
	public Bitmap loadImgLocal(String id) {
		url = setUrl(id);
		bitmapReference = new SoftReference<Bitmap>(getBitmapFromLocal(url));
		return getBitmap();
	}

	/**
	 * 下载并显示原始图片
	 */
	public void loadImg(String id, ImageView v, int defaultDrawable, int p) {
		mType = 0;
		pos = p;
		loadImgBy(url, v, defaultDrawable);
	}

	private static String setUrl(String id) {
		if (id == null || id.trim().length() <= 0) {
			return null;
		}
		host = "http://" + Util.FILE_HOST_IP + "/FilesServer/filesservlet";
		userId = UserInfo.state.id + "";
		app = UserInfo.appId;
		url = "";
		url = host + "?" + RequestParam.APP + "=" + app + "&" + RequestParam.FILE_ID + "=" + id + "&"
				+ RequestParam.TYPE + "=" + type + "&" + RequestParam.USER_ID + "=" + userId;
		return url;
	}

	/**
	 * 下载并显示缩略图片
	 */
	public void loadImgScale(String id, ImageView v, int defaultDrawable, int p) {
		mType = 1;
		pos = p;
		// url = setUrl(id);
		loadImgBy(url, v, defaultDrawable);
	}

	private void loadImgBy(String url, ImageView v, int defaultDrawable) {
		mImageView = v;
		mUrl = url;
		if (TextUtils.isEmpty(url)) {
			mImageView.setTag(R.layout.activity_main, mUrl);
			mImageView.setImageResource(defaultDrawable);
			return;
		}
		File cacheDir = new File(Util.IMG_CACHE_PATH);
		String urlConvert = MD5.md5Upper(mUrl);
		File file = new File(cacheDir, urlConvert);
		if (file.exists()) {
			if (mType == 1) {
				mImageView.setImageBitmap(findScaleBitmap());
			} else {
				mImageView.setImageBitmap(loadImgLocal(mUrl));
			}
			if (Util.isTmpFile) {
				deletTempFile(Util.tmpFilePath);
				Util.tmpFilePath = file.getAbsolutePath();
				Util.isTmpFile = false;
			}
		} else {
			ImageObj obj = new ImageObj();
			obj.imageView = mImageView;
			obj.imageView.setTag(R.layout.activity_main, mUrl);
			obj.imageView.setImageResource(defaultDrawable);
			obj.type = mType;
			if (map.containsKey(mUrl)) {// 防止重复请求同一张图片
				map.get(mUrl).add(obj);
				return;
			} else {
				ArrayList<ImageObj> list = new ArrayList<ImageObj>();
				list.add(obj);
				map.put(mUrl, list);

			}
			new Thread() {
				@Override
				public void run() {
					try {
						HttpURLConnection conn = null;
						InputStream is = null;
						try {
							if (!mAllowLoad) {
								LogInfo.LogOut("list", "-----------------------prepare to load");
								synchronized (lock) {
									try {
										lock.wait();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							if (mAllowLoad && (firstLoad || (pos <= mStopLoadLimit && pos >= mStartLoadLimit))
									|| pos == -1) {
								LogInfo.LogOut("list", "ImageLoader.thread=" + this.getName() + "\t\tmImageView="
										+ mImageView + "\t\turl=" + mUrl);
								URL tempUrl = new URL(mUrl);

								conn = (HttpURLConnection) tempUrl.openConnection();
								conn.setDoInput(true);
								LogInfo.LogOut("list", "--conn.getResponseCode() = " + conn.getResponseCode());
								if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
									is = conn.getInputStream();
									byte buffer[] = new byte[1024];
									int len;
									ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
									while ((len = is.read(buffer)) != -1) {
										swapStream.write(buffer, 0, len);
									}
									if (swapStream.size() > 0) {
										byte[] swapArray = swapStream.toByteArray();
										Bitmap bitmap = BitmapFactory.decodeByteArray(swapArray, 0, swapArray.length);
										swapStream.close();
										bitmapReference = new SoftReference<Bitmap>(bitmap);
										saveBitmapToLocal(mUrl, bitmap);
									}
								}
							} else {
								LogInfo.LogOut("igrPosition=" + pos);
							}
							if (getBitmap() != null) {
								if (mType == 1 && getBitmap().getHeight() > 0) {
									findScaleBitmap();
								}
								loaderImageHandler.sendEmptyMessage(0);
							} else {
								map.remove(mUrl);
							}

						} catch (Exception e) {
							map.remove(mUrl);
							e.printStackTrace();
						} finally {
							if (is != null) {
								try {
									is.close();
								} catch (IOException e) {
								}
							}
							if (conn != null) {
								conn.disconnect();
							}
						}
					} catch (OutOfMemoryError e) {
						System.gc();
					}
				}
			}.start();
		}
	}

	/**
	 * 将原图保存到本地
	 * 
	 * @param url
	 * @param bitmap
	 */
	private void saveBitmapToLocal(String url, Bitmap bitmap) {
		File cacheDir = new File(Util.IMG_CACHE_PATH);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		String urlConvert = MD5.md5Upper(url);
		File file = new File(cacheDir, urlConvert);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
				outputStream.flush();
			}
			if (Util.isTmpFile) {
				deletTempFile(Util.tmpFilePath);
				Util.tmpFilePath = file.getAbsolutePath();
				Util.isTmpFile = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 传入本地路径 返回压缩后的小图
	 */
	private Bitmap findScaleBitmap() {
		Bitmap bitmap = null;
		try {
			File cacheDir = new File(Util.IMG_CACHE_PATH);
			String urlConvert = MD5.md5Upper(mUrl + SCALE);
			File file = new File(cacheDir, urlConvert);
			if (file.exists()) {// 小图已经存在
				return BitmapFactory.decodeFile(file.getAbsolutePath());
			} else {
				urlConvert = MD5.md5Upper(mUrl);
				File oldfile = new File(cacheDir, urlConvert);
				String path = oldfile.getAbsolutePath();
				BitmapFactory.Options bmfoptions = new BitmapFactory.Options();
				bmfoptions.inSampleSize = 1;
				bmfoptions.inJustDecodeBounds = true;
				bitmap = BitmapFactory.decodeFile(path, bmfoptions);
				freeBitmap(bitmap);
				if (bmfoptions.outWidth > ICONW || bmfoptions.outHeight > ICONH) {
					// 缩放图片的尺寸
					float scaleWidth = (float) ICONW / bmfoptions.outWidth; // 按固定大小缩放
																			// sWidth
																			// 写多大就多大
					float scaleHeight = (float) ICONH / bmfoptions.outHeight; //
					Matrix matrix = new Matrix();
					matrix.postScale(scaleWidth, scaleHeight);
					bitmap = BitmapFactory.decodeFile(path);
					// 产生缩放后的Bitmap对象
					Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmfoptions.outWidth, bmfoptions.outHeight,
							matrix, false);
					freeBitmap(bitmap);
					bitmap = resizeBitmap;
				} else {
					bitmap = BitmapFactory.decodeFile(path);
				}
				// 保存到文件,下载不用再次生成
				try {
					FileOutputStream out = new FileOutputStream(file);
					if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
						out.flush();
						out.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return bitmap;
	}

	/**
	 * 将某bitmap释放掉
	 */
	private void freeBitmap(Bitmap bitmap) {
		try {
			if (bitmap != null) {
				bitmap.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据网址获取已经下载到本地的原图,没有则为null
	 */
	private Bitmap getBitmapFromLocal(String url) {
		Bitmap bitmap = null;
		File cacheDir = new File(Util.IMG_CACHE_PATH);
		String urlConvert = MD5.md5Upper(url);
		File file = new File(cacheDir, urlConvert);
		if (!file.exists()) {
			return bitmap;
		}
		bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		return bitmap;
	}

	private void deletTempFile(String path) {
		try {
			if (path == null || "".equals(path.trim())) {
				return;
			}
			File f = new File(path);
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 程序 每次启动时执行,按周期清除缓存
	 */
	private static void clearCache(String catchPath) {
		File cacheDir = new File(catchPath);
		if (cacheDir.exists()) {
			File[] fileList = cacheDir.listFiles();
			long week = 7 * 24 * 60 * 60 * 1000;// 缓存一周
			for (File entry : fileList) {
				try {
					if (System.currentTimeMillis() - entry.lastModified() > week) {
						entry.delete();
						LogInfo.LogOut(entry.getName());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
