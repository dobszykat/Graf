package com.example.grafologus;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends Activity {

	EditText name;
	EditText id;
	Button login;
	RadioButton pat;
	RadioButton graph;
	Intent intent;
	String line = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialUISetup();
	}

	public void initialUISetup() {
		graph = (RadioButton) findViewById(R.id.graphologist);
		pat = (RadioButton) findViewById(R.id.patient);
		name = (EditText) findViewById(R.id.name);
		id = (EditText) findViewById(R.id.id);
		login = (Button) findViewById(R.id.login);

	}
	
	//Internet kapcsolat vizsgálat
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	//Belépés
	public void onClick_Login(View v) {
		if (isNetworkAvailable()) {
			final String fname = name.getText() + "\n"; // Bejelentkezett név
			String idtext = id.getText() + "\n";
			Log.i("id",idtext);
			if (graph.isChecked()) {
				intent = new Intent(this, Graphologist.class);

			} else if (pat.isChecked()) {
				intent = new Intent(this, Patient.class);
			}
			intent.putExtra("id", idtext);
			intent.putExtra("Name", fname);
			startActivity(intent);
		} else {
			AlertDialog.Builder netNotAvailable = new AlertDialog.Builder(MainActivity.this);
			netNotAvailable.setMessage("Nincs internetkapcsolat.");
			netNotAvailable.setTitle("Hiba");
			netNotAvailable.show();

		}
	}

}
