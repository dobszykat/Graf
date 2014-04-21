package com.example.grafologus;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

/**
 * Take an object and serialize and then save it to preferences
 * @author John Matthews
 *
 */
public class SerializeObject {
    private final static String TAG = "SerializeObject";

    

	@SuppressLint("NewApi")
	public static String objectToString(Serializable object) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(out).writeObject(object);
            byte[] data = out.toByteArray();
            out.close();

            out = new ByteArrayOutputStream();
            Base64OutputStream b64 = new Base64OutputStream(out,0);
            b64.write(data);
            b64.close();
            out.close();
            return new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    


    @TargetApi(8)
    public static Object stringToObject(String encodedObject) {
        try {
            return new ObjectInputStream(new Base64InputStream(
                    new ByteArrayInputStream(encodedObject.getBytes()), 0)).readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void WriteSettings(Context context, String data, String filename){ 
        FileOutputStream fOut = null; 
        OutputStreamWriter osw = null;

        try{
            fOut = context.openFileOutput(filename, Context.MODE_APPEND);       
            osw = new OutputStreamWriter(fOut); 
            osw.write(data);
            osw.flush(); 
            //Toast.makeText(context, "Settings saved",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {       
            e.printStackTrace(); 
           // Toast.makeText(context, "Settings not saved",Toast.LENGTH_SHORT).show();
        } 
        finally { 
            try { 
                if(osw!=null)
                    osw.close();
                if (fOut != null)
                    fOut.close(); 
            } catch (IOException e) { 
                   e.printStackTrace(); 
            } 
        } 
    }

    public static String ReadSettings(Context context, String filename){ 
        StringBuffer dataBuffer = new StringBuffer();
        try{
            // open the file for reading
            InputStream instream = context.openFileInput(filename);
            // if file the available for reading
            if (instream != null) {
                // prepare the file for reading
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                String newLine;
                // read every line of the file into the line-variable, on line at the time
                while (( newLine = buffreader.readLine()) != null) {
                    // do something with the settings from the file
                	Log.i("sor", newLine);
                	if(newLine.equals("array_end")){
                		Log.i("most van", "vege egy tombnek");
                		dataBuffer.delete(0,dataBuffer.length());
                	}
                	else{
                    dataBuffer.append(newLine);
                	}
                }
                // close the file again
                instream.close();
            }

        } catch (java.io.FileNotFoundException f) {
            // do something if the myfilename.txt does not exits
            Log.e(TAG, "FileNot Found in ReadSettings filename = " + filename);
            try {
                context.openFileOutput(filename, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Error in ReadSettings filename = " + filename);
        }

        return dataBuffer.toString();
    }
    
    public static ArrayList<String> Read(Context context, String filename){ 
    	ArrayList<String> arr = new ArrayList<String>();
        StringBuffer dataBuffer = new StringBuffer();
        try{
            InputStream instream = context.openFileInput(filename);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                String newLine;
                while (( newLine = buffreader.readLine()) != null) {
                    dataBuffer.append(newLine);
                    arr.add(newLine);
                }
                instream.close();
            }

        } catch (java.io.FileNotFoundException f) {
            // do something if the myfilename.txt does not exits
            Log.e(TAG, "FileNot Found in ReadSettings filename = " + filename);
            try {
                context.openFileOutput(filename, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e(TAG, "IO Error in ReadSettings filename = " + filename);
        }

        return arr;
    }
}
