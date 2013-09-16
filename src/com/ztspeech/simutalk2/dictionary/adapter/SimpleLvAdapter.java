package com.ztspeech.simutalk2.dictionary.adapter;

import com.ztspeech.simutalk2.R;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SimpleLvAdapter extends BaseLvAdapter{

	public SimpleLvAdapter(Context context, List list) {
		super(context, list);
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView==null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_simple,null);
			viewHolder.tv = (TextView) convertView.findViewById(R.id.tvSimple);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv.setText(list.get(position).toString());
		return convertView;
	}
	public static class ViewHolder{
		public TextView tv;
	}
}
