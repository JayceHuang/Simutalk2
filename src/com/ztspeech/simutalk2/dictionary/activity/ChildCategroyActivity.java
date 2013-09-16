package com.ztspeech.simutalk2.dictionary.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.adapter.ChildLvAdapter;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Child;
import com.ztspeech.simutalk2.dictionary.entity.Words;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class ChildCategroyActivity extends BaseActivity implements OnTouchListener, OnItemClickListener {

	private ListView lvCategroy;
	private EditText edtWord;
	private Integer categroyId;
	private List list;
	private SQLiteDom cd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent fromMainIntent = getIntent();
		categroyId = fromMainIntent.getIntExtra("categroyId", 0);
		String title = fromMainIntent.getStringExtra("categroyName");
		super.setTitleAndContentView(R.layout.activity_search, title);
		lvCategroy = (ListView) findViewById(R.id.lvCategroy);
		cd = new SQLiteDom();
		list = cd.getChildByCategroyId(categroyId);
		ChildLvAdapter cLv = new ChildLvAdapter(this, list);
		lvCategroy.setAdapter(cLv);
		lvCategroy.setOnItemClickListener(this);
		edtWord = (EditText) findViewById(R.id.edtInputWords);
		edtWord.setInputType(InputType.TYPE_NULL);
		edtWord.setOnTouchListener(this);

		/**
		 * 个人记录不显示搜索框
		 */
		if (categroyId == 2) {
			LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
			linearLayout1.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		// Intent intent = new
		// Intent(ChildCategroyActivity.this,WordsActivity.class);
		// intent.putExtra("childId", ((Child)list.get(arg2)).getChildId());
		// intent.putExtra("categroyId", categroyId);
		// intent.putExtra("title", ((Child)list.get(arg2)).getChildName());
		// startActivity(intent);

		Intent intent = new Intent(ChildCategroyActivity.this, ResultActivity.class);
		intent.putExtra("childId", ((Child) list.get(arg2)).getChildId());
		intent.putExtra("from", "detail");
		intent.putExtra("categroyId", categroyId);
		intent.putExtra("title", ((Child) list.get(arg2)).getChildName());
		startActivity(intent);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Intent intent = new
			// Intent(ChildCategroyActivity.this,WordsAutoCompletedActivity.class);
			// intent.putExtra("from", "yes");
			// intent.putExtra("categroyId", categroyId);
			// startActivityForResult(intent,
			// Util.REQUESTCODE_AUTOCOMPLETEDWORDS);
			// overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

			Intent intent = new Intent(ChildCategroyActivity.this, ResultActivity.class);
			intent.putExtra("from", "search");
			intent.putExtra("categroyId", categroyId);
			intent.putExtra("title", "搜索结果");
			startActivity(intent);
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == resultCode && resultCode == Util.REQUESTCODE_AUTOCOMPLETEDWORDS) {
			ArrayList<Words> wordsList = data.getParcelableArrayListExtra("wordsList");
			String strWords = data.getStringExtra("words");
			Intent intent = new Intent(ChildCategroyActivity.this, WordsActivity.class);
			intent.putParcelableArrayListExtra("wordsList", wordsList);
			intent.putExtra("words", strWords);
			intent.putExtra("categroyId", categroyId);
			intent.putExtra("title", getResources().getString(R.string.dictionary_result_title));
			startActivity(intent);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
