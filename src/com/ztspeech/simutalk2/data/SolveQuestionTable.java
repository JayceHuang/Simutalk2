package com.ztspeech.simutalk2.data;

import java.util.Date;
import cn.ac.ia.directtrans.json.UserState;
import android.database.sqlite.SQLiteDatabase;


public class SolveQuestionTable  extends DBTable{
	
	public SolveQuestionTable() {
		// TODO Auto-generated constructor stub		
		tableName = " ask_question ";
	}


	private static SolveQuestionTable mInstanse = null;	
	public String getUserName() {
		return state.name;
	}

	public void setUserName(String userName) {
		state.name = userName;
	}


	public boolean autoTTS = true;
	public String param0 = "";
	public Date loginTime = new Date();
	public UserState state = new UserState();
	
	public static SolveQuestionTable getInstanse(){

		if(mInstanse == null) {
			
			mInstanse = new SolveQuestionTable();
		}
		
		return mInstanse;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + 
				"(dataid integer primary key autoincrement, " +
				"name varchar(50), autoplay integer," +
				"language varchar(4), host_version varchar(1024)," +
				"update_flag varchar(1024), param0 varchar(256),  " +
				"param11 varchar(256)," +
				"param1 varchar(1024), param2 varchar(1024))"); 
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        //db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);   
    //    onCreate(db);
	}
}
