package com.example.grafologus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.support.v4.app.NavUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Partner extends Activity {
	ProgressDialog pd;
	SocketClass socket;
	EditText editText;
	TextView chattext;
	String longtext = "";
	Button send;
	String line = null;
	Intent intent;
	LinearLayout ll;
	int width = 0;int height = 0;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		   WindowManager w = getWindowManager();
		    Display d = w.getDefaultDisplay();
		    DisplayMetrics metrics = new DisplayMetrics();
		    d.getMetrics(metrics);
		     width = metrics.widthPixels;
		     height = metrics.heightPixels;
	}

	//Csatlakoz�s a szerverre
	public void Connect(String participant, String fullname) {

		socket = new SocketClass(participant, fullname);
		pd = new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setMessage("V�rakoz�s a m�sik f�l kapcsol�d�s�ra!");
		pd.show();
		new Thread() {
			public void run() {

				while (!socket.IsConnected()) {

				}
				pd.dismiss();
			}
		}.start();
	}

	//Chat ablak
	public void Chat() {

		setContentView(R.layout.chat);
		editText = (EditText) findViewById(R.id.text);
		chattext = (TextView) findViewById(R.id.chattext);
		chattext.setText(longtext);
		new Thread() {
			public void run() {
				while (true) {
					if (socket.IsChat() && socket.LineisChanged()) {
						line = socket.WhatIsLine();
						runOnUiThread(new Runnable() {
							public void run() {
								longtext += "m�sik f�l: " + line + "\n";
								chattext.setText(longtext);
							}
						});
					}
				}
			}
		}.start();

	}
	
	public void OnClick_Send(View v) {
		if (!editText.getText().toString().equals("")) {
			socket.Printing("chat");
			socket.Printing(editText.getText().toString());
			longtext += "Magam:" + editText.getText().toString() + "\n";
			editText.setText("");
			chattext.setText(longtext);

		} else {
			Toast.makeText(getApplicationContext(), "�res mez�!",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_chat:
			Chat();
			return true;
		case R.id.cancel:
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onDestroy() {
		Log.v("", "Partner - Destroy");
		super.onDestroy();
		if (pd != null && pd.isShowing()) {
			pd.cancel();
		}
		if (isFinishing()) {
			socket.Printing("quit\n"); // Kil�p�s a kapcsolatb�l
			socket.Exit();
			finish();
		}

	}

	public SocketClass Socket() {return socket;}

};
