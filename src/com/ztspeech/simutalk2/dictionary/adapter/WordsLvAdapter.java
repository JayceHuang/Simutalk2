package com.ztspeech.simutalk2.dictionary.adapter;

import java.util.List;

import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Categroy;
import com.ztspeech.simutalk2.dictionary.entity.Child;
import com.ztspeech.simutalk2.dictionary.entity.Words;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class WordsLvAdapter extends BaseLvAdapter{

	private Context context;
	// private ArrayList<Words> list;
	private List list;
	private Integer childId;
	private SQLiteDom sqliteDom = null;
	private Child child = null;
	private Categroy categroy = null;

//	private LocaleTTS mLocaleTTS;
//	private TTSPlayer mTtsPlayer;
	private UserInfo mUser;

	public WordsLvAdapter(Context context, List list, Integer childId) {
		super(context, list);
		sqliteDom = new SQLiteDom();
		this.context = context;
		this.list = list;
		this.childId = childId;
		mUser = UserInfo.getInstanse();
		if (childId != null) {
			//child = sqliteDom.getChildById(childId);
			//categroy = sqliteDom.getCategroyById(child.getCategroyId());
		}
		
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (childId == null) {
			//child = sqliteDom.getChildById(((Words) list.get(position)).getChildID());
			//categroy = sqliteDom.getCategroyById(child.getCategroyId());
		}
		// System.out.println(((Words)list.get(position)).getChinese());
		final Words word = (Words) list.get(position);
		// final Integer wordsId = word.getWordsId();
		WordsLvItem1 viewHolder = null;
		if (convertView == null) {
			viewHolder = new WordsLvItem1(context,this);
			
		} else {
			viewHolder = (WordsLvItem1) convertView;
		}

		viewHolder.setData(word, categroy, child, context, chOrEn);		
	
		return viewHolder;
	}

	

}
