package com.example.grafologus;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class QuestionsList extends ListActivity {

	ArrayList<String> NameList = new ArrayList<String>();
	ArrayList<String> arr = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	int clickCounter = 0;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		arr = SerializeObject.Read(QuestionsList.this, "namelist.dat");
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arr);
		setListAdapter(adapter);
		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Log.i("ahova kattintottam: ", arr.get(position));
			}
		});

	}

	public void addItems(View v) {
		adapter.notifyDataSetChanged();
	}
}
