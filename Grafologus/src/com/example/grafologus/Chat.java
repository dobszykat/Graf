package com.example.grafologus;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Chat extends Partner {
	EditText text;
	TextView chattext;
	String longtext="";
	SocketClass socket = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		Log.v("Chat", "onCreate");
		editText = (EditText) findViewById(R.id.text);
		chattext = (TextView) findViewById(R.id.chattext);
		chattext.setText(longtext);
		Log.i("CHAT", "kezdodik");
		ChatRun();
		
	}
	
	public void ChatRun(){
		new Thread() {
			public void run() {
				while (true) {
					if(Socket().LineisChanged()){
						Log.i("CHAT", "kezdodik3");
						line = Socket().WhatIsLine();
						runOnUiThread(new Runnable() {
							public void run() {
						longtext= "másik fél: " + line + "\n" + longtext;
						chattext.setText(longtext);
							}});
					}
				}
			}}.start();
	}
	
	public void OnClick_Send(View v) {
		if(!editText.getText().toString().equals("")){
		Socket().Printing("chat");
		Socket().Printing(editText.getText().toString());
		longtext+= "Magam:" + editText.getText().toString() + "\n";
		editText.setText("");
		chattext.setText(longtext);
		
		}
		else {
			Toast.makeText(getApplicationContext(), "Üres mezõ!", Toast.LENGTH_SHORT).show();
		}

	}

	
}