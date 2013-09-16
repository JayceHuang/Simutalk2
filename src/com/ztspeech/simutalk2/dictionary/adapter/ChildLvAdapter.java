package com.ztspeech.simutalk2.dictionary.adapter;

import com.ztspeech.simutalk2.R;

import java.util.List;

import com.ztspeech.simutalk2.dictionary.entity.Child;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChildLvAdapter extends BaseLvAdapter{

	private Context context;
	private List list;
	public ChildLvAdapter(Context context, List list) {
		super(context, list);
		this.context = context;
		this.list = list;	
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Child child = (Child) list.get(position);
		ViewHolder viewHolder = null;
		if(convertView==null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_categroy_item,null);
			viewHolder.tvCategroy = (TextView) convertView.findViewById(R.id.tvCategroy);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvCategroy.setText(child.getChildName());
		return convertView;
	}
	public static class ViewHolder{
		public TextView tvCategroy;
	}

}
