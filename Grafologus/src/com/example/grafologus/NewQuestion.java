package com.example.grafologus;
import java.util.ArrayList;

import com.example.grafologus.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class NewQuestion extends Activity {
	
	public ArrayList<EditText> TextList;
	EditText first;
	EditText name;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.questions);
		Log.v("OnCreate", "NewQuestion");
		TextList = new ArrayList<EditText>();
		first = (EditText) findViewById(R.id.firstquestion);
		name = (EditText) findViewById(R.id.nameofquestions);

	}

	public int count = 0;
	
	public void NewQuestionbtn(){
		Log.i("count", String.valueOf(count));
		if(count == 0){
			count++;
			TextList.add(first);
		}
		else{
			if(!TextList.get(count-1).getText().toString().equals("")){
			TextView textview = new TextView(this);
			EditText edittext = new EditText(this);
			textview.setText("Ide írja a " + String.valueOf(count+1) + ". kérdését!");
			edittext.setId(count);
			edittext.setBackgroundResource(R.drawable.textstyle);
			textview.setTextColor(getResources().getColor(R.color.appyellow));
			LinearLayout ll = (LinearLayout)findViewById(R.id.Questions);
			ll.addView(textview);
			ll.addView(edittext);
			TextList.add(edittext);
			count++;
			}
			else{
				Toast.makeText(getApplicationContext(), "Az elõzõ kérdés üresen maradt!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void SaveQuestions(){
		String getname =  name.getText().toString();
		if(!getname.equals("") && !TextList.get(count-1).getText().toString().equals("")){
			ArrayList<String> QuestionsList = new ArrayList<String>();
			for(int i = 0; i< count; i++){
				Log.i("mentem ide" + String.valueOf(i) +":", TextList.get(i).getText().toString());
				QuestionsList.add(TextList.get(i).getText().toString());
			}
			
			String ser = SerializeObject.objectToString(QuestionsList);
			if (ser != null && !ser.equalsIgnoreCase("")) {
			    SerializeObject.WriteSettings(NewQuestion.this, ser, "myobject" + getname + ".dat");
			} else {
			    SerializeObject.WriteSettings(NewQuestion.this, "", "myobject.dat");
			}
			SerializeObject.WriteSettings(NewQuestion.this, getname + "\n", "namelist.dat");
		}
		else {
			Toast.makeText(getApplicationContext(), "Hiányzó kérdéssor név vagy kérdés!", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
		case R.id.newbtn:
			NewQuestionbtn();
			return true;
		case R.id.done:
			SaveQuestions();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.questions_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	

}


