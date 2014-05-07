package com.example.grafologus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.conn.ConnectTimeoutException;
import android.os.AsyncTask;
import android.util.Log;

public class SocketClass {
	  
	private PrintWriter printwriter;
	String line = null;
	Boolean connected = false;
	boolean chatischanged = false;
	boolean willgetchattext = false;
	int grafget = 0;
	boolean getdrawing = false;
	boolean getdrawingcoords = false;
	boolean getallcoords = false;
	boolean finishexam = false;
	boolean connectionbreak = false;
	boolean canconnect = true;
	int id = -8;
	Coordinates c;
	int db = 0;
	volatile boolean lineischanged = false;
	BufferedReader in;
	ArrayList<String> drawingname = new ArrayList<String>();
	ArrayList<String> drawingid = new ArrayList<String>();
	ArrayList<String> drawing = new ArrayList<String>();
	ArrayList<Coordinates> Drawings = new ArrayList<Coordinates>();
	List<Coordinates> listcoordinates = new ArrayList<Coordinates>();
		
	public static Socket client = null;
	private static SocketClass singleton = new SocketClass( );
	private SocketClass(){ };
	
	
	  public static SocketClass getInstance( ) {
	      return singleton;
	   }
	public boolean IsFinishedExam() {
		return finishexam;
	}

	public boolean GetAllCoords() {
		if (getallcoords) {
			getallcoords = false;
			return true;
		}
		return false;
	}

	public String koords;

	public String WhatIsLine() {
		if (lineischanged) {
			lineischanged = false;
		}
		if (chatischanged) {
			chatischanged = false;
		}
		return line;

	}

	public boolean IfGetDrawing() {
		if (getdrawing) {
			getdrawing = false;
			return true;
		}
		return false;
	}
	public ArrayList<String> DrawingsId(){
		return drawingid;
	}
	public ArrayList<String> DrawingsName() {
		return drawingname;
	}

	public ArrayList<Coordinates> Drawings() {
		return Drawings;
	}

	public int AvailableCoordNum() {
		return grafget;
	}

	public void SetPartnerId(String id) {

	}

	public void ConnectToPartner(final String resztvevo, final String name,
			String ownid) {
		new PostTask().execute(resztvevo, name, ownid);
	}

	private class PostTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {

			String resztvevo = params[0];
			String name = params[1];
			String ownid = params[2];
			InetAddress serverAddr;
			try {
				Log.i("", "socket");
				serverAddr = InetAddress.getByName("192.168.0.20");
				client = new Socket(serverAddr, 7452);
			} catch (SocketException e) {
				e.printStackTrace();
				connected = false;
				connectionbreak = true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				printwriter = new PrintWriter(new OutputStreamWriter(
						client.getOutputStream(), "UTF-8"), true);
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			printwriter.write(resztvevo);
			printwriter.write(name);
			printwriter.flush();
			SendPatientInfo(ownid);
			try {
				in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				while (true) {
					if (!lineischanged && (line = in.readLine()) != null) {
						Log.i("naaalegyszii", line);
						if (line.equals("Kapcsolat")) {
							connected = true;

						}else if(line.equals("No more partner")){
							canconnect = false;
						}
						else if (line.equals("Megszakadt a kapcsolat!")) {
							connectionbreak = true;
						} else if (line.equals("chat")) {
							willgetchattext = true;
						} else if (line.equals("finishexam")) {
							finishexam = true;
						} else if (line.equals("grafologus kap")) {
							Log.i("megkaptam h ", " jo");
							ReadCoordinates();
							Log.i("MOSTfogokID-t", "JJEE");
							while (true) {
								if ((line = in.readLine()) != null) {
									Log.i("MOST KAPOM AZ ID-T", String.valueOf(line));
									id = Integer.parseInt(line);
									break;
								}
							}

						}else if(line.equals("Get selected drawing data")){
							ReadCoordinates();
				
						}
						else if (line.equals("GetQuestions")) { // Kérdések
																	// listája
							Log.i("kapom a kerdeseket", "most");
							int num = 0;
							while (true) {
								if ((line = in.readLine()) != null) {
									num = Integer.parseInt(line);
									break;
								}
							}
							
							int k = 0;
							for (int i = 0; i < num*2; i++) {
								while (true) {
									if ((line = in.readLine()) != null) {
										if((k & 1)==0){
										drawingid.add(line);
										k++;
										}
										else{
										drawingname.add(line);
										k++;
										}
										Log.i("k", String.valueOf(k));
										break;
									}
								}
							}
							Log.i("ezaz!", "megvannak a kérdések!");
							getdrawing = true;
							
						} else if (willgetchattext) {
							willgetchattext = false;
							chatischanged = true;
						} else {
							lineischanged = true;
						}
					}
				}

			} catch (ConnectTimeoutException e) {
				connectionbreak = true;
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				connectionbreak = true;
				e.printStackTrace();
			} catch (SocketException e) {
				connectionbreak = true;
				connected = false;
				Log.i("Socket", "Exception");
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

	
	public void ReadCoordinates(){
		while (!getallcoords) {
			String tmp = "";
			try {
				while (0 != (line = in.readLine()).compareTo("end")) {
					if (line != null) {
						if (0 == line.compareTo("vege")) {
							Log.i("vege lett", " most");
							getallcoords = true;
							break;
							
						}
						tmp += line + "\n";
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!getallcoords){
			tmp = tmp.substring(0, tmp.length() - 1);
			Log.i("ez", tmp);
			ReadArray(tmp);
			tmp = "";
			}
		}
	}
	
	public void SendArray(Coordinates c) throws IOException {
		koords = SerializeObject.objectToString(c);
		Printing(koords, true);
		Printing("\nend\n", true);
		printwriter.flush();
	}

	public Coordinates ReadArray(String x) {
		Log.i("gr", "tömbben");

		Object obj = SerializeObject.stringToObject(x);
		if (obj instanceof Coordinates) {
			c = (Coordinates) obj;
		}
		listcoordinates.add(c);
		grafget++;
		Log.i("Megvan!", "tömb kész" + String.valueOf(c.height));
		return c;
	}

	public void RemoveRow() {
		Printing("Delete");
		Printing(String.valueOf(id));
	}

	public void GetDrawingsList() {
		Printing("GetDrawingsList");
		// Printing(name);
	}

	public Coordinates Coord() {
		grafget--;
		Log.i(String.valueOf(db), String.valueOf(listcoordinates.size()));
		return listcoordinates.get(db++);
	}
	
	public List<Coordinates> CoordList() {
		return listcoordinates;
	}

	public void Exit() {
		Printing("quit\n");
		try {
			in.close();
			printwriter.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void SendPatientInfo(String id) {
		printwriter.write("patientid\n");
		printwriter.write(id);
		printwriter.flush();
	}

	public boolean IsChat() {
		return chatischanged;
	}

	public void Printing(String line) {
		printwriter.write(line + "\n");
		printwriter.flush();
	}

	public void Printing(String line, boolean noflush) {
		printwriter.write(line);
	}
	

	public Boolean IsConnected() {
		return connected;
	};

	public boolean IsConnectionBreak() {
		return connectionbreak;
	}

	public Boolean LineisChanged() {
		return lineischanged;
	}
};
