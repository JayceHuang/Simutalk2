package com.ztspeech.simutalk2.dictionary.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ztspeech.simutalk2.R;

public class SkinActivity extends BaseActivity {

	private ListView lvSkin;
	private List<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.slideinright, R.anim.slideoutleft);
		super.onCreate(savedInstanceState);
		setTitleAndContentView(R.layout.activity_setskin, "皮肤设置");
		lvSkin = (ListView) findViewById(R.id.lvSkin);
		list = new ArrayList<String>();
		list.add("经典白色");
		list.add("天空蓝色");
		list.add("神秘黑色");
		lvSkin.setAdapter(lvSkinItem);

	}

	BaseAdapter lvSkinItem = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tvSkin;
			if (convertView == null) {
				convertView = LayoutInflater.from(SkinActivity.this).inflate(R.layout.listview_skin_item, null);
				tvSkin = (TextView) convertView.findViewById(R.id.tvSkinName);

				convertView.setTag(tvSkin);
			} else {
				tvSkin = (TextView) convertView.getTag();
			}
			tvSkin.setText(list.get(position));
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

	};
}
