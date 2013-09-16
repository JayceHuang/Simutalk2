package com.ztspeech.simutalk2.qa.view;

import com.ztspeech.simutalk2.data.DataObject;

import android.content.Context;
import android.widget.LinearLayout;

public abstract class AdapterItemView extends LinearLayout {

	protected DataObject mData = null;
	
	public AdapterItemView(Context context) {
		super(context, null);		
	}
	
	public DataObject getData(){
		return mData;
	}
	
	public abstract void setData(DataObject data );	
	public abstract void init(Context context);
	public abstract void clearCache();
}
