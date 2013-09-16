package com.ztspeech.simutalk2.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TransDataList {	
	
	private static final String TABLE_NAME = "histroy";

	private ArrayList<TransData> items = new ArrayList<TransData>(); 
	
	private static TransDataList mInstance = null;
	public static TransDataList getInstance(){
		
		if(mInstance == null){
			mInstance = new TransDataList();
		}
		
		return mInstance;
	}

	
	public TransData get(int n){
		
		if( n < 0){
			return null;
		}
		
		if(n >= items.size()){
			return null;
		}
		
		return items.get(n);
	}
	
	public int size(){
		
		return items.size();
	}
	
	public void remove(int item) {
		// TODO Auto-generated method stub
		if( item < 0){
			return;
		}
		
		if(item >= items.size()){
			return;
		}
		
		items.remove(item);		
	}
	
	public void clear(DBTable db) {
		// TODO Auto-generated method stub
		SQLiteDatabase database = db.getWritableDatabase();  
		//database.beginTransaction(); 
		database.delete(TABLE_NAME, "", null);
		
       // database.execSQL("delete from histroy", null); 
       //database.close();
       // database.setTransactionSuccessful();   
        //database.endTransaction(); 
        
		items.clear();
	}
	
	public void add(TransData data, DBTable db) {  
		
        SQLiteDatabase database = db.getWritableDatabase();   
        database.beginTransaction();   
        // debug
       // database.delete("histroy", "transtype='"+data.type +"' and transfrom='"+data.from+ "'", null);  
        
        ContentValues cv =new ContentValues();
        cv.put("transtype",data.type);
        cv.put("transfrom", data.from);
        cv.put("transto",data.to );
        database.insert(TABLE_NAME,null,cv);
       // database.close();  //可以不关闭数据库，他里面会缓存一个数据库对象，如果以后还要用就直接用这个缓存的数据库对象。但通过   
        // context.openOrCreateDatabase(arg0, arg1, arg2)打开的数据库必须得关闭   
        database.setTransactionSuccessful();   
        database.endTransaction();  
    }   
	
  /*
    public void update(TransData person) {   
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();   
        database.execSQL(   
                "update person set name=?,age=? where personid=?",   
                new Object[] { person.getName(), person.getAge(),   
                        person.getId() });   
    }   

    
    public TransData find(Integer id) {   
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();   
        Cursor cursor = database.rawQuery(   
                "select * from person where personid=?",   
                new String[] { String.valueOf(id) });   
        if (cursor.moveToNext()) {   
            return new TransData(cursor.getInt(0), cursor.getString(1), cursor.getString(2),cursor.getString(3));   
        }   
        return null;   
    }   

  */     
    public void delete(DBTable db, Integer... ids) {   
        if (ids.length > 0) {   
            StringBuffer sb = new StringBuffer();   
            for (Integer id : ids) {   
                sb.append('?').append(',');  
                id += id;
            }   
            sb.deleteCharAt(sb.length() - 1);   
            SQLiteDatabase database = db.getWritableDatabase();   
            database.execSQL(   
                    "delete from histroy where dataid in(" + sb.toString()   
                            + ")", ids);   
        }   
    }

 
    public void getScrollData(DBTable db) {   

    	items.clear();
    	
        SQLiteDatabase database = db.getReadableDatabase();   
        Cursor cursor = database.rawQuery( "select * from histroy", null);   
        while (cursor.moveToNext()) { 

        	items.add(new TransData(cursor.getInt(0), 
				        			cursor.getString(1), 
				        			cursor.getString(2),
				        			cursor.getString(3)));   
        }   
  
    }   

/*    
    public long getCount() {   
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();   
        Cursor cursor = database.rawQuery("select count(*) from person", null);   
        if (cursor.moveToNext()) {   
            return cursor.getLong(0);   
        }   
        return 0;   
    }  
     */
  
}
