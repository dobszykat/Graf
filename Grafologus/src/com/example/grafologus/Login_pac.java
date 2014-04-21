package com.example.grafologus;

import android.app.Activity;
import android.os.Bundle;

public class Login_pac extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyTouchEventView tv = new MyTouchEventView(this);
		setContentView(tv);
		addContentView(tv.vege,tv.params);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
