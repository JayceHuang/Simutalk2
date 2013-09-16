package com.ztspeech.simutalk2.dictionary.dom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class InitDataBase {

	private Context context;
	private String databaseFilename = Util.DATABASE_PATH + "/" + Util.DATABASE_FILENAME;
	private String databaseFilename2 = Util.DATABASE_PATH + "/" + Util.DATABASE_FILENAME2;

	public InitDataBase(Context context) {
		this.context = context;
	}

	public InitDataBase() {

	}

	public String writeDatabase() {
		int[] id = { R.raw.dictionary0, R.raw.dictionary1, R.raw.dictionary2, R.raw.dictionary3, R.raw.dictionary4 };
		try {
			File dir = new File(Util.DATABASE_PATH);
			if (!dir.exists())
				dir.mkdirs();
			if (!(new File(databaseFilename)).exists()) {
				writeDictionaryDatabaseFromRaw(id);
			} else {
				int apkSupportedMinDBv = Integer.parseInt(context.getResources().getString(
						R.string.database_minversions));
				int apkSupportedMaxDBv = Integer.parseInt(context.getResources().getString(
						R.string.database_maxversions));
				String localDBv = new SQLiteDom().getCurrentdbVersion();
				if (localDBv == null) {
					writeDictionaryDatabaseFromRaw(id);
				} else {
					int ldbv = Integer.parseInt(localDBv);
					if (ldbv >= apkSupportedMinDBv && ldbv <= apkSupportedMaxDBv) {

					} else {
						writeDictionaryDatabaseFromRaw(id);
					}
				}

			}

			return context.getResources().getString(R.string.dbInit_Successfull);
		} catch (Exception e) {
			e.printStackTrace();
			return context.getResources().getString(R.string.dbInit_fail);

		}
	}

	public String writeDatabaseToPhone() {
		try {
			// 获得dictionary.db文件的绝对路径

			File dir = new File(Util.DATABASE_PATH);
			// 如果/sdcard/dictionary目录中存在，创建这个目录
			if (!dir.exists())
				dir.mkdirs();
			// 如果在/sdcard/dictionary目录中不存在
			// dictionary.db文件，则从res\raw目录中复制这个文件到
			// SD卡的目录（/sdcard/dictionary）
			if (!(new File(databaseFilename2)).exists()) {
				// 获得封装dictionary.db文件的InputStream对象
				InputStream is = context.getResources().openRawResource(R.raw.directtrans);
				FileOutputStream fos = new FileOutputStream(databaseFilename2);
				byte[] buffer = new byte[8192];
				int count = 0;
				// 开始复制dictionary.db文件
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}

				fos.close();
				is.close();
			}
			// Runtime.getRuntime()
			// .exec("chmod 644 "
			// + databaseFilename2);
			return context.getResources().getString(R.string.dbInit_Successfull);
		} catch (Exception e) {
			e.printStackTrace();
			return context.getResources().getString(R.string.dbInit_fail);

		}
	}

	public String updateDataBase(InputStream dataBase) {
		try {
			// 获得dictionary.db文件的绝对路径
			File db = new File(databaseFilename);
			if (db.exists()) {
				db.delete();
			}
			// 获得封装dictionary.db文件的InputStream对象
			FileOutputStream fos = new FileOutputStream(databaseFilename);
			byte[] buffer = new byte[8192];
			int count = 0;
			// 开始复制dictionary.db文件
			while ((count = dataBase.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}

			fos.close();
			dataBase.close();
			return context.getResources().getString(R.string.dbInit_Successfull);
		} catch (Exception e) {
			e.printStackTrace();
			return context.getResources().getString(R.string.dbInit_fail);

		}
	}

	public String updateDataBase2(InputStream dataBase) {
		try {
			// 获得dictionary.db文件的绝对路径
			File db = new File(databaseFilename2);
			if (db.exists()) {
				db.delete();
			}
			// 获得封装dictionary.db文件的InputStream对象
			FileOutputStream fos = new FileOutputStream(databaseFilename2);
			byte[] buffer = new byte[8192];
			int count = 0;
			// 开始复制dictionary.db文件
			while ((count = dataBase.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}

			fos.close();
			dataBase.close();
			return "1";
		} catch (Exception e) {
			e.printStackTrace();
			return "2";

		}
	}

	public void writeDictionaryDatabaseFromRaw(int[] ids) {
		InputStream[] is = new InputStream[5];
		for (int i = 0; i < ids.length; i++) {
			// id[i] = Integer.parseInt(R.raw);
			is[i] = context.getResources().openRawResource(ids[i]);
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(databaseFilename);
			byte[] buffer = new byte[ids.length * 1000 * 1024];
			int count = 0;
			// 开始复制dictionary.db文件
			for (int i = 0; i < is.length; i++) {
				while ((count = is[i].read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}

			}

			fos.close();
			for (int i = 0; i < is.length; i++) {
				is[i].close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new SQLiteDom().insertCurrentVersion(context.getResources().getString(R.string.database_currentversions), '0');
	}
}
