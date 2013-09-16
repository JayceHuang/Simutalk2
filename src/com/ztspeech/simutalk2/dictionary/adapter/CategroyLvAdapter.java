package com.ztspeech.simutalk2.dictionary.adapter;

import com.ztspeech.simutalk2.R;

import java.util.List;
import com.ztspeech.simutalk2.dictionary.entity.Categroy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CategroyLvAdapter extends BaseLvAdapter{

	private Context context;
	private List list;
	public CategroyLvAdapter(Context context, List list) {
		super(context, list);
		this.context = context;
		this.list = list;	
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Categroy categroy = (Categroy) list.get(position);
		ViewHolder viewHolder = null;
		if(convertView==null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_categroy_item,null);
			viewHolder.tvCategroy = (TextView) convertView.findViewById(R.id.tvCategroy);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvCategroy.setText(categroy.getCategroyName());
		return convertView;
	}
	public static class ViewHolder{
		public TextView tvCategroy;
	}
}
