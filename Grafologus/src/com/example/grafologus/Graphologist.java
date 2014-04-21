package com.example.grafologus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class Graphologist extends Partner {
	EditText tosend;
	ProgressDialog pd;
	Graphologist_exam gv;
	Intent intent;
	String line = null;
	LinearLayout l1;
	Graphologist_exam tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v("Graphologist", "onCreate");
		Intent myIntent = getIntent();
		String fullname = myIntent.getStringExtra("firstKeyName");
		Log.i("Graf megkapta a nevet", fullname);
		setContentView(R.layout.login_graf);
		Connect("grafologus\n", fullname);
		tosend = (EditText) findViewById(R.id.tosend);
		
	}

	// Amit beírt a szövegmezõbe, azt elküldi a szervernek -> utasítás
	public void OnClick_betu(final View v) {
		line = tosend.getText() + "\n";
		tosend.setClickable(false);
		UtasitasKuld(v);
	}
	
	public void UtasitasKuld(final View v){
		Socket().Printing(line);
		new Thread() {
			public void run() {
				while (true) {
					if (Socket().GetCoords()) {
					
						runOnUiThread(new Runnable() {
						
							public void run() {
								Log.i("igeeen!","MOST kaptam koordinátákat!");
								IfGetCoords(v);
								
							}
						});
						
						break;
					}
				}
			}
		}.start();
	}

	public void IfGetCoords(View v){
		tosend.setClickable(true);
		ll = (LinearLayout)findViewById(R.id.grafologus);
		Log.i("button", "done");
		Button button= new Button(getApplicationContext());
		button.setText("Rajz megtekintése - " + tosend.getText());
		button.setBackgroundResource(R.drawable.buttonclicked);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setContentView(R.layout.graphologist_exam);
				tv = new Graphologist_exam(
						getApplicationContext(), Socket().Coord(), height, width);
				FrameLayout frame = (FrameLayout) findViewById(R.id.viewframelayout2);
				frame.addView(tv);
			}
			});
		ll.addView(button);
		
	}
	
	public void OnClick_Replay(View v){
		tv.Replay();
	}
	
	public void OnClick_SaveCoords(View v){
		setContentView(R.layout.login_graf);
	}
	
	public void OnClick_Again(View v){
		setContentView(R.layout.login_graf);
		UtasitasKuld(v);
		Socket().RemoveRow();
	}
	
	// Új kérdéssorok
	public void OnClick_NewQuestionnaire(View v) {
		intent = new Intent(this, NewQuestion.class);
		startActivity(intent);
	}

	// Meglévõ kérdéssorok
	public void OnClick_Questionnaire(View v) {
		intent = new Intent(this, QuestionsList.class);
		startActivity(intent);

	}

}
