package com.ztspeech.simutalk2.net;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.ztspeech.simutalk2.dictionary.util.LogInfo;

/**
 * 为了略过一闪而过的图片,listview使用此类获取图片
 * 
 */
public class ListViewImageEngine implements OnScrollListener {
	ListView listview;
	WeakReference<Bitmap> bitmapReference = null;

	public ListViewImageEngine(ListView listview) {
		this.listview = listview;
		this.listview.setOnScrollListener(this);
	}

	/**
	 * 显示原图
	 */
	public void imageLoader(ImageView v, String imageId, int defaultDrawableId, int pos) {
		ImageEngine.setImageBitmap(imageId, v, defaultDrawableId, pos);
	}

	/**
	 * 显示缩略图
	 */
	public void imageLoaderScale(ImageView v, String imageId, int defaultDrawableId, int pos) {
		ImageEngine.setImageBitmapScale(imageId, v, defaultDrawableId, pos);
	}

	// ---------------------------------------------------------------

	private void loadImage() {
		int start = listview.getFirstVisiblePosition();
		int end = listview.getLastVisiblePosition();
		if (end >= listview.getCount()) {
			end = listview.getCount() - 1;
		}
		ImageEngine.setLoadLimit(start, end);
		ImageEngine.unlock();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
			LogInfo.LogOut("list", "-------------SCROLL_STATE_FLING");
			ImageEngine.lock();
			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
			LogInfo.LogOut("list", "-------------SCROLL_STATE_IDLE");
			loadImage();
			break;
		case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			LogInfo.LogOut("list", "--------------SCROLL_STATE_TOUCH_SCROLL");
			ImageEngine.lock();
			break;

		default:
			break;
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}
}
