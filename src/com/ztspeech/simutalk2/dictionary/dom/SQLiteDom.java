package com.ztspeech.simutalk2.dictionary.dom;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.entity.Categroy;
import com.ztspeech.simutalk2.dictionary.entity.Child;
import com.ztspeech.simutalk2.dictionary.entity.Collecter;
import com.ztspeech.simutalk2.dictionary.entity.KouyiRecord;
import com.ztspeech.simutalk2.dictionary.entity.Words;
import com.ztspeech.simutalk2.dictionary.util.LogInfo;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class SQLiteDom {

	private static SQLiteDatabase database = null;
	private static SQLiteDatabase database2 = null;

	private boolean isOpen = false;

	public boolean getIsOpen() {
		return isOpen;
	}

	public SQLiteDom() {

	}

	public void openDB1() {
		if (database == null) {
			database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
		}
	}

	public void openDB2() {
		isOpen = true;
		if (database2 == null) {
			database2 = SQLiteDatabase.openOrCreateDatabase(databaseFilename2, null);
		}
	}

	private String databaseFilename = Util.DATABASE_PATH + "/" + Util.DATABASE_FILENAME;
	private String databaseFilename2 = Util.DATABASE_PATH + "/" + Util.DATABASE_FILENAME2;

	public void closeDataBase() {
		isOpen = false;
		if (database != null) {
			database.close();
		}
		if (database2 != null) {
			database2.close();
		}
	}

	// 批量插入 短语
	public void insertNewWords(List<Words> list) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
		// null);
		Words word = null;
		for (int i = 0; i < list.size(); i++) {
			word = list.get(i);
			insertNewWord(word);
		}
	}

	// 插入单条短语
	public void insertNewWord(Words word) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
		// null);
		ContentValues cv = new ContentValues();
		if (word != null) {
			cv.put(Util.WORDS_CHILDID, word.getChildID());
			cv.put(Util.WORDS_CHINESE, word.getChinese());
			cv.put(Util.WORDS_ENGLISH, word.getEnglish());
			cv.put(Util.WORDS_HEAT, word.getWordsHeat());
			database.insert(Util.WORDS, null, cv);
		}
	}

	// 查询全部分类信息
	public List getAllCategroy() {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
		// null);
		String sql = "select * from " + Util.CATEGROY;
		Cursor cursor = database.rawQuery(sql, null);
		List list = new ArrayList();
		Categroy categroy = null;
		while (cursor.moveToNext()) {
			categroy = new Categroy();
			categroy.setCategroyId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CATEGROY_ID))));
			categroy.setCategroyName(cursor.getString(cursor.getColumnIndex(Util.CATEGROY_NAME)));
			categroy.setCategroyHeat(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CATEGROY_HEAT))));
			list.add(categroy);
			categroy = null;
		}
		cursor.close();
		// database.close();
		return list;
	}

	// 通过categroy_id查询子类信息
	public List getChildByCategroyId(Integer categroyId) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
		// null);
		String sql = "select * from " + Util.CHILD + " where categroy_id=" + categroyId;
		Cursor cursor = database.rawQuery(sql, null);
		List list = new ArrayList();
		Child child = null;
		while (cursor.moveToNext()) {
			child = new Child();
			child.setChildId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CHILD_ID))));
			child.setCategroyId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CHILD_CATEGROYID))));
			child.setChildName(cursor.getString(cursor.getColumnIndex(Util.CHILD_NAME)));
			child.setChildHeat(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CHILD_HEAT))));
			list.add(child);
			child = null;
		}
		cursor.close();
		// database.close();
		return list;
	}

	// 通过单词或句子查询相关句子
	public List getSimilarResult(String chinese, String english, Integer childId, int page) {

		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
		// null);
		String sql = null;
		Cursor cursor = null;
		int countInOnePage = Util.COUNTINONEPAGE;
		// int count = 0;
		// sql = "select count(*) as count from words;";
		// cursor = database.rawQuery(sql,null);
		// if (cursor.getCount() > 0)
		// {
		// cursor.moveToFirst();
		// count = cursor.getInt(cursor.getColumnIndex("count"));
		// }

		sql = "select * from " + Util.WORDS;
		if ((chinese == null || "".equals(chinese)) && english != null) {
			sql += " where " + Util.WORDS_ENGLISH + " like '%" + strFilter(english) + "%'";
		} else if ((english == null || "".equals(english)) && chinese != null) {
			sql += " where " + Util.WORDS_CHINESE + " like '%" + strFilter(chinese) + "%'";
		}
		if (childId == null || "".equals(childId)) {

		} else {
			if ((english == null || "".equals(english)) && (chinese == null || "".equals(chinese))) {
				sql += " where " + Util.WORDS_CHILDID + "=" + childId;
			} else {
				sql += " and " + Util.WORDS_CHILDID + "=" + childId;
			}
		}
		sql += " order by " + Util.WORDS_ID + " limit " + countInOnePage + " offset " + (page - 1) * countInOnePage;
		LogInfo.LogOut(">>>>>>>sql<<<<<<<<<", sql);
		cursor = database.rawQuery(sql, null);
		List list = new ArrayList();
		Words words = null;
		while (cursor.moveToNext()) {
			words = new Words();
			words.setWordsId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.WORDS_ID))));
			words.setChildID(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.WORDS_CHILDID))));
			words.setChinese(cursor.getString(cursor.getColumnIndex(Util.WORDS_CHINESE)));
			words.setEnglish(cursor.getString(cursor.getColumnIndex(Util.WORDS_ENGLISH)));
			words.setWordsHeat(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.WORDS_HEAT))));
			list.add(words);
			words = null;
		}
		cursor.close();
		// database.close();
		// Map map = new HashMap();
		// map.put("list", list);
		// map.put("count", count);
		return list;
	}

	// 通过category_id查询categroy
	public Categroy getCategroyById(Integer categroyId) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
		// null);
		String sql = "select * from " + Util.CATEGROY + " where " + Util.CATEGROY_ID + "=" + categroyId;
		Cursor cursor = database.rawQuery(sql, null);
		Categroy categroy = new Categroy();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			categroy.setCategroyId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CATEGROY_ID))));
			categroy.setCategroyName(cursor.getString(cursor.getColumnIndex(Util.CATEGROY_NAME)));
			categroy.setCategroyHeat(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CATEGROY_HEAT))));
		}
		cursor.close();
		// database.close();
		return categroy;
	}

	// 通过child_id查询child
	public Child getChildById(Integer childId) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
		// null);
		String sql = "select * from " + Util.CHILD + " where " + Util.CHILD_ID + "=" + childId;
		Cursor cursor = database.rawQuery(sql, null);
		Child child = new Child();
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			child.setChildId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CHILD_ID))));
			child.setCategroyId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CHILD_CATEGROYID))));
			child.setChildName(cursor.getString(cursor.getColumnIndex(Util.CHILD_NAME)));
			child.setChildHeat(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.CHILD_HEAT))));
		}
		cursor.close();
		// database.close();
		return child;
	}

	// 通过categroy_id查询words
	public List getSimilarResult(Integer categroyId, String chinese, String english, int page) {
		int countInOnePage = Util.COUNTINONEPAGE;
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
		// null);
		String sql = "select " + Util.WORDS_ID + "," + Util.WORDS + "." + Util.WORDS_CHILDID + "," + Util.CHILD + "."
				+ Util.CHILD_CATEGROYID + "," + Util.WORDS_CHINESE + "," + Util.WORDS_ENGLISH + "," + Util.WORDS_HEAT
				+ "," + Util.CHILD_HEAT + " from " + Util.WORDS + "," + Util.CHILD + " where " + Util.WORDS + "."
				+ Util.WORDS_CHILDID + "=" + Util.CHILD + "." + Util.CHILD_CATEGROYID;
		if ((chinese == null || "".equals(chinese)) && english != null) {
			sql += " and " + Util.WORDS_ENGLISH + " like '%" + strFilter(english) + "%'";
		} else if ((english == null || "".equals(english)) && chinese != null) {
			sql += " and " + Util.WORDS_CHINESE + " like '%" + strFilter(chinese) + "%'";
		}
		if (categroyId == null || "".equals(categroyId)) {

		} else {
			sql += " and " + Util.CHILD_CATEGROYID + "=" + categroyId;
		}
		sql += " order by " + Util.WORDS_ID + " limit " + countInOnePage + " offset " + (page - 1) * countInOnePage;
		LogInfo.LogOut(">>>>>>>sql<<<<<<<<<", sql);
		Cursor cursor = database.rawQuery(sql, null);
		List list = new ArrayList();
		Words words = null;
		while (cursor.moveToNext()) {
			words = new Words();
			words.setWordsId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.WORDS_ID))));
			words.setChildID(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.WORDS_CHILDID))));
			words.setChinese(cursor.getString(cursor.getColumnIndex(Util.WORDS_CHINESE)));
			words.setEnglish(cursor.getString(cursor.getColumnIndex(Util.WORDS_ENGLISH)));
			words.setWordsHeat(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.WORDS_HEAT))));
			list.add(words);
			words = null;
		}
		cursor.close();
		// database.close();
		return list;
	}

	// 查询用户录入记录
	public List getUserInput() {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		List list = new ArrayList<String>();
		String sql = "select * from " + Util.USERINPUT + " order by " + Util.USERINPUT_ID + " desc";
		Cursor cursor = database2.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			list.add(cursor.getString(cursor.getColumnIndex(Util.USERINPUT_STR)));
		}
		cursor.close();
		// database.close();
		return list;
	}

	// 存入录入记录,删除重复项后插入,并维持缓存数量上限
	public void saveUserInput(String str) {
		List list = getUserInput();
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql;
		sql = "delete from " + Util.USERINPUT + " where " + Util.USERINPUT_STR + "='" + strFilter(str) + "'";
		database2.execSQL(sql);
		ContentValues cv = new ContentValues();
		cv.put(Util.USERINPUT_STR, str);
		int size = list.size();
		if (size < Util.COUNTOFUSERINPUTHUANCUN) {
			database2.insert(Util.USERINPUT, null, cv);
		} else {
			int emp = (size - Util.COUNTOFUSERINPUTHUANCUN) + 1;
			sql = "delete from " + Util.USERINPUT + " where id in (select id from " + Util.USERINPUT + " order by "
					+ Util.USERINPUT_ID + " limit " + emp + " offset 0)";
			LogInfo.LogOut("<<<<<<<<<<<<sql>>>>>>>>>>>>", sql);
			database2.execSQL(sql);
			database2.insert(Util.USERINPUT, null, cv);
		}
		// database.close();
	}

	// 清除缓存
	public void deleteAllUserInput() {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql = "delete from " + Util.USERINPUT;
		database2.execSQL(sql);
		// database.close();
	}

	// 查询口译记录
	public List getSimilarResultInKouyi(String str, int page) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql = null;
		Cursor cursor = null;
		int countInOnePage = Util.COUNTINONEPAGE;
		// int count = 0;
		// sql = "select count(*) as count from words;";
		// cursor = database.rawQuery(sql,null);
		// if (cursor.getCount() > 0)
		// {
		// cursor.moveToFirst();
		// count = cursor.getInt(cursor.getColumnIndex("count"));
		// }
		sql = "select * from " + Util.KOUYIRECORD;
		if (str == null || "".equals(str)) {

		} else {
			sql += " where " + Util.KOUYIRECORD_SAID + " like '%" + strFilter(str) + "%' or "
					+ Util.KOUYIRECORD_TRANSLATED + " like '%" + strFilter(str) + "%'";
		}

		// sql += " order by record_id limit " + countInOnePage + " offset "
		// + (page - 1) * countInOnePage;
		LogInfo.LogOut(">>>>>>>sql<<<<<<<<<", sql);
		cursor = database2.rawQuery(sql, null);
		List list = new ArrayList();
		KouyiRecord kouyi = null;
		while (cursor.moveToNext()) {
			kouyi = new KouyiRecord();
			kouyi.setRecordId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_ID))));
			kouyi.setSaid(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_SAID)));
			kouyi.setTranslated(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_TRANSLATED)));
			kouyi.setDateTime(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_DATETIME)));
			kouyi.setId(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_IDS)));
			kouyi.setType(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_TYPE)));
			kouyi.setComment(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_COMMENT)));
			list.add(kouyi);
			kouyi = null;
		}
		cursor.close();
		// database.close();
		// Map map = new HashMap();
		// map.put("list", list);
		// map.put("count", count);
		return list;
	}

	// 通过说和译查找口译记录
	public List getKouyiBySaidandTranslate(String said) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql = null;
		Cursor cursor = null;
		int countInOnePage = Util.COUNTINONEPAGE;
		// int count = 0;
		// sql = "select count(*) as count from words;";
		// cursor = database.rawQuery(sql,null);
		// if (cursor.getCount() > 0)
		// {
		// cursor.moveToFirst();
		// count = cursor.getInt(cursor.getColumnIndex("count"));
		// }
		// sql =
		// "select * from "+Util.KOUYIRECORD+" where "+Util.KOUYIRECORD_SAID+"="
		// + strFilter(said) + " and "+Util.KOUYIRECORD_TRANSLATED+"="
		// + strFilter(translate);

		sql = "select * from " + Util.KOUYIRECORD + " where " + Util.KOUYIRECORD_SAID + "='" + strFilter(said) + "'";

		// sql += " order by record_id limit " + countInOnePage + " offset "
		// + (page - 1) * countInOnePage;
		LogInfo.LogOut(">>>>>>>sql<<<<<<<<<", sql);
		cursor = database2.rawQuery(sql, null);
		List list = new ArrayList();
		KouyiRecord kouyi = null;
		while (cursor.moveToNext()) {
			kouyi = new KouyiRecord();
			kouyi.setRecordId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_ID))));
			kouyi.setSaid(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_SAID)));
			kouyi.setTranslated(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_TRANSLATED)));
			kouyi.setDateTime(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_DATETIME)));
			kouyi.setId(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_IDS)));
			kouyi.setType(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_TYPE)));
			kouyi.setComment(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_COMMENT)));
			list.add(kouyi);
			kouyi = null;
		}
		cursor.close();
		// database.close();
		// Map map = new HashMap();
		// map.put("list", list);
		// map.put("count", count);
		return list;
	}

	public int getIndexOfRecord(Integer recordId) {
		String sql = null;
		Cursor cursor = null;
		int countInOnePage = Util.COUNTINONEPAGE;
		sql = "select * from " + Util.KOUYIRECORD + " where " + Util.KOUYIRECORD_ID + "=" + recordId;
		LogInfo.LogOut(">>>>>>>sql<<<<<<<<<", sql);
		cursor = database2.rawQuery(sql, null);
		KouyiRecord kouyi = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			kouyi = new KouyiRecord();
			kouyi.setRecordId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_ID))));
			kouyi.setSaid(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_SAID)));
			kouyi.setTranslated(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_TRANSLATED)));
			kouyi.setDateTime(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_DATETIME)));
			kouyi.setId(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_IDS)));
			kouyi.setType(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_TYPE)));
			kouyi.setComment(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_COMMENT)));
		}
		cursor.close();
		List<KouyiRecord> list = getKouyiBySaidandTranslate(kouyi.getSaid());
		KouyiRecord kk = null;
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			kk = list.get(i);
			if (kk.getRecordId() == recordId) {
				break;
			}
			index++;
		}
		return index;
	}

	// 添加口译记录
	public void insertRecord(KouyiRecord kouyi) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		// database.beginTransaction();
		ContentValues cv = new ContentValues();
		cv.put(Util.KOUYIRECORD_ID, kouyi.getRecordId());
		cv.put(Util.KOUYIRECORD_SAID, kouyi.getSaid());
		cv.put(Util.KOUYIRECORD_TRANSLATED, kouyi.getTranslated());
		String dateTime = new PublicArithmetic().getCurrentDateTime();
		cv.put(Util.KOUYIRECORD_DATETIME, dateTime);
		cv.put(Util.KOUYIRECORD_IDS, kouyi.getId());
		cv.put(Util.KOUYIRECORD_TYPE, kouyi.getType());
		cv.put(Util.KOUYIRECORD_COMMENT, kouyi.getComment());
		database2.insert(Util.KOUYIRECORD, null, cv);
		// database.setTransactionSuccessful();
		// database.endTransaction();
		// database.close();
	}

	public Integer getLastRecordId() {
		Integer id = null;
		String sql = "select " + Util.KOUYIRECORD_ID + " from " + Util.KOUYIRECORD + " order by " + Util.KOUYIRECORD_ID
				+ " desc limit 1";
		Cursor cursor = database2.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			id = cursor.getInt(cursor.getColumnIndex(Util.KOUYIRECORD_ID));
		}
		cursor.close();
		return id;
	}

	// 通过record_id删除口译记录并返回index
	public int deleteRecordByIdReturnIndex(Integer recordId) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		int index = getIndexOfRecord(recordId);
		String sql = "delete from " + Util.KOUYIRECORD + " where " + Util.KOUYIRECORD_ID + "=" + recordId;
		LogInfo.LogOut(">>>>>>>>>>sql<<<<<<<<<<<<", sql);
		database2.execSQL(sql);
		return index;
	}

	// 通过record_id删除口译记录
	public void deleteRecordById(Integer id) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql = "delete from " + Util.KOUYIRECORD + " where " + Util.KOUYIRECORD_ID + "=" + id;
		LogInfo.LogOut(">>>>>>>>>>sql<<<<<<<<<<<<", sql);
		database2.execSQL(sql);
		// database.delete(table, whereClause, whereArgs)
		// return true;
		// database.close();
	}

	// 清空口译记录
	public void deleteAllRecord() {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql = "delete from " + Util.KOUYIRECORD;
		LogInfo.LogOut(">>>>>>>>>>sql<<<<<<<<<<<<", sql);
		database2.execSQL(sql);
		// database.delete(table, whereClause, whereArgs)
		// return true;
		// database.close();
	}

	// 更新口译记录
	public void updateRecord(KouyiRecord kouyi) {
		String sql = "update " + Util.KOUYIRECORD + " set " + Util.KOUYIRECORD_SAID + "='" + strFilter(kouyi.getSaid())
				+ "'," + Util.KOUYIRECORD_TRANSLATED + "='" + strFilter(kouyi.getTranslated()) + "',"
				+ Util.KOUYIRECORD_TYPE + "='" + strFilter(kouyi.getType()) + "'," + Util.KOUYIRECORD_COMMENT + "='"
				+ strFilter(kouyi.getComment()) + "' where " + Util.KOUYIRECORD_ID + "=" + kouyi.getRecordId();
		LogInfo.LogOut(">>>>>>>>>>sql<<<<<<<<<<<<", sql);
		database2.execSQL(sql);
	}

	// 顶踩
	public void commentRecord(Integer id, int flag) {
		String sql = "update " + Util.KOUYIRECORD + " set " + Util.KOUYIRECORD_COMMENT + "='" + flag + "' where "
				+ Util.KOUYIRECORD_ID + "=" + id;
		LogInfo.LogOut(">>>>>>>>>>sql<<<<<<<<<<<<", sql);
		database2.execSQL(sql);
	}

	// 清空收藏夹
	public void deleteAllCollecter() {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql = "delete from " + Util.COLLECTER;
		LogInfo.LogOut(">>>>>>>>>>sql<<<<<<<<<<<<", sql);
		database2.execSQL(sql);
		// database.delete(table, whereClause, whereArgs)
		// return true;
		// database.close();
	}

	// 分类检索内容添加收藏 返回值-1表示插入失败,0表示已存在,其余表示插入成功
	public int insertCollecterFromSearch(Words words) {
		int result;
		Collecter emp = new Collecter();
		emp.setText1(words.getEnglish());
		emp.setText2(words.getChinese());
		Collecter collecter = getCollectedWordsByWordsId(emp);
		if (collecter == null) {
			// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
			// null);
			ContentValues cv = new ContentValues();
			cv.put(Util.COLLECTER_CHILDID, words.getChildID());
			cv.put(Util.COLLECTER_TEXT1, words.getEnglish());
			cv.put(Util.COLLECTER_TEXT2, words.getChinese());
			String dateTime = new PublicArithmetic().getCurrentDateTime();
			cv.put(Util.COLLECTER_DATETIME, dateTime);
			result = (int) database2.insert(Util.COLLECTER, null, cv);
			// database.close();
		} else {
			result = 0;
		}
		return result;
	}

	// 从口译结果添加收藏 返回值-1表示插入失败,0表示已存在,其余表示插入成功
	public int insertCollecterFromKouyi(Collecter collecter) {
		int result;
		Collecter newCollecter = getCollectedWordsByWordsId(collecter);
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		if (newCollecter == null) {
			ContentValues cv = new ContentValues();
			cv.put(Util.COLLECTER_CHILDID, collecter.getChildId());
			cv.put(Util.COLLECTER_TEXT1, collecter.getText1());
			cv.put(Util.COLLECTER_TEXT2, collecter.getText2());
			String dateTime = new PublicArithmetic().getCurrentDateTime();
			cv.put(Util.COLLECTER_DATETIME, dateTime);
			result = (int) database2.insert(Util.COLLECTER, null, cv);
			// database.close();
		} else {
			result = 0;
		}
		return result;
	}

	// 通过收藏查询该收藏是否存在
	public Collecter getCollectedWordsByWordsId(Collecter collecter) {
		String sql = "select * from " + Util.COLLECTER + " where " + Util.COLLECTER_TEXT1 + "='"
				+ strFilter(collecter.getText1()) + "' and " + Util.COLLECTER_TEXT2 + "='"
				+ strFilter(collecter.getText2()) + "'";
		LogInfo.LogOut(">>>>>>>>>>sql<<<<<<<<<<<<", sql);
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		Cursor cursor = database2.rawQuery(sql, null);
		Collecter newCollecter = null;
		if (cursor.getCount() > 0) {
			newCollecter = new Collecter();
			cursor.moveToFirst();
			newCollecter.setChildId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_CHILDID))));
			newCollecter.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_ID))));
			newCollecter.setText1(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_TEXT1)));
			newCollecter.setText2(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_TEXT2)));
			newCollecter.setDateTime(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_DATETIME)));
		} else {
			newCollecter = null;
		}
		cursor.close();
		// database.close();
		return newCollecter;
	}

	// 查询收藏
	public List getCollectedWords(String str, int page) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql = null;
		Cursor cursor = null;
		int countInOnePage = Util.COUNTINONEPAGE;
		sql = "select * from " + Util.COLLECTER;
		if (str == null || "".equals(str)) {

		} else {
			sql += " where " + Util.COLLECTER_TEXT1 + " like '%" + strFilter(str) + "%' or " + Util.COLLECTER_TEXT2
					+ " like '%" + strFilter(str) + "%'";
		}
		// sql += " order by record_id limit " + countInOnePage + " offset "
		// + (page - 1) * countInOnePage;
		LogInfo.LogOut(">>>>>>>sql<<<<<<<<<", sql);
		cursor = database2.rawQuery(sql, null);
		List list = new ArrayList();
		Collecter newCollecter = null;
		while (cursor.moveToNext()) {
			newCollecter = new Collecter();
			newCollecter.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_ID))));
			newCollecter.setChildId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_CHILDID))));
			newCollecter.setText1(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_TEXT1)));
			newCollecter.setText2(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_TEXT2)));
			newCollecter.setDateTime(cursor.getString(cursor.getColumnIndex(Util.COLLECTER_DATETIME)));
			list.add(newCollecter);
			newCollecter = null;
		}
		cursor.close();
		// database.close();
		// Map map = new HashMap();
		// map.put("list", list);
		// map.put("count", count);
		return list;
	}

	public void deleteCollectedWordsByWordsId(Integer id) {
		// database = SQLiteDatabase.openOrCreateDatabase(databaseFilename2,
		// null);
		String sql = "delete from " + Util.COLLECTER + " where " + Util.COLLECTER_ID + "=" + id;
		LogInfo.LogOut(">>>>>>>>>>sql<<<<<<<<<<<<", sql);
		database2.execSQL(sql);
		// database.delete(table, whereClause, whereArgs)
		// return true;
		// database.close();
	}

	// 过滤单引号
	public String strFilter(String str) {
		return str.replace("'", "''");
	}

	public String getCurrentdbVersion() {
		String sql = "select version from databaseversion order by id desc limit 1 offset 0";
		Cursor cursor = database2.rawQuery(sql, null);
		String version = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			version = cursor.getString(cursor.getColumnIndex("version"));
		}
		cursor.close();
		return version;
	}

	public void insertCurrentVersion(String version, char fromWhere) {
		ContentValues cv = new ContentValues();
		cv.put("version", version);
		switch (fromWhere) {
		case '0':
			cv.put("fromwhere", 0);// 0为从应用程序
			break;
		case '1':
			cv.put("fromwhere", 1);// 1为从网络
			break;
		default:
			cv.put("fromwhere", "error");// 错误
			break;
		}
		String dateTime = new PublicArithmetic().getCurrentDateTime();
		cv.put("datetime", dateTime);
		database2.insert("databaseversion", null, cv);
	}

	// 个人数据库版本升级
	public void updateUserDatabaseVersion() {
		String sql = null;
		try {
			sql = "ALTER TABLE " + Util.KOUYIRECORD + " ADD " + Util.KOUYIRECORD_TYPE + " TEXT DEFAULT 'type'";
			database2.execSQL(sql);
			sql = "ALTER TABLE " + Util.KOUYIRECORD + " ADD " + Util.KOUYIRECORD_IDS + " TEXT DEFAULT '0000'";
			database2.execSQL(sql);
			sql = "ALTER TABLE " + Util.KOUYIRECORD + " ADD " + Util.KOUYIRECORD_COMMENT + " TEXT DEFAULT '3'";
			database2.execSQL(sql);
		} catch (SQLException e) {

			e.printStackTrace();
			return;
		}
		changeOldDatabaseTypeValue();
	}

	// 给老版本数据加type值
	public void changeOldDatabaseTypeValue() {
		Cursor cursor = null;
		String sql = "select * from " + Util.KOUYIRECORD + " where " + Util.KOUYIRECORD_TYPE + "='type'";
		LogInfo.LogOut(">>>>>>>sql<<<<<<<<<", sql);
		cursor = database2.rawQuery(sql, null);
		List<KouyiRecord> list = new ArrayList();
		KouyiRecord kouyi = null;
		while (cursor.moveToNext()) {
			kouyi = new KouyiRecord();
			kouyi.setRecordId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_ID))));
			kouyi.setSaid(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_SAID)));
			kouyi.setTranslated(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_TRANSLATED)));
			kouyi.setDateTime(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_DATETIME)));
			kouyi.setId(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_IDS)));
			kouyi.setType(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_TYPE)));
			kouyi.setComment(cursor.getString(cursor.getColumnIndex(Util.KOUYIRECORD_COMMENT)));
			list.add(kouyi);
			kouyi = null;
		}
		cursor.close();
		for (int i = 0; i < list.size(); i++) {
			kouyi = list.get(i);
			int result = new PublicArithmetic().isWhat(kouyi.getSaid());
			switch (result) {
			case 0:
			case 3:
				kouyi.setType(UserInfo.S2T_CH2EN);
				break;
			case 1:
			case 2:
				kouyi.setType(UserInfo.S2T_EN2CH);
				break;
			}
			updateRecord(kouyi);
		}
	}
}
