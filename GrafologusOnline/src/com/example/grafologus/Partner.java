package com.example.grafologus;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ScrollView;

public class Partner extends Activity {

	// Deklarációk
	ProgressDialog pd;
	SocketClass socket;
	String longtext = "";
	String line = null;
	Intent intent;
//	int width = 0;
//	int height = 0;
	boolean chat = false;
	ScrollView sv;
	static int pBarMax = 60;
	boolean chatalertshow = false;
	int second = 0;
	Builder alert;
	boolean firstmessage = true;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager w = getWindowManager();
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
//		width = metrics.widthPixels;
//		height = metrics.heightPixels;
		alert = new AlertDialog.Builder(Partner.this);
	}

	public void SetAlertDialog(String title, String message, boolean cancelable, boolean finish) {
		alert.setTitle(title).setMessage(message)
				.setCancelable(cancelable);
		if(finish){
			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog,
								int which) {
							finish();
						}
			});
		}
		else {
			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog,
								int which) {
						}
			});
		}

	}
	

	// Csatlakozás a szerverre
	public void Connect(String participant, String fullname, String id) {
		socket = SocketClass.getInstance();
		socket.ConnectToPartner(participant, fullname, id);
		pd = new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setMessage("Várakozás a másik fél kapcsolódására!");
		pd.show();
		new Thread() {
			public void run() {
				while (!socket.IsConnected() && second < pBarMax) {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (second == pBarMax - 1) {
						SetAlertDialog(
								"Hiba",
								"Jelenleg nem elérhetõ másik fél. Kérem próbálkozzon késõbb!",
								false, true);
						runOnUiThread(new Runnable() {

							public void run() {
								alert.show();
							}
						});
					}
					second++;
				}
				pd.dismiss();
			}
		}.start();
		Thread.interrupted();
		ChatTextListener();
	}

	public void Video() {

	}

	// Chat üzeneteket figyeli, illetve a kapcsolatmegszakadást
	public void ChatTextListener() {
		SetAlertDialog("Chat", "Üzenete érkezett", true, false);

		new Thread() {
			public void run() {
				while (true) {
					//Amikor nincs megnyitva a chat ablak, a másik fél szövegét összefûzi
					if(socket.IsChat() && !chat){
						longtext += "Másik fél: " + socket.WhatIsLine() + "\n";
						if(firstmessage){
							runOnUiThread(new Runnable() {
								public void run() {
										alert.show();
								}
							});
							firstmessage = false;
						}
					}
					if (socket.IsFinishedExam()) {
						SetAlertDialog("Vége",
								"Vége a vizsgálatnak! Viszontlátásra!", false, true);
						runOnUiThread(new Runnable() {
							public void run() {
								alert.show();
							}
						});
						break;
					} else if (socket.IsConnectionBreak()) {
						SetAlertDialog(
								"Hiba",
								"Megszakadt a kapcsolat!\n Kérem próbáljon újra kapcsolódni!",
								false, true);
						runOnUiThread(new Runnable() {
							public void run() {
								alert.show();
							}
						});
						break;
					}
				}
			}
		}.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (isFinishing()) {
			socket.Exit();
			finish();
		}
	}

	// Menü
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v("", "Partner - Destroy");
		if (pd != null && pd.isShowing()) {
			pd.cancel(); pd.dismiss();
		}
		Thread.currentThread().interrupt();
		socket.Exit();
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.chat:
		chat = true;
		intent = new Intent(this, Chat.class);
		intent.putExtra("messages", longtext);
		startActivityForResult(intent,1);
			return true;
		case R.id.video:
			Video();
			return true;
		case android.R.id.home:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (1): {
			if (resultCode == Activity.RESULT_OK) {
				longtext= data.getStringExtra("longtext");
				chat = false;
				firstmessage = true;
			}
		}
		}

	}
};
