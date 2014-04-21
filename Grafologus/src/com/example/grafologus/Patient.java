package com.example.grafologus;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Patient extends Partner {

	MyTouchEventView tv;
	String line = null;
	SocketClass socket;
	ProgressDialog pd;
	EditText editText;
	TextView chattext;
	String longtext = "";
	Button finish;
	boolean kap = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent myIntent = getIntent(); // gets the previously created intent
		String fullname = myIntent.getStringExtra("firstKeyName");
		setContentView(R.layout.touceventview);
		tv = new MyTouchEventView(getApplicationContext());
		FrameLayout frame = (FrameLayout) findViewById(R.id.viewframelayout);
		frame.addView(tv);

		//frame.addView(tv.vege, tv.params);		//kirajzoló gomb
		finish = (Button) findViewById(R.id.finish);

		Connect("paciens\n", fullname);
		Examination();

	}

	public void OnClick_Finish(View v) {
		kap = true;
		Log.i("páciens", "befejezte a rajzolást");
		Coordinates c = new Coordinates();
		c.koordtombx = tv.Xkoords();
		c.koordtomby = tv.Ykoords();
		c.speedarray = tv.Speedkoords();
		c.height = height;
		c.width = width;
		
		try {
			Socket().SendArray(c);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tv.Reset();
	}

	public void Examination() {

		new Thread() {
			public void run() {
				while (true) {
					if (Socket().LineisChanged() && !Socket().IsChat()) {
						line = Socket().WhatIsLine();
						runOnUiThread(new Runnable() {
							
							public void run() {
								if(kap){
								new AlertDialog.Builder(Patient.this)
										.setTitle("Grafológus üzenete")
										.setMessage(
												"Kérem rajzolja be a következõt!: "
														+ line)
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,int which) {}
												}).show();
									kap = false;
								}
							}
						
						});
							finish.setClickable(true);
					}
				}
			}
		}.start();
	}

}
