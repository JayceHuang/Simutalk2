package com.ztspeech.simutalk2.data;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TransHistory {	
	
	private ArrayList<TransData> items = new ArrayList<TransData>(); 
	
	private static TransHistory mInstance = null;
	public static TransHistory getInstance(){
		
		if(mInstance == null){
			mInstance = new TransHistory();
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
	
	public void clear(HistoryTable db) {
		// TODO Auto-generated method stub
		db.clearTable();        
		items.clear();
	}
	
	public void add(TransData data, HistoryTable db) {  
		
        SQLiteDatabase database = db.getWritableDatabase();   
        database.beginTransaction();   
        database.execSQL("insert into histroy (transtype,transfrom,transto)values(?,?,?)",   
                new Object[] { data.type, data.from, data.to });   
       // database.close();  //可以不关闭数据库，他里面会缓存一个数据库对象，如果以后还要用就直接用这个缓存的数据库对象。但通过   
        // context.openOrCreateDatabase(arg0, arg1, arg2)打开的数据库必须得关闭   
        database.setTransactionSuccessful();   
        database.endTransaction();  
    }   
	
    public void delete(HistoryTable db, Integer... ids) {   
        if (ids.length > 0) {   
            StringBuffer sb = new StringBuffer();   
            for (Integer id : ids) {   
                sb.append('?').append(',');   
            }   
            sb.deleteCharAt(sb.length() - 1);   
            SQLiteDatabase database = db.getWritableDatabase();   
            database.execSQL(   
                    "delete from histroy where dataid in(" + sb.toString()   
                            + ")", ids);   
        }   
    }

 
    public void getScrollData(HistoryTable db) {   

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
  
}
