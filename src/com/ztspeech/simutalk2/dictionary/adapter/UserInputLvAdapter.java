package com.ztspeech.simutalk2.dictionary.adapter;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.data.UserInfo;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class UserInputLvAdapter extends BaseLvAdapter{

	private Context context;
	private List list;
	private UserInfo mUser;
	public UserInputLvAdapter(Context context, List list) {
		super(context, list);
		this.context = context;
		this.list = list;
		mUser = UserInfo.getInstanse();
	}
	@Override
	public void addFooter(ListView lv) {
		if(list.size()>0) {
			view = LayoutInflater.from(context).inflate(R.layout.listview_footer, null);
			TextView tvFooter = (TextView) view.findViewById(R.id.tvFooter);
			tvFooter.setText("Çå³ý»º´æ");
			lv.addFooterView(view);
		}
		
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_words_simpleitem, null);
			viewHolder.tv = (TextView) convertView.findViewById(R.id.tvWords);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tv.setText(list.get(position).toString());
		changeFontSize(viewHolder);
		return convertView;
	}
	public static class ViewHolder{
		public TextView tv;
	}
	public void changeFontSize(ViewHolder viewHolder){
		viewHolder.tv.setTextSize(mUser.getFontSize());
	}
}
