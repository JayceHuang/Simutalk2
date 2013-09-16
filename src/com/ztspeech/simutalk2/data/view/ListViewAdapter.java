package com.ztspeech.simutalk2.data.view;

import com.ztspeech.simutalk2.data.DataListObejct;
import com.ztspeech.simutalk2.data.view.ViewItemEvent.OnListViewItemClickListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ListViewAdapter extends BaseAdapter  implements OnListViewItemClickListener {

	
	private OnListViewItemClickListener mOnListViewItemClickListener = null;
	protected DataListObejct mDataList = null;
	protected Context mContext = null;
	
	public ListViewAdapter(Context context, DataListObejct list) {
		// TODO Auto-generated constructor stub
		super();
		mDataList = list;
		mContext = context;

	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		if(mDataList == null){
			return 0;
		}
		
		return mDataList.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		// TODO Auto-generated method stub
		ListViewItem v = null;
		if (arg1 == null) {
			v = newItemView(mContext);
			v.setOnListViewItemClickListener(this);
			arg1 = v;
		}
		else {
			v = (ListViewItem)arg1;
		}		

		v.setData(mDataList.get(arg0));	
		
		return arg1;
	}
	public abstract ListViewItem newItemView(Context context);
	
	
	public void OnListViewItemClick(int event, long id) {
		// TODO Auto-generated method stub
		
		if(mOnListViewItemClickListener != null){
			mOnListViewItemClickListener.OnListViewItemClick(event, id);
		}		
	}
	
	public void setOnListViewItemClickListener(OnListViewItemClickListener listener) {
		
		mOnListViewItemClickListener = listener;
	}
	
}
