package com.ztspeech.simutalk2.qa.view;

import com.ztspeech.simutalk2.data.DataListObejct;
import com.ztspeech.simutalk2.data.DataObject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class DataListAdapter extends BaseAdapter {

	private Context mContext = null;
	private DataListObejct mDataList;
	
	public DataListAdapter(Context context, DataListObejct list) {
		super();
		this.mContext = context;
		mDataList = list;
	}
	
	public DataListAdapter(Context context) {
		super();
		this.mContext = context;
	}	
	
	public void setDataList(DataListObejct list){
		mDataList = list;
	}
	
	public int getCount() {

		if(mDataList == null){
			return 0;
		}
		return mDataList.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {

		return arg0;
	}
		
	public abstract AdapterItemView getAdapterItemView(Context context);
	
	
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		AdapterItemView item = null;
		if (arg1 == null) {
			item = getAdapterItemView(mContext);
			item.init(mContext);
			arg1 = item;
		}
		else{
			item = (AdapterItemView)arg1;
		}
		item.clearCache();
		DataObject data = mDataList.get(arg0);
		item.setData(data);			
		
		return item;
	}
}
