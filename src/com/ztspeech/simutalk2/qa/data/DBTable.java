package com.ztspeech.simutalk2.qa.data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DBTable {

	public String tableName = "";
	public SQLiteOpenHelper database = null;
	
	public void setDataBase(SQLiteOpenHelper database){

		this.database = database;
	}
	
	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

	public abstract void onCreate(SQLiteDatabase db);

	public SQLiteDatabase getReadableDatabase() {
		// TODO Auto-generated method stub
		return database.getReadableDatabase();
	}

	public void clearTable() {
		// TODO Auto-generated method stub
		SQLiteDatabase writer = this.getWritableDatabase();
		writer.delete(tableName, null, null);
	}
	
	public SQLiteDatabase getWritableDatabase() {
		// TODO Auto-generated method stub
		return database.getWritableDatabase();
	}

}
