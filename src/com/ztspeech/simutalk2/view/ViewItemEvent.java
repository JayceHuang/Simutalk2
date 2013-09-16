package com.ztspeech.simutalk2.view;

public class ViewItemEvent {

	public static final int OPEN = 0;
	public static final int EDIT = 1;
	public static final int DELETE = 2;
	public static final int DETAIL = 3;
	public static final int ADD = 6;
	public static final int SELECT_TRUE = 4;
	public static final int SELECT_FALSE = 5;

	public interface OnListViewItemClickListener {
		public void OnListViewItemClick(int event, long id);
	}
}
