package com.ztspeech.simutalk2.trans.speak;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ztspeech.simutalk2.R;

public class AnimSmoothLvBaseAdapter extends BaseAdapter implements OnScrollListener {

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	private Context context;
	private List list;
	private Animation sa;
	private int currentapiVersion = 0;
	private int scrollState;

	public AnimSmoothLvBaseAdapter(Context context, List list, ListView lv) {
		this.context = context;
		this.list = list;
		scrollState = list.size();
		//sa = AnimationUtils.loadAnimation(context, R.anim.zoom_enter);
		sa = AnimationUtils.loadAnimation(context, R.anim.wave_scale);
		sa.setFillAfter(true);

		currentapiVersion = Integer.valueOf(Build.VERSION.SDK);
		lv.setOnScrollListener(this);
		this.addFootView(lv);
	}

	private boolean isShowAnim = false;

	public void startAnim(View view, int position) {
		if (isShowAnim) {
			if (position == list.size() - 1) {
				view.startAnimation(sa);
				isShowAnim = false;
			}
		}
	}

	public void clearCache(View convertView) {
		convertView.clearAnimation();
	}

	public void notifyDataSetChanged(boolean isShowAnim) {
		this.isShowAnim = isShowAnim;
		super.notifyDataSetChanged();
	}

	public void addFootView(ListView lv) {
		LinearLayout ll = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, context.getResources().getDimensionPixelSize(R.dimen.footerview_hight));
		LinearLayout ll2 = new LinearLayout(context);
		ll.addView(ll2, params);
		//lv.addFooterView(ll);
		lv.addFooterView(ll,null,false);
		
	}

	@SuppressLint("NewApi")
	public void smoothToBottomWithAnim(ListView lv) {
		if (currentapiVersion > 8) {
			// 方案一 往前滚动超过20条时无动画 直接跳到最后
			// 有bug 个别情况无法跳转到最后
			if (list.size() - scrollState >= 5) {
				lv.setSelection(list.size());
				lv.setSelected(true);
			} else {
				lv.smoothScrollToPosition(list.size());
			}
			// 方案二 不往前滚动时添加有动画，往前滚动后添加条目直接无动画
			// if(list.size()-1-scrollState>20){
			// lv.setSelection(list.size()-1);
			// lv.setSelected(true);
			// }else{
			// lv.smoothScrollToPosition(list.size()-1);
			// if(scrollState!=this.getCount()-1){
			// lv.setSelection(list.size()-1);
			// lv.setSelected(true);
			// }
			// }
		} else {
			lv.setSelection(list.size());
			lv.setSelected(true);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		scrollState = firstVisibleItem + visibleItemCount - 1;

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
}
