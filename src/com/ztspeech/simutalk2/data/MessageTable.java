package com.ztspeech.simutalk2.data;

import android.database.sqlite.SQLiteDatabase;

public class MessageTable extends DBTable  {
	
	
	private static MessageTable mInstanse;
	
	public MessageTable() {
		// TODO Auto-generated constructor stub
		tableName = " histroy ";
	}
	
	public static MessageTable getInstanse(){

		if(mInstanse == null) {
			
			mInstanse = new MessageTable();
		}
		
		return mInstanse;
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + 
				"(dataid integer primary key autoincrement,transtype varchar(20), transfrom varchar(250), transto varchar(250))"); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+ tableName);   
        onCreate(db);
	}
}
