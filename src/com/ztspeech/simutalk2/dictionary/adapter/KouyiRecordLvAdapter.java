package com.ztspeech.simutalk2.dictionary.adapter;

import java.util.List;
import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Categroy;
import com.ztspeech.simutalk2.dictionary.entity.Child;
import com.ztspeech.simutalk2.dictionary.entity.KouyiRecord;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class KouyiRecordLvAdapter extends BaseLvAdapter {

	private Context context;
	private View parentview;
	private List list;
	private Integer childId;
	private SQLiteDom sqliteDom = null;
	private Child child = null;
	private Categroy categroy = null;
	private UserInfo mUser;
	public KouyiRecordLvAdapter(Context context, List list, Integer childId,
			View parentview) {
		
		super(context, list);
		sqliteDom = new SQLiteDom();
		this.context = context;
		this.parentview = parentview;
		this.list = list;
		this.childId = childId;
		//child = sqliteDom.getChildById(childId);
		//categroy = sqliteDom.getCategroyById(child.getCategroyId());
		mUser = UserInfo.getInstanse();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final KouyiRecord kouyi = (KouyiRecord) list.get(position);
		KouyiRecordLvItem1 viewHolder = null;
		if (convertView == null) {
			
			viewHolder = new KouyiRecordLvItem1(context,this);
			
		}else{
			viewHolder = (KouyiRecordLvItem1) convertView;
		}
		viewHolder.setData(kouyi, categroy, child, context, childId);
		
		return viewHolder;
	}
}