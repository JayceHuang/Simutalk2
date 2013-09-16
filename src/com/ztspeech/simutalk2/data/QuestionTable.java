package com.ztspeech.simutalk2.data;

import android.database.sqlite.SQLiteDatabase;

public class QuestionTable extends DBTable  {
	
	
	private static QuestionTable mInstanse;
	
	public QuestionTable() {
		// TODO Auto-generated constructor stub
		tableName = " question ";
	}
	
	public static QuestionTable getInstanse(){

		if(mInstanse == null) {
			
			mInstanse = new QuestionTable();
		}
		
		return mInstanse;
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + 
				" (dataid integer primary key autoincrement, aid integer, senderId integer," +
				" sender_name varchar(50), ask_time integer, text varchar(250), ask_len integer," +
				" sid integer, solver_id integer,solver_name varchar(50), solver_time integer, " +
				" solver_text varchar(250), solver_len integer, state integer, type varchar(20)," +
				" look_over integer)"); 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
//        db.execSQL("DROP TABLE IF EXISTS "+ tableName);   
//        onCreate(db);
	}
}
