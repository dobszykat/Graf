package com.example.grafologus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

public class Graphologist extends Partner {

	//Deklarációk
	ProgressDialog pd;
	Graphologist_exam gv;
	Intent intent;
	Button button;
	Button show;
	Button replay;
	EditText tosend;
	int layoutnumber = 0;
	ViewFlipper flipper;
	SimpleDateFormat today;
	String line = "";
	String graphid = "";
	String datestring = "";
	boolean cansend = true;
	boolean isquestionnare = false;
	boolean getdrawingslist = false;
	FrameLayout frame;
	int height = 0; int width = 0;
	ArrayList<String> drawingsnamelist = new ArrayList<String>();
	ArrayList<String> drawingidlist = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v("Graphologist", "onCreate");
		Intent myIntent = getIntent();
		graphid = myIntent.getStringExtra("id");
		String fullname = myIntent.getStringExtra("Name"); // ?? kell ez?
		setContentView(R.layout.login_graf);
		Connect("grafologus\n", fullname, graphid);
		initialUISetup();
	}

	@SuppressLint("SimpleDateFormat")
	public void initialUISetup() {
		flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		tosend = (EditText) findViewById(R.id.tosend);
		button = (Button) findViewById(R.id.sendquestion);
		replay = (Button) findViewById(R.id.replay);
		show = (Button) findViewById(R.id.show);
		gv = new Graphologist_exam(getApplicationContext());
		frame = (FrameLayout) findViewById(R.id.viewframelayout2);
		frame.addView(gv);
		pd = new ProgressDialog(this);
		show.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				show.setVisibility(View.INVISIBLE);
				flipper.showNext();
				Examination();
				gv.Reset();
				gv.Drawing(socket.Coord(), height, width);
				if (!isquestionnare) {
					button.setClickable(true);
					button.setEnabled(true);
				}
			}
		});
		today = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		datestring = today.format(date);
		Log.i("ma", datestring);

	}
boolean pdshow = false;

	// Érkezik a rajz
	public void Examination() {
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		new Thread() {
			public void run() {
				boolean firstRun = true;
				while(true) {
					if (gv.FinishDrawing() && socket.AvailableCoordNum()>0) {
						pd.dismiss(); pdshow = false;
						if(firstRun && socket.AvailableCoordNum()>5) firstRun = false;
						else gv.Drawing(socket.Coord(), height, width);
					} else if(socket.GetAllCoords() && socket.AvailableCoordNum()==0){
						Log.i("megvaaan a vege", "Most kilepek!!!!!!!!!");
						break;
					}
					else if(gv.FinishDrawing() && socket.AvailableCoordNum()==0 && !pdshow){
						runOnUiThread(new Runnable() {

							public void run() {
								pd.show();
								pdshow = true;
							}
							});
					}
				}

				Log.i("megkaptm teljensen kirajzolva", "most");
				
			}
		}.start();
	}

	// Egyszerû utasítást kérünk
	public void OnClick_SendAsking(final View v) {
		if (!tosend.getText().toString().equals("")) {
			button.setClickable(false);
			button.setEnabled(false);
			line = tosend.getText() + "\n";
			SendLine(v);
		} else {
			new AlertDialog.Builder(this)
					.setCancelable(false)
					.setTitle("Hiba")
					.setMessage("Üresen elküldött utasítás!")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}
	}

	public void SendQuestionnare(final ArrayList<String> a, final View v) {
		isquestionnare = true;
		button.setClickable(false);
		button.setEnabled(false);
		new Thread() {
			public void run() {
				for (int i = 0; i < a.size(); i++) {
					if (i == a.size() - 1) {
						isquestionnare = false;
					}
					line = a.get(i);
					SendLine(v);
					while (true) {
						if (cansend) {
							break;
						}
					}

				}

			}
		}.start();

	}

	public void Kirajzol(final View v) {
		new Thread() {
			public void run() {
				while (true) {
					if (socket.AvailableCoordNum()>0) {

						runOnUiThread(new Runnable() {

							public void run() {
								show.setText("Rajz megtekintése - " + line.trim());
								show.setVisibility(View.VISIBLE);

							}
						});

						break;
					}
				}
			}
		}.start();
	}

	public void SendLine(final View v) {
		cansend = false;
		socket.Printing(line);
		Kirajzol(v);
	}

	public void OnClick_Replay(View v) {
		replay.setEnabled(false);
		gv.Replay();
	}

	//Koordináták mentése
	public void OnClick_SaveCoords(View v) {
		drawingsnamelist.add(line.trim() + " - " + datestring);
		drawingidlist.add(null);
		flipper.showPrevious();
		cansend = true;
	}

	//Utasítás újraküldése - nem megfelelõ rajz esetén
	public void OnClick_Again(View v) {
		show.setVisibility(View.INVISIBLE);
		flipper.showPrevious();
		SendLine(v);
		socket.RemoveRow();
	}

	// Új kérdéssorok
	public void OnClick_NewQuestionnaire(View v) {
		intent = new Intent(this, NewQuestion.class);
		startActivity(intent);
	}

	// Meglévõ kérdéssorok
	public void OnClick_Questionnaire(View v) {
		intent = new Intent(this, QuestionsList.class);
		startActivityForResult(intent, 1);

	}

	//Korábbi rajzok megtekintése
	public void OnClick_Drawings(View v) {
		Log.i("kattintották a", "régi rajzokat");
		intent = new Intent(this, GetDrawings.class);
		if (!getdrawingslist) {
			socket.GetDrawingsList();
			pd.setMessage("Rajzok betöltése");
			pd.show();
			new Thread() {
				public void run() {
					while (true) {
						if (socket.IfGetDrawing()) {
							drawingsnamelist = socket.DrawingsName();
							drawingidlist = socket.DrawingsId();
							intent.putExtra("height", height);
							intent.putExtra("width", width);
							intent.putStringArrayListExtra("names",
									drawingsnamelist);
							intent.putStringArrayListExtra("id", drawingidlist);
							pd.dismiss();
							pd.cancel();
							getdrawingslist = true;
							startActivity(intent);
							break;
						}
					}
				}
			}.start();
		} else {
			intent.putStringArrayListExtra("names", drawingsnamelist);
			startActivity(intent);
		}

	}
	
	//Vizsgálat befejezése
	public void OnClick_FinishExam(View v) {
		socket.Printing("finishexam\n");
		finish();
	}

	//Kiválasztott kérdéssor nevével tér vissza
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (1): {
			if (resultCode == Activity.RESULT_OK) {
				String questionname = data.getStringExtra("key");
				ArrayList<String> a = new ArrayList<String>();

				String ser = SerializeObject.ReadSettings(Graphologist.this,
						"myobject" + questionname + ".dat");
				if (ser != null && !ser.equalsIgnoreCase("")) {
					Object obj = SerializeObject.stringToObject(ser);
					if (obj instanceof ArrayList) {
						a = (ArrayList<String>) obj;
					}
				}
				View view = this.getCurrentFocus();
				SendQuestionnare(a, view);
			}
		}
		}

	}

	@Override
	public void onBackPressed() {
		flipper.setDisplayedChild(0);
	}

	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	  super.onWindowFocusChanged(hasFocus);

	 height = frame.getMeasuredHeight();
	 width = frame.getMeasuredWidth();
		Log.i("graf", String.valueOf(height) + "-" + String.valueOf(width));
	 }
}
