package com.ztspeech.simutalk2.qa.view;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;

public class DirWordSelectDialog extends Dialog implements OnClickListener {
	public interface SimuTalkSendDialogListener {
		public void onClick();
	}

	private TextView titleText;
	private Button cancelBtn;
	private Button okBtn;
	private RelativeLayout bottomLayout01;
	private SimuTalkSendDialogListener okListener;
	private SimuTalkSendDialogListener cancelLisenter;

	private ListView listview;
	private MyAdpter myAdapter;
	private ArrayList<String> datas = null;

	public DirWordSelectDialog(Context context) {
		super(context, R.style.dialog);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.list_layout);
		titleText = (TextView) findViewById(R.id.title);
		titleText.setText("结果选择:");
		cancelBtn = (Button) findViewById(R.id.BtnCancel);
		okBtn = (Button) findViewById(R.id.BtnOK);
		listview = (ListView) findViewById(R.id.smsList);
		myAdapter = new MyAdpter(context);
		listview.setAdapter(myAdapter);

		bottomLayout01 = (RelativeLayout) findViewById(R.id.layoutBottom01);
		cancelBtn.setOnClickListener(this);
		okBtn.setOnClickListener(this);
		okBtn.setText(context.getString(R.string.OK_Txt));
		cancelBtn.setText(context.getString(R.string.CANCEL_Txt));
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.BtnOK:
			dismiss();
			if (okListener != null) {
				okListener.onClick();
			}
			break;
		case R.id.BtnCancel:
			dismiss();
			if (cancelLisenter != null) {
				cancelLisenter.onClick();
			}
			break;
		}
	}

	/**
	 * 
	 * 设置对话框的标题
	 */
	public DirWordSelectDialog setTitle(String title) {
		this.titleText.setText(title);
		return this;
	}

	/**
	 * 重新设置确定和取消按钮监听器
	 */
	public DirWordSelectDialog setButton(SimuTalkSendDialogListener okListener,
			SimuTalkSendDialogListener cancelListener) {
		this.okListener = okListener;
		this.cancelLisenter = cancelListener;
		return this;
	}

	/**
	 * 设置确定和取消按钮的文本和监听器
	 */
	public DirWordSelectDialog setButton(String oktext, String cancelText, SimuTalkSendDialogListener okListener,
			SimuTalkSendDialogListener cancelListener) {
		bottomLayout01.setVisibility(View.GONE);
		if (oktext != null && !oktext.trim().equals("")) {
			okBtn.setText(oktext);
		}
		if (cancelText != null && !cancelText.trim().equals("")) {
			cancelBtn.setText(cancelText);
		}
		this.okListener = okListener;
		this.cancelLisenter = cancelListener;
		return this;
	}

	public DirWordSelectDialog setItems(ArrayList<String> datas, OnItemClickListener listener) {
		listview.setVisibility(View.VISIBLE);
		this.datas = datas;
		if (listener != null) {
			listview.setOnItemClickListener(listener);
		}
		myAdapter.notifyDataSetChanged();
		return this;
	}

	public void notifyDataSetChanged() {
		myAdapter.notifyDataSetChanged();
	}

	private class MyAdpter extends BaseAdapter {
		private LayoutInflater layoutInflater = null;

		public MyAdpter(Context context) {
			layoutInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			if (datas == null) {
				return 0;
			} else {
				return datas.size();
			}
		}

		public Object getItem(int position) {
			if (datas == null) {
				return null;
			} else {
				return datas.get(position);
			}
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				v = layoutInflater.inflate(R.layout.list_layout_item, null);
				viewHolder.result = (TextView) v.findViewById(R.id.smsname);
				v.setTag(viewHolder);
			} else {
				v = convertView;
				viewHolder = (ViewHolder) v.getTag();
			}
			viewHolder.result.setText(datas.get(position));
			return v;
		}

		private final class ViewHolder {
			public TextView result;
		}
	}
}
