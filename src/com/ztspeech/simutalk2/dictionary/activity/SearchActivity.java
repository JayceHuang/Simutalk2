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
import android.widget.ListView;

import com.ztspeech.simutalk2.R;
import com.ztspeech.simutalk2.dictionary.adapter.CategroyLvAdapter;
import com.ztspeech.simutalk2.dictionary.dom.SQLiteDom;
import com.ztspeech.simutalk2.dictionary.entity.Categroy;
import com.ztspeech.simutalk2.dictionary.entity.Words;
import com.ztspeech.simutalk2.dictionary.util.Util;

public class SearchActivity extends BaseActivity implements OnTouchListener, OnItemClickListener {

	private SQLiteDom cd = null;
	private ListView lvCategroy;
	private EditText edtWord;
	private List<Categroy> list;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// super.setTitleAndContentView(R.layout.activity_main, "分类检索");
		setContentView(R.layout.activity_search);
		lvCategroy = (ListView) findViewById(R.id.lvCategroy);
		cd = new SQLiteDom();
		list = cd.getAllCategroy();
		CategroyLvAdapter cLv = new CategroyLvAdapter(this, list);
		lvCategroy.setAdapter(cLv);
		lvCategroy.setOnItemClickListener(this);
		edtWord = (EditText) findViewById(R.id.edtInputWords);
		edtWord.setInputType(InputType.TYPE_NULL);
		edtWord.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// Intent intent = new
			// Intent(SearchActivity.this,WordsAutoCompletedActivity.class);
			// intent.putExtra("from", "yes");
			// startActivityForResult(intent,
			// Util.REQUESTCODE_AUTOCOMPLETEDWORDS);
			// overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

			Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
			intent.putExtra("from", "search");
			intent.putExtra("title", "搜索结果");
			// intent.putExtra("from", "yes");
			startActivity(intent);
			// overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		}

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (list.get(arg2).getCategroyName().equals(getResources().getString(R.string.categroy_collector))) {
			Intent intent = new Intent(SearchActivity.this, CollectedWordsActivity.class);
			intent.putExtra("childId", list.get(arg2).getCategroyId());
			startActivity(intent);
		} else if (list.get(arg2).getCategroyName().equals(getResources().getString(R.string.categroy_record))) {
			Intent intent = new Intent(SearchActivity.this, KouyiRecordActivity.class);
			intent.putExtra("childId", list.get(arg2).getCategroyId());
			startActivity(intent);
		} else {
			Intent intent = new Intent(SearchActivity.this, ChildCategroyActivity.class);
			intent.putExtra("categroyId", ((Categroy) list.get(arg2)).getCategroyId());
			intent.putExtra("categroyName", ((Categroy) list.get(arg2)).getCategroyName());
			startActivity(intent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == resultCode && resultCode == Util.REQUESTCODE_AUTOCOMPLETEDWORDS) {
			ArrayList<Words> wordsList = data.getParcelableArrayListExtra("wordsList");
			String strWords = data.getStringExtra("words");
			Intent intent = new Intent(SearchActivity.this, WordsActivity.class);
			intent.putParcelableArrayListExtra("wordsList", wordsList);
			intent.putExtra("words", strWords);
			intent.putExtra("title", getResources().getString(R.string.dictionary_result_title));
			startActivity(intent);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	// ImageGetter imageGetter = new ImageGetter(){
	//
	// @Override
	// public Drawable getDrawable(String source) {
	// int id = Integer.parseInt(source);
	// Drawable drawable = getResources().getDrawable(id);
	// return drawable;
	// }
	//
	// };

}
