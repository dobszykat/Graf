package com.example.grafologus;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import java.io.BufferedReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class MainActivity extends Activity {

	EditText name;
	Button login;
	RadioButton pac;
	RadioButton graf;
	Intent intent;
	String line = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initialUISetup();
	}

	public void initialUISetup() {
		graf = (RadioButton) findViewById(R.id.grafologus);
		pac = (RadioButton) findViewById(R.id.paciens);
		name = (EditText) findViewById(R.id.name);
		login = (Button) findViewById(R.id.login);

	}
	
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
			
			if (graf.isChecked()) {
				intent = new Intent(this, Graphologist.class);

			} else if (pac.isChecked()) {
				intent = new Intent(this, Patient.class);
			}
			
			intent.putExtra("firstKeyName", fname);
			startActivity(intent);
			finish();
		} else {
			AlertDialog.Builder netNotAvailable = new AlertDialog.Builder(MainActivity.this);
			netNotAvailable.setMessage("Nincs internetkapcsolat.");
			netNotAvailable.setTitle("Hiba");
			netNotAvailable.setIcon(R.drawable.ic_launcher);
			netNotAvailable.show();

		}
	}

}
