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
			// ���dictionary.db�ļ��ľ���·��

			File dir = new File(Util.DATABASE_PATH);
			// ���/sdcard/dictionaryĿ¼�д��ڣ��������Ŀ¼
			if (!dir.exists())
				dir.mkdirs();
			// �����/sdcard/dictionaryĿ¼�в�����
			// dictionary.db�ļ������res\rawĿ¼�и�������ļ���
			// SD����Ŀ¼��/sdcard/dictionary��
			if (!(new File(databaseFilename2)).exists()) {
				// ��÷�װdictionary.db�ļ���InputStream����
				InputStream is = context.getResources().openRawResource(R.raw.directtrans);
				FileOutputStream fos = new FileOutputStream(databaseFilename2);
				byte[] buffer = new byte[8192];
				int count = 0;
				// ��ʼ����dictionary.db�ļ�
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
			// ���dictionary.db�ļ��ľ���·��
			File db = new File(databaseFilename);
			if (db.exists()) {
				db.delete();
			}
			// ��÷�װdictionary.db�ļ���InputStream����
			FileOutputStream fos = new FileOutputStream(databaseFilename);
			byte[] buffer = new byte[8192];
			int count = 0;
			// ��ʼ����dictionary.db�ļ�
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
			// ���dictionary.db�ļ��ľ���·��
			File db = new File(databaseFilename2);
			if (db.exists()) {
				db.delete();
			}
			// ��÷�װdictionary.db�ļ���InputStream����
			FileOutputStream fos = new FileOutputStream(databaseFilename2);
			byte[] buffer = new byte[8192];
			int count = 0;
			// ��ʼ����dictionary.db�ļ�
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
			// ��ʼ����dictionary.db�ļ�
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
