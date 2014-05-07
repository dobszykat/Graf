package com.example.grafologus;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;

public class GetDrawings extends Activity {
	ListView drawinglist;
	ArrayAdapter<String> adapter;
	Intent intent;
	SocketClass socket;
	Graphologist_exam gv;
	int height = 0;
	int width = 0;
	// ArrayList<Coordinates> rajzok;
	ViewFlipper flipper;
	ArrayList<String> namelist;
	ArrayList<String> idlist;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.getdrawing);
		initialUISetup();
		Log.v("GetDrawings", "OnCreate");
		Intent intent = getIntent();
		Bundle extras = getIntent().getExtras();
		 height =extras.getInt("height");
		 width = extras.getInt("width");
		namelist = (ArrayList<String>) intent.getStringArrayListExtra("names");
		idlist = (ArrayList<String>) intent.getStringArrayListExtra("id");
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, namelist);
		drawinglist.setAdapter(adapter);
		drawinglist.setOnItemClickListener(clickListener);
		socket= SocketClass.getInstance();

	}

	public void initialUISetup(){
		flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		gv = new Graphologist_exam(getApplicationContext());
		drawinglist = (ListView) findViewById(R.id.list);
		FrameLayout frame = (FrameLayout) findViewById(R.id.viewdrawing);
		frame.addView(gv);
	}
	
	
	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, final View view,
				int position, long id) {
			Log.i("testy", "I Clicked on Row " + namelist.get(position) + " and it worked!");
			if(idlist.get(position)!=null){
				socket.Printing("GetDrawing");
				socket.Printing(idlist.get(position));
				Drawing();
			}
			
		}
	};
	
	public void Drawing() {
		flipper.showNext();
		new Thread() {
			public void run() {
				while (!socket.GetAllCoords()) {
					if (socket.AvailableCoordNum()>0) {
						gv.Drawing(socket.Coord(), height, width);
					}
				}
			}
		}.start();
	}

	public void OnClick_Back(View v) {
		gv.Reset();
		flipper.showPrevious();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onBackPressed() {
			finish();
	}
}
