package com.ztspeech.simutalk2.dictionary.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.entity.Collecter;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;

public class CollectedSimpleLvAdapter extends BaseLvAdapter{

	private Context context;
	private List list;
	private Integer enOrCh;
	public CollectedSimpleLvAdapter(Context context, List list,Integer enOrCh) {
		super(context, list);
		this.context = context;
		this.list = list;
		this.enOrCh = enOrCh;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Collecter collecter = (Collecter) list.get(position);
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_words_simpleitem,null);
			viewHolder.tvWords = (TextView) convertView.findViewById(R.id.tvWords);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		int ii = new PublicArithmetic().isWhat(collecter.getText1());
		if(enOrCh==0){
			switch(ii){
			case 0:
			case 3:
				viewHolder.tvWords.setText(collecter.getText1());
				break;
			case 1:
			case 2:
				viewHolder.tvWords.setText(collecter.getText2());
				break;
			}
		}else{
			switch(ii){
			case 0:
			case 3:
				viewHolder.tvWords.setText(collecter.getText2());
				break;
			case 1:
			case 2:
				viewHolder.tvWords.setText(collecter.getText1());
				break;
			}
		}
		return convertView;
	}
	public static class ViewHolder{
		public TextView tvWords;
	}
}
