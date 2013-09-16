package com.ztspeech.simutalk2.trans.speak;

import com.ztspeech.simutalk2.data.TransTextTable;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.util.PublicArithmetic;
import com.ztspeech.simutalk2.trans.speak.SpeakView.IOnSpeakViewAdapterCallback;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;


public class SpeakAdapter extends  AnimSmoothLvBaseAdapter {

	private Context mContext = null;
	private IOnSpeakViewAdapterCallback mSpeakAdapterCallback = null;
	private float mFontSize = 20;
	public void setOnSpeakViewAdapterListener(IOnSpeakViewAdapterCallback listener){		
		mSpeakAdapterCallback = listener;
	}
	
	
	public SpeakAdapter(Context context, TransTextTable table,ListView lv) {
		super(context, table.mItems, lv);
		this.mContext = context;
		mTable = table;
	}
	
	public void setFontSize( float size){
		
		mFontSize = size;
	}
	
	public void addItem(SpeakItemData obj){
	
		int nSize = mTable.size();			
		if(nSize > 0){
			SpeakItemData prev = mTable.get(nSize-1);
			prev.focus = false;		
			
		}
		
		mTable.add(nSize, obj);			
	}

	public void update(SpeakItemData obj){
		int nIndex = mTable.size()-1;
		mTable.mItems.remove(nIndex);
		mTable.mItems.add(nIndex,obj);
	}
	
	public void updateAfterSpeak(SpeakItemData obj){
		int nIndex = mTable.size()-1;
		mTable.update(nIndex, obj);
	}
	public void commont(int nIndex,SpeakItemData obj){
		mTable.commont(nIndex,obj);
	}
	
	public int removeItem(int nIndex,SpeakItemData data){
		mTable.mItems.remove(nIndex);
		int index = mTable.remove(data);
//		new PublicArithmetic().tongbuKouyiRecord(mTable.findSaid(data), index);
		return index;
	}	

	public void clear(){
		mTable.clear();	
		new SQLiteDom().deleteAllRecord();
	}	
	
	public int getCount() {
		// TODO Auto-generated method stub
		return mTable.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {

		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub

		if (arg1 == null) {
			SpeakView v = new SpeakView(mContext, mTable.get(arg0));
			v.setOnCallbackListener(mSpeakAdapterCallback);
			arg1 = v;
		}
		this.clearCache(arg1);
		SpeakView v = (SpeakView)arg1;
		SpeakItemData data = mTable.get(arg0);
		
		v.setFontSize(mFontSize);
		v.setData(data);			
//		if(data.type == SpeakItemData.Define.SPEAK_TYPE_FULL){
//			v.setPadding(0, 0, 0, (arg2.getHeight() >> 1) - 50);
//		}
//		else {
//			v.setPadding(0, 0, 0, 0);
//		}

		this.startAnim(v, arg0);
		return v;
	}
	
	// 展示的文字

	private TransTextTable mTable;
	public void setFocus(SpeakItemData data) {
		data.isFocus = data;
		this.notifyDataSetChanged();
	}
	
}
