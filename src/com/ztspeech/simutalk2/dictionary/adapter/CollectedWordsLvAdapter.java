package com.ztspeech.simutalk2.dictionary.adapter;

import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Categroy;
import com.ztspeech.simutalk2.dictionary.entity.Child;
import com.ztspeech.simutalk2.dictionary.entity.Collecter;

public class CollectedWordsLvAdapter extends BaseLvAdapter{

	private Context context;
	private List list;
	private Integer childId;
	private SQLiteDom sqliteDom = null;
	private Child child = null;
	private Categroy categroy = null;
	
	private Integer chOrEn=1;

	private UserInfo mUser;
	public CollectedWordsLvAdapter(Context context, List list, Integer childId,
			View parentview) {
		super(context, list);
		this.context = context;
		this.list = list;
		this.childId = childId;
		sqliteDom = new SQLiteDom();
		mUser = UserInfo.getInstanse();
	}

	public void setCHorEN(Integer i){
		chOrEn = i;
		
		this.notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//child = sqliteDom.getChildById(((Collecter)list.get(position)).getChildId());
		//categroy = sqliteDom.getCategroyById(child.getCategroyId());
		final Collecter collecter = (Collecter) list.get(position);
		CollectedLvItem1 viewHolder = null;
		if (convertView == null) {
			viewHolder = new CollectedLvItem1(context,this);
			
		}else{
			viewHolder = (CollectedLvItem1) convertView;
		}
		viewHolder.setData(collecter, categroy, child, context, chOrEn);
		return viewHolder;
	}
}
