package com.ztspeech.simutalk2.trans;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.ac.ia.directtrans.json.JsonRequestResult;
import cn.ac.ia.directtrans.json.JsonSetUserInfo;
import cn.ac.ia.files.RequestParam;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.activity.BaseActivity;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.Util;
import com.ztspeech.simutalk2.net.ImageEngine;
import com.ztspeech.simutalk2.net.PostPackageEngine;
import com.ztspeech.simutalk2.net.ResultPackage;

public class SetHeadProtraitActivity extends BaseActivity {
	private static final String TAG = "SetHeadProtraitActivity";
	private final static String MIME_TYPE_IMAGE_PNG = "image/*";
	// 上传图片的最大宽度
	private static int IMAGE_SIZE_WIDTH = 100;
	// 上传图片的最大高度
	private static int IMAGE_SIZE_HEIGHT = 120;

	private static final int PHOTOS_FROM_CAMERA = 0;
	private static final int PHOTOS_FROM_IMAGE = 1;
	private static final int PHOTOS_RESULt_HANDLE = 2;
	private String imagePath = null;
	private String curr = null;
	private String cancelText;
	private TextView nickName;
	private Bitmap image = null;
	// private File out;
	private final String tempFileString = Util.IMG_CACHE_PATH + "temp001.png";
	private final String CachePath = Util.IMG_CACHE_PATH;
	private ImageView iv;
	private String path = null;
	private Uri seletedUri;
	// private SharedPreferences headProtraitPreferences = null;
	private UserInfo mUser = UserInfo.getInstanse();
	private SetHeadProtraitActivityEngine mSetHeadProtraitActivityEngine;
	private Context context;
	private PostPackageEngine mPostPackageEngine;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				LogInfo.LogOut(TAG, "(String) msg.obj = " + (String) msg.obj);
				setUserPhoto((String) msg.obj);
				break;
			case 1:
				savePhoto((Bitmap) msg.obj);
				break;
			case 2:
				LogInfo.LogOut(TAG, "用户取消联网");
				Toast.makeText(context, "用户取消联网", Toast.LENGTH_LONG).show();
				mSetHeadProtraitActivityEngine.dismissLoading();
				break;
			case 104:
				mSetHeadProtraitActivityEngine.dismissLoading();
				LogInfo.LogOutE("haitian", "AskQuestionActivity>>>>>>>>>>>>>>>>>>> dismissLoading");
				break;
			case 200:
				postPackageCallBack((ResultPackage) msg.obj);
				break;
			case 404:
				mSetHeadProtraitActivityEngine.dismissLoading();
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG).show();
				LogInfo.LogOutE("haitian", ">>>>>>>>>>>>>>>>>--(String) msg.obj =" + (String) msg.obj);
				break;
			default:
				break;
			}

		};
	};

	protected void onStart() {
		super.onStart();
		Util.isTmpFile = true;
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.head_protrait, "设置头像");
		// setContentView(R.layout.head_protrait);
		context = this;
		mSetHeadProtraitActivityEngine = new SetHeadProtraitActivityEngine(context, handler);
		iv = (ImageView) findViewById(R.id.iv);
		nickName = (TextView) findViewById(R.id.nickName);
		nickName.setText("昵称：" + mUser.getUserName());

		// out = new File(tempFileString);
		// headProtraitPreferences = this.getSharedPreferences("userInfos",
		// Context.MODE_PRIVATE);
		// String tmpPath = headProtraitPreferences.getString("headImgPath",
		// null);
		String ImgId = UserInfo.state.photo;
		// if (tmpPath != null) {
		// image = BitmapFactory.decodeFile(tmpPath);
		// if (image != null && ImgId != null) {
		// iv.setImageBitmap(image);
		// } else {
		// ImageEngine.setImageBitmapScale(ImgId, iv,
		// R.drawable.qa_you_qa_min_friend_head, -1);
		// }
		// } else {
		// image = BitmapFactory.decodeFile(tempFileString);
		// if (image != null && ImgId != null) {
		// iv.setImageBitmap(image);
		// } else {
		Util.isTmpFile = true;
		deletTempFile(Util.tmpFilePath);
		ImageEngine.setImageBitmapScale(ImgId, iv, R.drawable.qa_you_qa_min_friend_head, -1);
		// }

		// }
	}

	public void selectImage(View view) {
		showImageSelectedDiaolog();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogInfo.LogOutE("haitian", "requestCode =" + requestCode + "   resultCode = " + resultCode);
		if (resultCode == RESULT_CANCELED) {
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case PHOTOS_FROM_CAMERA:// 照相机
				startPhotoCrop(imagePath);
				break;
			case PHOTOS_FROM_IMAGE: // 系统相册
				seletedUri = data.getData();
				ContentResolver cr = this.getContentResolver();
				Cursor cursor = cr.query(seletedUri, null, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					path = cursor.getString(cursor.getColumnIndex("_data"));
				} else {
					path = seletedUri.getPath();
				}
				startPhotoCrop(path);
				break;
			case PHOTOS_RESULt_HANDLE: // 剪裁后的图片
				Log.e("haitian", "curr = " + curr);
				saveCompressImg(curr);
				break;
			}
		}

	}

	/**
	 * 指定路径的图片进行裁剪
	 * 
	 * @param path
	 */
	private void startPhotoCrop(String imgPath) {
		if (imgPath == null || "".equals(imgPath.trim())) {
			Toast.makeText(this, "无效的图片路径..", 0).show();
			return;
		}
		copyFile(imgPath, tempFileString);
		curr = tempFileString;
		Uri uri = Uri.fromFile(new File(curr));
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("output", uri);
		startActivityForResult(intent, PHOTOS_RESULt_HANDLE);
	}

	private void freeImages() {
		try {
			if (image != null) {
				image.recycle();
				image = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		deletTempFile(Util.tmpFilePath);
		Util.isTmpFile = false;
		freeImages();
	}

	/**
	 * 选择照片的获取方式对话框（相册，或是相机现拍）
	 * 
	 */
	private void showImageSelectedDiaolog() {
		Context dialogContext = new ContextThemeWrapper(this, android.R.style.Theme_Light);
		String[] SOURCE_ITEMS = { this.getString(R.string.takePhotos), this.getString(R.string.storage_image) };
		cancelText = this.getString(R.string.CANCEL_Txt);
		ListAdapter adapter = new ArrayAdapter<String>(dialogContext, android.R.layout.simple_list_item_1, SOURCE_ITEMS);
		AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
		builder.setTitle(this.getString(R.string.image_select_title));
		builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				getImage(which);
			}
		});
		builder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_BACKSLASH) {
					dialog.dismiss();
				}
				return true;
			}
		});
		builder.create().show();
	}

	/**
	 * 预处理相机或者相册获取图片
	 * 
	 * @param type
	 */
	private void getImage(int type) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			new AlertDialog.Builder(this).setTitle(getString(R.string.hint))
					.setMessage(getString(R.string.unableToGetResource))
					.setNegativeButton(getString(R.string.OK_Txt), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						}
					}).show();
			return;
		}
		switch (type) {
		case PHOTOS_FROM_CAMERA:// 相机拍照
			new Thread(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String filename = timeStampFormat.format(new Date());
					ContentValues values = new ContentValues();
					values.put(Media.TITLE, filename);
					Uri photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					imagePath = getRealPathFromURI(photoUri, getContentResolver());
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
					startActivityForResult(intent, PHOTOS_FROM_CAMERA);
				}
			}).start();
			break;
		case PHOTOS_FROM_IMAGE:// 图库和相册
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
						try {
							// 打开系统的图库 获取一个图片
							// Intent intent = new
							// Intent(Intent.ACTION_GET_CONTENT, null);
							Intent intent = new Intent();
							intent.setAction(Intent.ACTION_PICK);
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setType(MIME_TYPE_IMAGE_PNG);
							startActivityForResult(intent, PHOTOS_FROM_IMAGE);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}).start();
			break;
		default:
			break;
		}

	}

	/**
	 * 对裁剪后的图片进行压缩保存
	 * 
	 * @param path
	 */
	private void saveCompressImg(String path) {
		File imgFile = new File(path);
		File dir = new File(CachePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!imgFile.exists()) {
			Toast.makeText(this, "无效的图片路径..", 0).show();
			return;
		}
		compressFile(path);
	}

	/**
	 * 将原图保存到本地
	 * 
	 * @param url
	 * @param bitmap
	 */
	private String saveImg(Bitmap bitmap) {
		FileOutputStream fileOutputStream = null;
		try {
			File temp = new File(tempFileString);
			File dir = new File(CachePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (temp.exists()) {
				temp.delete();
				temp.createNewFile();
			}
			fileOutputStream = new FileOutputStream(temp);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
				fileOutputStream.flush();
			}
			return temp.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
					fileOutputStream = null;
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public void copyFile(String oldPath, String newPath) {
		try {
			// int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					// bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 将InputStream转化为byte[]
	 * 
	 * @param in
	 *            输入流
	 * @return 数组
	 */
	/*
	 * private static byte[] getData(InputStream in) { if (in == null) { return
	 * null; } ByteArrayOutputStream bs = new ByteArrayOutputStream(); byte[] b
	 * = new byte[1024]; int len = 0; try { while ((len = in.read(b, 0,
	 * b.length)) != -1) { bs.write(b, 0, len); } return bs.toByteArray(); }
	 * catch (Exception e) { e.printStackTrace(); } return null; }
	 */

	private static String getRealPathFromURI(Uri uri, ContentResolver resolver) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = resolver.query(uri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String str = cursor.getString(column_index);
		cursor.close();
		return str;

	}

	private void compressFile(final String filepath) {
		AsyncTask<String, String, Bitmap> compressFile = new AsyncTask<String, String, Bitmap>() {
			@Override
			protected void onPreExecute() {
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				BitmapFactory.Options bmfoptions = new BitmapFactory.Options();
				bmfoptions.inSampleSize = 1;
				bmfoptions.inJustDecodeBounds = true;
				image = BitmapFactory.decodeFile(filepath, bmfoptions);
				freeBitmap(image);
				if (bmfoptions.outWidth > IMAGE_SIZE_WIDTH || bmfoptions.outHeight > IMAGE_SIZE_HEIGHT) {
					// 缩放图片的尺寸
					float scaleWidth = (float) IMAGE_SIZE_WIDTH / bmfoptions.outWidth; // 按固定大小缩放
					float scaleHeight = (float) IMAGE_SIZE_HEIGHT / bmfoptions.outHeight; // 按固定大小缩放
					Matrix matrix = new Matrix();
					if (scaleWidth > scaleHeight && scaleWidth > 1) {
						scaleHeight = scaleWidth;
					}
					if (scaleHeight > scaleWidth && scaleHeight > 1) {
						scaleWidth = scaleHeight;
					}
					matrix.postScale(scaleWidth, scaleHeight);
					image = BitmapFactory.decodeFile(filepath);
					// 产生缩放后的Bitmap对象
					Bitmap resizeBitmap = Bitmap.createBitmap(image, 0, 0, bmfoptions.outWidth, bmfoptions.outHeight,
							matrix, false);
					freeBitmap(image);
					image = resizeBitmap;
					// saveImg(image);
				} else {
					image = BitmapFactory.decodeFile(filepath);
				}
				deletTempFile(filepath);
				handler.sendMessage(Message.obtain(handler, 1, image));
				return image;
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				if (bitmap != null) {
					// iv.setImageBitmap(image);
					mediaMount();
				}
			}

		};
		compressFile.execute();
	}

	private void mediaMount() {
		// 模拟系统发送一个 sd卡挂载的广播
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
		intent.setData(Uri.parse("file:" + Environment.getExternalStorageDirectory().getAbsolutePath()));
		sendBroadcast(intent);
	}

	/**
	 * 保存照片到服务器
	 * 
	 * @param bm
	 * @return
	 */
	private String mUserPhotoId = "";

	private boolean savePhoto(Bitmap bm) {

		ByteArrayOutputStream data = new ByteArrayOutputStream(10240);
		if (false == bm.compress(Bitmap.CompressFormat.PNG, 100, data)) {
			return false;
		}

		// AsyncHttpPost post = new AsyncHttpPost(new AsyncHttpPostLisenter() {
		//
		// @Override
		// public void onData(String fileId) {
		// if (fileId == null) {
		// WaitingActivity.stop();
		// return;
		// }
		// path = Util.IMG_CACHE_PATH + fileId + ".png";
		// headProtraitPreferences.edit().putString("headImgPath",
		// path).commit();
		// setUserPhoto(fileId);
		// }
		//
		// @Override
		// public void onBegin() {
		// WaitingActivity.waiting(SetHeadProtraitActivity.this, 0);
		// }
		//
		// @Override
		// public void onEnd() {
		//
		// }
		//
		// });
		// post.setHost(getString(R.string.file_host_ip), UserInfo.appId,
		// UserInfo.state.id + "");
		// post.postVoice(data.toByteArray(), RequestParam.FILE_TYPE_PHOTO);
		Util.isTmpFile = true;
		mSetHeadProtraitActivityEngine.httpRequestPostNewThread(data.toByteArray(), RequestParam.FILE_TYPE_PHOTO);
		return true;
	}

	private void postPackageCallBack(ResultPackage result) {
		if (result.isNetSucceed()) {

			JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
			if (ret != null) {
				if (ret.succeed == true) {
					UserInfo.getInstanse().setPhoto(mUserPhotoId);
					if (image != null) {
						iv.setImageBitmap(image);
					}
				} else {
					new AlertDialog.Builder(SetHeadProtraitActivity.this).setTitle("提示").setMessage(ret.explain)
							.setPositiveButton("确定", null).show();
				}
				deletTempFile(curr);
			}
		}
	}

	/**
	 * 
	 */
	// public IHttpPostListener mSetUserPhotoListener = new IHttpPostListener()
	// {
	//
	// @Override
	// public void onNetPostResult(PostPackage owner, ResultPackage result) {
	//
	// if (result.isNetSucceed()) {
	//
	// JsonRequestResult ret = JsonRequestResult.fromJson(result.getJson());
	// if (ret != null) {
	// if (ret.succeed == true) {
	//
	// UserInfo.getInstanse().setPhoto(mUserPhotoId);
	// } else {
	// new
	// AlertDialog.Builder(SetHeadProtraitActivity.this).setTitle("提示").setMessage(ret.explain)
	// .setPositiveButton("确定", null).show();
	// }
	// }
	// }
	//
	// WaitingActivity.stop();
	// }
	// };

	/**
	 * 更新服务器用户照片
	 * 
	 * @param fileId
	 */
	protected void setUserPhoto(String fileId) {
		// PostPackage post = new PostPackage(this, mSetUserPhotoListener);
		Util.isTmpFile = false;
		deletTempFile(Util.tmpFilePath);
		JsonSetUserInfo info = new JsonSetUserInfo();
		info.setUserPhoto(fileId);
		mUserPhotoId = fileId;

		mPostPackageEngine = new PostPackageEngine(context, info, handler);
		mPostPackageEngine.post();
		handler.sendEmptyMessage(104);

		// if (post.post(info, true)) {
		//
		// } else {
		// WaitingActivity.stop();
		// }
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
}
