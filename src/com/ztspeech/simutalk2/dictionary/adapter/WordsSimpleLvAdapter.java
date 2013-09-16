package com.ztspeech.simutalk2.dictionary.adapter;

import com.ztspeech.simutalk2.R;

import java.util.List;

import com.ztspeech.simutalk2.data.UserInfo;
import com.ztspeech.simutalk2.dictionary.entity.Words;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WordsSimpleLvAdapter extends BaseLvAdapter{

	private Context context;
	private List list;
	private Integer enOrCh;
	private UserInfo mUser;
	public WordsSimpleLvAdapter(Context context, List list,Integer enOrCh) {
		super(context, list);
		this.context = context;
		this.list = list;
		this.enOrCh = enOrCh;
		mUser = UserInfo.getInstanse();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Words word = (Words) list.get(position);
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_words_simpleitem,null);
			viewHolder.tvWords = (TextView) convertView.findViewById(R.id.tvWords);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(enOrCh==0){
			viewHolder.tvWords.setText(word.getChinese());
		}else{
			viewHolder.tvWords.setText(word.getEnglish());
		}
		changeFontSize(viewHolder);
		return convertView;
	}
	public static class ViewHolder{
		public TextView tvWords;
	}
	public void changeFontSize(ViewHolder viewHolder){
		viewHolder.tvWords.setTextSize(mUser.getFontSize());
	}
}
