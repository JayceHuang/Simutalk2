package com.ztspeech.simutalk2.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class MsgGroupTable extends DBTable {

	public MsgGroupTable() {

		tableName = " message ";
	}
	
	private static MsgGroupTable mInstance = null;

	public static MsgGroupTable getInstance() {

		if (mInstance == null) {
			mInstance = new MsgGroupTable();
		}

		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
				+ "(dataid integer primary key,"
				+ " function varchar(20),"
				+ " link_id integer, type varchar(20),"
				+ " sender_id varchar(20), state integer,"
				+ " sender_name varchar(50), look_over varchar(2),"
				+ " msg_time integer, text varchar(250)," 
				+  " sound_length integer,sound varchar(64))");
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//		db.execSQL("DROP TABLE IF EXISTS "+ tableName);
//		onCreate(db);
	}


	public boolean addToDB(MsgInfoData data) {

		SQLiteDatabase writer = getWritableDatabase();
		data.id = MsgInfoData.getNewId();

		ContentValues cv =new ContentValues();		
		
        cv.put("dataid", 		data.id);
        cv.put("function", 		data.getCmd());
        cv.put("link_id", 		data.linkId);        
        cv.put("type", 			data.type+"");	
        cv.put("sender_id", 	data.senderId);
        cv.put("sender_name",	data.name);	
        cv.put("look_over", 	data.look_over);
        cv.put("msg_time", 		data.time.getTime());	
        cv.put("text", 			data.text);
        cv.put("state",			data.state);
        cv.put("sound_length",  data.vLen);	
        cv.put("sound", 		data.vId);

        writer.insert(tableName, null, cv);
		
		return true;

	}
	
	public boolean setDBLookover(long id) {

		SQLiteDatabase writer = this.getWritableDatabase();

		writer.execSQL(
				"update " + tableName + " set look_over = 1 where dataid =?",
						new Object[] {id});
		return true;

	}	
	
	public void deleteMsg(long id) {

		SQLiteDatabase writer = this.getWritableDatabase();
		writer.execSQL("delete from " + tableName + " where look_over = 1 and dataid=?",
				new Object[] { id });		
	}
	
	public boolean setState(long linkId, int state) {

		SQLiteDatabase writer = this.getWritableDatabase();
		writer.execSQL(
				"update " + tableName + " set state =? where link_id =?",
						new Object[] {state,linkId});
		return true;
	}
	

	public void clearDialog(String dialog) {

		SQLiteDatabase writer = this.getWritableDatabase();
		writer.execSQL("delete from " + tableName + " where dialog=?",
				new Object[] { dialog });
	}

	public void clearMsgByUser(String user){
		
		SQLiteDatabase writer = this.getWritableDatabase();
		writer.execSQL("delete from " + tableName + " where look_over = 1 and user_id=?",
				new Object[] { user });

	}

	public void clearMsgBySenderId(long linkId) {

		SQLiteDatabase writer = this.getWritableDatabase();
		writer.execSQL("delete from " + tableName + " where link_id=?",
				new Object[] { linkId });		
	}

	public void clearMsg() {

		SQLiteDatabase writer = this.getWritableDatabase();
		writer.execSQL("delete from " + tableName + " where look_over = ?",	new Object[] { 1 });
		
	}

	public void deleteLookOverMsg(long linkId) {

		SQLiteDatabase writer = this.getWritableDatabase();
		writer.execSQL("delete from " + tableName + " where look_over = 1 and link_id=?",
				new Object[] { linkId });		
	}

	public boolean setUserName(int senderId, String name) {
		SQLiteDatabase writer = this.getWritableDatabase();
		writer.execSQL(
				"update " + tableName + " set sender_name =? where sender_id =?",
						new Object[] {name,senderId});
		return true;		
	}
	
	
}
