package com.ztspeech.simutalk2.dictionary.adapter;

import com.ztspeech.simutalk2.R;

import java.util.List;

import com.ztspeech.simutalk2.dictionary.util.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BaseLvAdapter extends BaseAdapter{

	public Context context;
	public List list;
	public View view;
	private TextView tvFooter;
	private ProgressBar pbFooter;
	public Integer chOrEn=0;
	public BaseLvAdapter(Context context,List list){
		this.context = context;
		this.list = list;
	}
	public View getFooter() {

		return view;
	}
	public void setCHorEN(Integer i){
		switch(i){
		case 0:
		case 3:
			chOrEn = 0;
			break;
		case 1:
		case 2:
			chOrEn = 1;
			break;
		}
		
		
		this.notifyDataSetChanged();
	}
	public void addFooter(ListView lv) {
		if(list.size()>=Util.COUNTINONEPAGE&&lv.getFooterViewsCount()==0) {
			view = LayoutInflater.from(context).inflate(R.layout.listview_footer, null);
			tvFooter = (TextView) view.findViewById(R.id.tvFooter);
			pbFooter = (ProgressBar) view.findViewById(R.id.pbFooter);
			lv.addFooterView(view);
		}
		
	}
	public void removeFooter(ListView lv){
		if(lv.getFooterViewsCount()>0){
			lv.removeFooterView(view);
		}
	}
//	public void footerBeClicked() {
//		tvFooter.setVisibility(View.GONE);
//		pbFooter.setVisibility(View.VISIBLE);
//	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return null;
	}

}
