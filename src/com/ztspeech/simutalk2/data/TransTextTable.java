package com.ztspeech.simutalk2.data;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.KouyiRecord;
import com.ztspeech.simutalk2.trans.speak.SpeakItemData;

public class TransTextTable extends DBTable {

	public static final String LANGUAGE_CH = "i=ch"; // 输入语言
	public static final String LANGUAGE_EN = "i=en"; // 输入语言
	public static final String LANGUAGE_LE = "i=le"; // 输入语言

	public static Integer record_id = null;

	private SQLiteDom sqlLiteDom;

	public TransTextTable(String tbName) {

		tableName = tbName;
		sqlLiteDom = new SQLiteDom();
	}

	public List<SpeakItemData> mItems = new ArrayList<SpeakItemData>();
	private int maxId = 1;

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + "(dataid integer primary key, "
				+ "text varchar(256), type integer,language varchar(10))");
	}

	/**
	 * 删除口译记录中过多的语音
	 */
	private void clearVoiceBuffer() {

		int MAX_SIZE = 100;
		int nSize = mItems.size();
		SpeakItemData data = null;

		for (int i = MAX_SIZE; i < nSize; i++) {

			data = mItems.get(i);
			data.speakStream = null;
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (oldVersion == 1) {
			// db.execSQL("DROP TABLE IF EXISTS "+ tableName);
			// onCreate(db);
		}
		// db.execSQL("DROP TABLE IF EXISTS "+ tableName);
		// onCreate(db);
	}

	public void initRecord_Id() {
		record_id = sqlLiteDom.getLastRecordId();
		if (record_id == null) {
			record_id = 0;
		}
	}

	public void insert(SpeakItemData obj) {

		// SQLiteDatabase writer = getWritableDatabase();
		// ContentValues cv =new ContentValues();
		// obj.id = ++ maxId;
		// int type = obj.type;
		//
		// if(type == SpeakItemData.Define.SPEAK_TYPE_SPEAK){
		// type = SpeakItemData.Define.SPEAK_TYPE_TEXT;
		// }
		//
		// cv.put("type", type);
		// cv.put("dataid", obj.id);
		// cv.put("text",obj.speak);
		// cv.put("language", obj.languageSpeak);
		//
		// writer.insert(tableName, null, cv);
		record_id++;
		KouyiRecord kouyi = new KouyiRecord();
		kouyi.setRecordId(record_id);
		kouyi.setId(obj.taskId);
		kouyi.setSaid(obj.speak);
		kouyi.setTranslated(obj.trans);
		kouyi.setType(obj.type);
		kouyi.setDateTime(obj.datetime);
		kouyi.setComment(obj.flag + "");
		sqlLiteDom.insertRecord(kouyi);
	}

	public SpeakItemData get(int arg0) {

		return mItems.get(arg0);
	}

	/**
	 * 清空所有记录
	 */
	public void clear() {

		// SQLiteDatabase writer = getWritableDatabase();
		// writer.execSQL("delete from " + tableName + " where 1=?",new Object[]
		// {1});
		//

		sqlLiteDom.deleteAllRecord();
		mItems.clear();

		// SpeakItemData full = new SpeakItemData();
		// full.type = SpeakItemData.Define.SPEAK_TYPE_FULL;
		// full.speak = " ";
		// mItems.add(full);
	}

	// /**
	// * 查找翻译句对
	// * @param f
	// * @return
	// */
	// public SpeakItemData findTrans(SpeakItemData f) {
	//
	// int nSize = mItems.size();
	// for( int i=0; i < nSize; i ++){
	// SpeakItemData data = mItems.get(i);
	// if(data.id == f.id){
	// if(data.type == SpeakItemData.Define.SPEAK_TYPE_TRANS){
	// i--;
	// if(i >=0){
	// data = mItems.get(i);
	// if( data.type == SpeakItemData.Define.SPEAK_TYPE_SPEAK ||
	// data.type == SpeakItemData.Define.SPEAK_TYPE_TEXT){
	// return data;
	// }
	// }
	// }
	// else {
	// i++;
	// if(i < nSize) {
	// data = mItems.get(i);
	// if( data.type == SpeakItemData.Define.SPEAK_TYPE_TRANS){
	// return data;
	// }
	// }
	// }
	// break;
	// }
	// }
	// return null;
	// }

	// public String findSaid(SpeakItemData del){
	// String text = "";
	// if(del.type == SpeakItemData.Define.SPEAK_TYPE_TEXT ||
	// del.type == SpeakItemData.Define.SPEAK_TYPE_SPEAK){
	// text = del.speak;
	// }
	// else if(del.type == SpeakItemData.Define.SPEAK_TYPE_TRANS){
	// SpeakItemData trans = findTrans(del);
	// if(trans != null){
	// text = trans.speak;
	// }
	// }
	// return text;
	// }

	/**
	 * 删除记录并返回对应索引 （索引从0开始）
	 * 
	 * @param del
	 * @return
	 */
	public int remove(SpeakItemData del) {

		// SpeakItemData trans = findTrans(del);
		// String text = "";
		// if(del.type == SpeakItemData.Define.SPEAK_TYPE_TEXT ||
		// del.type == SpeakItemData.Define.SPEAK_TYPE_SPEAK){
		// text = del.speak;
		// }
		// else if(del.type == SpeakItemData.Define.SPEAK_TYPE_TRANS){
		// if(trans != null){
		// text = trans.speak;
		// }
		// }
		//
		// int nSize = mItems.size();
		// int nIndex = 0;
		// for( int i=0; i < nSize; i ++){
		// SpeakItemData data = mItems.get(i);
		//
		// if(data.id == del.id){
		// mItems.remove(del);
		// if( trans != null){
		// mItems.remove(trans);
		// }
		// break;
		// }
		// if(data.speak.equals(text)) {
		// nIndex ++;
		// }
		// }
		int index = 0;
		sqlLiteDom.deleteRecordById(del.recordId);
		return index;
	}

	/**
	 * 获取口译记录
	 * 
	 * @param id
	 * @return KouyiRecord
	 */
	public KouyiRecord getRecords(int id) {
		SpeakItemData obj = mItems.get(id);
		KouyiRecord kouyi = new KouyiRecord();
		kouyi.setRecordId(obj.id);
		kouyi.setId(obj.taskId);
		kouyi.setSaid(obj.speak);
		kouyi.setTranslated(obj.trans);
		kouyi.setType(obj.flag + "");
		kouyi.setDateTime(obj.datetime);
		kouyi.setComment(obj.flag + "");
		// int nSize = mItems.size() - 1;
		//
		// for( int i=0; i < nSize; i ++){
		// SpeakItemData data = mItems.get(i);
		// if(data.id == id){
		// if(data.type == SpeakItemData.Define.SPEAK_TYPE_TRANS){
		// kouyi.setTranslated(data.speak);
		// i--;
		// if(i >=0){
		// data = mItems.get(i);
		// if( data.type == SpeakItemData.Define.SPEAK_TYPE_SPEAK ||
		// data.type == SpeakItemData.Define.SPEAK_TYPE_TEXT){
		// kouyi.setSaid(data.speak);
		// }
		// }else{
		// kouyi.setSaid(" ");
		// }
		// }
		// else {
		// kouyi.setSaid(data.speak);
		// i++;
		// if(i < nSize) {
		// data = mItems.get(i);
		// if( data.type == SpeakItemData.Define.SPEAK_TYPE_TRANS){
		// kouyi.setTranslated(data.speak);
		// }
		// }else{
		// kouyi.setTranslated(" ");
		// }
		// }
		// return kouyi;
		// }
		// }
		return kouyi;
	}

	/**
	 * 得到记录个数
	 * 
	 * @return
	 */
	public int size() {

		return mItems.size();
	}

	public void add(int nIndex, SpeakItemData obj) {
		clearVoiceBuffer();
		insert(obj);
		obj.recordId = record_id;
		mItems.add(nIndex, obj);
	}

	public void update(int nIndex, SpeakItemData obj) {
		mItems.remove(nIndex);
		mItems.add(nIndex, obj);
		update(obj);
	}

	public void commont(int nIndex, SpeakItemData obj) {
		mItems.remove(nIndex);
		mItems.add(nIndex, obj);
		sqlLiteDom.commentRecord(obj.recordId, obj.flag);
	}

	public void update(SpeakItemData obj) {
		KouyiRecord kouyi = new KouyiRecord();
		kouyi.setRecordId(record_id);
		kouyi.setSaid(obj.speak);
		kouyi.setId(obj.taskId);
		kouyi.setTranslated(obj.trans);
		kouyi.setType(obj.type + "");
		kouyi.setDateTime(obj.datetime);
		kouyi.setComment(obj.flag + "");
		sqlLiteDom.updateRecord(kouyi);
	}

	/**
	 * 从数据库中加载数据
	 */
	public void load() {

		mItems.clear();
		List list = sqlLiteDom.getSimilarResultInKouyi(null, 1);
		KouyiRecord kouyi = null;
		SpeakItemData obj = null;
		for (int i = 0; i < list.size(); i++) {
			kouyi = (KouyiRecord) list.get(i);
			obj = new SpeakItemData();
			obj.flag = Integer.parseInt(kouyi.getComment());
			obj.datetime = kouyi.getDateTime();
			obj.speak = kouyi.getSaid();
			obj.trans = kouyi.getTranslated();
			obj.recordId = kouyi.getRecordId();
			obj.taskId = kouyi.getId();
			obj.type = kouyi.getType();
			obj.id = kouyi.getRecordId();
			mItems.add(obj);
		}
		// SQLiteDatabase reader = this.getReadableDatabase();
		// Cursor cursor = reader.rawQuery(
		// "select dataid, text,type,language from " + tableName , null);
		//
		// if(cursor != null){
		// SpeakItemData pre = null;
		// while (cursor.moveToNext()) {
		//
		// SpeakItemData data = new SpeakItemData();
		// data.id = cursor.getInt(0);
		// data.speak = cursor.getString(1);
		// data.type = cursor.getInt(2);
		// data.languageSpeak = cursor.getString(3);
		//
		// if(data.type == SpeakItemData.Define.SPEAK_TYPE_SPEAK){
		// data.type = SpeakItemData.Define.SPEAK_TYPE_TEXT;
		// }
		//
		// if(data.type == SpeakItemData.Define.SPEAK_TYPE_TRANS){
		// if( pre != null) {
		// pre.text2 = data.text;
		// data.text2 = pre.text;
		// }
		// }
		//
		// maxId = data.id;
		// pre = data;
		// mItems.add(data);
		// }
		// cursor.close();
		//
		// if(pre != null){
		// if(pre.type == SpeakItemData.Define.SPEAK_TYPE_TEXT){
		//
		// }
		// }
		// }

		// SpeakItemData full = new SpeakItemData();
		// full.type = SpeakItemData.Define.SPEAK_TYPE_FULL;
		// full.text = " ";
		// mItems.add(full);
	}

	/**
	 * 删除指定识别记录
	 * 
	 * @param text
	 */
	// public void deleteByText(String text, int index) {
	//
	// int nSize = mItems.size();
	// int nPos = 0;
	// for( int i=0; i < nSize; i ++){
	// SpeakItemData data = mItems.get(i);
	// if(data.text.equals(text)){
	//
	// if(nPos == index) {
	// mItems.remove(i);
	// delete(data.id);
	// nSize--;
	// if(i < nSize) {
	// data = mItems.get(i);
	// if( data.type == SpeakItemData.Define.SPEAK_TYPE_TRANS){
	// mItems.remove(i);
	// delete(data.id);
	// nSize--;
	// }
	// }
	// }
	// nPos++;
	// }
	// }
	// }

	// 删除指定ID数据
	public void delete(Integer id) {

		// SQLiteDatabase writer = getWritableDatabase();
		// writer.execSQL("delete from " + tableName + " where dataid=?",new
		// Object[] { id });
		sqlLiteDom.deleteRecordById(id);
	}
}
