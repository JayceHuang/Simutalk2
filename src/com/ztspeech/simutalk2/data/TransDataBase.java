package com.ztspeech.simutalk2.data;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.dom.InitDataBase;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class TransDataBase extends SQLiteOpenHelper {

	private static TransDataBase mInstance = null;
	private Context context;
	private ArrayList<DBTable> tables = new ArrayList<DBTable>();

	public TransDataBase(Context context, String name, CursorFactory factory, int version) {

		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		this.context = context;

	}

	public synchronized static TransDataBase getInstance(Context context, String name, int version) {
		if(mInstance==null){
			mInstance = new TransDataBase(context, name, version);
		}
		return mInstance;
	}

	private String databaseFilename2 = Util.DATABASE_PATH + "/" + Util.DATABASE_FILENAME2;

	public TransDataBase(Context context, String dbName, int version) {

		// 第一个参数是应用的上下文
		// 第二个参数是应用的数据库名字
		// 第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
		// 第四个参数是数据库版本，必须是大于0的int（即非负数）
		super(context, dbName, null, version);
		mInstance = this;
	}

	/**
	 * 口译记录数据库初始化
	 * 
	 */
	private String isok2 = null;

	public String getUserDatabaseInittedResult() {
		File dir = new File(Util.DATABASE_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File dd = new File(databaseFilename2);
		if (dd.exists()) {
			isok2 = context.getResources().getString(R.string.dbInit_Successfull);
		} else {
			isok2 = null;
		}
		return isok2;
	}

	/**
	 * 口译记录数据库初始化
	 * 
	 */
	@SuppressWarnings("unused")
	private void initUserDatabase() {
		InitDataBase writeDBtoSDCard = new InitDataBase(context);
		isok2 = writeDBtoSDCard.writeDatabaseToPhone();
		if (isok2.equals(context.getResources().getString(R.string.dbInit_Successfull))) {
			new SQLiteDom().openDB2();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		File dir = new File(Util.DATABASE_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File dd = new File(databaseFilename2);
		if (dd.exists()) {
			dd.delete();
		}
		// initUserDatabase();
		for (int i = 0; i < tables.size(); i++) {
			tables.get(i).onCreate(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (newVersion > oldVersion && oldVersion <= 4) {
			SQLiteDom ss = new SQLiteDom();
			ss.openDB2();
			ss.updateUserDatabaseVersion();
		}
		for (int i = 0; i < tables.size(); i++) {
			tables.get(i).onUpgrade(db, oldVersion, newVersion);
		}
	}

	public void addTable(DBTable tb) {

		for (int i = 0; i < tables.size(); i++) {
			if (tb == tables.get(i)) {
				return;
			}
		}

		tb.setDataBase(this);
		tables.add(tb);
	}

}
