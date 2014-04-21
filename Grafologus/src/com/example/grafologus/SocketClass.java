package com.example.grafologus;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


public class SocketClass extends Activity implements Serializable{
	
	public static Socket client = null;
	public PrintWriter printwriter;
	String line = null;
	Boolean connected = false;
	boolean chat = false;
	boolean chatlesz = false;
	boolean grafget = false;
	int id = -8;
	Coordinates c;
	volatile boolean lineischanged = false;
	public String koords;
	
	public String WhatIsLine() {
		lineischanged = false;
		chat = false;
		return line;
		
	}
	
	
	public Boolean GetCoords() {
		if(grafget)
		{
			grafget = false; 
			return true;
		} 
		return false;
	}
	
	public SocketClass(final String resztvevo, final String name) {
		new PostTask().execute(resztvevo,name);
	}

	private class PostTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
		
		@Override
		protected String doInBackground(String... params) {

			String resztvevo = params[0];
	//		String name = params[1];
			InetAddress serverAddr;
			try {
				Log.i("","socket");
				serverAddr = InetAddress.getByName("192.168.0.20");
				client = new Socket(serverAddr, 7452);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				printwriter = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8"),true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			printwriter.write(resztvevo);
		//	printwriter.write(name);
			printwriter.flush();
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				while (true) {
					if (!lineischanged && (line = in.readLine()) != null) {
						Log.i("naaalegyszii", line);
						if (line.equals("Kapcsolat")) {
							connected = true;
					
						}
						else if(line.equals("Megszakadt a kapcsolat!")){
							connected = false;
							Log.i("Hiba", "Megszakadt a kapcsolat");
							Exit();
						}
						else if(line.equals("chat")){
							chatlesz = true;
						}
						else if(line.equals("grafologus kap")){
							Log.i("megkaptam h ", " jo");
							String tmp = "";
							while (0 != (line = in.readLine()).compareTo("end")) {						
								if(line!=null) {
									Log.i("ez", line);
									tmp += line+"\n";
								}
							}
							tmp = tmp.substring(0, tmp.length()-1);
							ReadArray(tmp);
							while(true){
								if ((line = in.readLine()) != null) {
									id = Integer.parseInt(line);
									break;
								}
							}
							grafget =  true;
						}
						else {
							if(chatlesz){
								chatlesz = false;
								chat = true;
							}
						lineischanged = true;
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.i("test", "hiba a beolvasáskor");
			}

			return "All Done!";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

		}
	}

	public void SendArray(Coordinates c) throws IOException {
		Printing("koord\n", true);
		koords = SerializeObject.objectToString(c);
		Printing(koords,true);
		Printing("\nend\n",true);
		printwriter.flush();
	}

	public void ReadArray(String x) {
		Log.i("gr","tömbben");
		    Object obj = SerializeObject.stringToObject(x);
		    if (obj instanceof Coordinates) {
		        c =(Coordinates) obj;
		    }

	}
	
	public void RemoveRow(){
		Printing("Delete");
		Printing(String.valueOf(id));
	}
	
	public Coordinates Coord(){return c;}
	
	public void Exit(){
		printwriter.close();
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean IsChat(){
		return chat;
	}
	
	public void Printing(String line) {
		printwriter.write(line+"\n");
		printwriter.flush();
	}
	
	public void Printing(String line, boolean noflush) {
		printwriter.write(line);
	}

	public Boolean IsConnected() {
		return connected;
	};

	public Boolean LineisChanged() {
		return lineischanged;
	}
};
