package com.ztspeech.simutalk2.qa.data;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.ac.ia.directtrans.json.QuestionInfo;

import com.ztspeech.simutalk2.data.DataListObejct;
import com.ztspeech.simutalk2.data.QuestionTable;

public class QuestionDataList extends DataListObejct {	
	
	private QuestionTable mTable = QuestionTable.getInstanse();
	public boolean isChanged = false;
	private static QuestionDataList mInstance = null;
	public static QuestionDataList getInstance(){
		
		if(mInstance == null){
			mInstance = new QuestionDataList();
		}
		
		return mInstance;
	}

	
	public void add(QuestionData data) {
		isChanged = true;
		super.add(data);
	}
	
	public QuestionData get(int n) {
		
		return (QuestionData)super.get(n);
	}
	
	public QuestionData findByAskId(long askId){
		int nCount = size();
		for(int i=0; i < nCount; i ++){
			
			QuestionData data = get(i);
			if(data.ask.id == askId) {
				return data;
			}			
		}
		
		return null;
	}
	
	public QuestionData findBySolveId(long solveId){
		int nCount = 0;
		for(int i=0; i < nCount; i ++){
			
			QuestionData data = (QuestionData) items.get(i);
			if(data.solve.id == solveId) {
				return data;
			}			
		}
		
		return null;
	}	
	
	
	public QuestionData findById(long id) {   
		return (QuestionData) super.findById(id);
    } 

	public void updateItem(QuestionData data){
		isChanged = true;
		
		SQLiteDatabase writer = mTable.getWritableDatabase(); 
		QuestionInfo solve = data.solve;   	
		QuestionInfo ask = data.ask;
		
        ContentValues cv =new ContentValues();
        cv.put("dataid", 		data.id);
        cv.put("aid", 			ask.id);
        cv.put("senderId",  	ask.senderId);
        cv.put("sender_name", 	ask.senderName);	
        cv.put("ask_time", 		ask.time.getTime());	
        cv.put("text",			ask.text);
        cv.put("ask_len",		ask.vLen);
        cv.put("state",			data.state);
        cv.put("type",			ask.type);

        cv.put("sid", 			solve.id);
        cv.put("solver_id",  	solve.senderId);
        cv.put("solver_name", 	solve.senderName);	
        cv.put("solver_time", 	solve.time.getTime());	
        cv.put("solver_time",	solve.text);
        cv.put("solver_len",	solve.vLen);
        cv.put("look_over",		data.look_over? 1:0);
        
        writer.replace(mTable.tableName, null, cv);
	}
	
	public void addToDB(QuestionData data) {  
		
        SQLiteDatabase writer = mTable.getWritableDatabase();   
      	QuestionInfo ask = data.ask; 
    	QuestionInfo solve = data.solve;   	
        ContentValues cv =new ContentValues();
        cv.put("aid", 			ask.id);
        cv.put("senderId",  	ask.senderId);
        cv.put("sender_name", 	ask.senderName);	
        cv.put("ask_time", 		ask.time.getTime());	
        cv.put("text",			ask.text);
        cv.put("ask_len",		ask.vLen);
        cv.put("state",			data.state);
        cv.put("type",			ask.type);
        
        cv.put("sid", 			solve.id);
        cv.put("solver_id",  	solve.senderId);
        cv.put("solver_name", 	solve.senderName);	
        cv.put("solver_time", 	solve.time.getTime());	
        cv.put("solver_text",	solve.text);
        cv.put("solver_len",	solve.vLen);
        cv.put("look_over",		data.look_over? 1:0);

        writer.insert(mTable.tableName, null, cv);
 
    }   
	
    public void delete(Integer... ids) {   
        if (ids.length > 0) {   
            StringBuffer sb = new StringBuffer();   
            for (Integer id : ids) {   
                sb.append('?').append(',');   
            }   
            sb.deleteCharAt(sb.length() - 1);   
            SQLiteDatabase database = mTable.getWritableDatabase();   
            database.execSQL(   
                    "delete from " + mTable.tableName +" where dataid in(" + sb.toString()   
                            + ")", ids);   
        }   
    }

 
    public void loadDB() {   

    	items.clear();
    	
        SQLiteDatabase database = mTable.getReadableDatabase();   
        Cursor cursor = database.rawQuery( "select * from " + mTable.tableName, null);   
        QuestionData data = null;
        
//		" (dataid integer primary key autoincrement, aid integer, senderId integer," +
//		" sender_name varchar(50), ask_time integer, text varchar(250), ask_len integer," +
//		" sid integer, solver_id integer,solver_name varchar(50), solver_time integer, " +
//		" solver_text varchar(250), solver_time integer, state integer, type varchar(20))"); 
        QuestionInfo solve = null;
        while (cursor.moveToNext()) {
        	data = new QuestionData();
        	// ask
        	int i=0;
        	data.id 			= cursor.getInt(i++);
        	data.ask.id 		= cursor.getInt(i++);
        	data.ask.senderId 	= cursor.getInt(i++);
        	data.ask.senderName = cursor.getString(i++);
        	data.ask.time 		= new Date( cursor.getLong(i++));
           	data.ask.text 		= cursor.getString(i++);
        	data.ask.vLen 	= cursor.getInt(i++);
        	
        	// solve
        	solve = data.solve;
           	solve.id 			= cursor.getInt(i++);
        	solve.senderId 		= cursor.getInt(i++);
        	solve.senderName 	= cursor.getString(i++);
        	solve.time 			= new Date(cursor.getLong(i++));	
           	solve.text 			= cursor.getString(i++);
        	solve.vLen 	= cursor.getInt(i++);  
             	
        	data.state	   = cursor.getInt(i++);
        	data.ask.type  = cursor.getString(i++);
        	data.look_over = cursor.getInt(i++) == 1;

        	data.type		= QuestionData.TYPE_QUESTION;
        	add(data);
        }
    }  	
}
