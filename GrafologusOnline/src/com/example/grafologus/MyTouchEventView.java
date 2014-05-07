package com.example.grafologus;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class MyTouchEventView extends View {
	static int size = 50;
	int i = 0;
	private Paint paint = new Paint();
	private Path path = new Path();
	private Paint circlePaint = new Paint();
	private Path circlePath = new Path();
	public Button vege;
	public LayoutParams params;
	List<List<Float>> listx = new ArrayList<List<Float>>();
	List<List<Float>> listy = new ArrayList<List<Float>>();
	List<List<Long>> listspeed = new ArrayList<List<Long>>();

	List<Float> koordtombx = new ArrayList<Float>();
	List<Float> koordtomby = new ArrayList<Float>();
	List<Long> speedarray = new ArrayList<Long>();

	boolean readytosend = false;
	public boolean IsReadyToSend(){
		if(readytosend){
			readytosend = false;
			return true;
		}
	return false;
	}

	public List<Float> Xkoords() {
		return listx.get(0);
	}

	public List<Float> Ykoords() {
		return listy.get(0);
	}

	public List<Long> Speedkoords() {
		return listspeed.get(0);
	}
	
	public int CoordListSize(){
		return listx.size();
	}
	
	public void GetLastCoords(){
		listx.add(koordtombx);
		listy.add(koordtomby);
		listspeed.add(speedarray);
		readytosend = true;
	}
	
	public MyTouchEventView(Context context) {
		super(context);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(2f);

		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.BLUE);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f);
		
		
	}

	public void Reset(){
		path.reset();
		postInvalidate();
	}
	
	public void ClearArrays(){
		listx.remove(0);
		listy.remove(0);
		listspeed.remove(0);
	}
	
	boolean szunet = false;

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawPath(path, paint);
		canvas.drawPath(circlePath, circlePaint);
	}


	//Valamit rajzolnak
	@Override
	public boolean onTouchEvent(MotionEvent event) {
			float Pointx = event.getX();
			float Pointy = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				szunet = false;
				path.moveTo(Pointx, Pointy);
				return true;
			case MotionEvent.ACTION_MOVE:
				path.lineTo(Pointx, Pointy);
				circlePath.reset();
				circlePath.addCircle(Pointx, Pointy, 30, Path.Direction.CW);
				break;

			case MotionEvent.ACTION_UP:
				circlePath.reset();
				szunet = true;
				break;
			default:
				return false;
			}

			if (!szunet) {
				koordtombx.add(Pointx);
				koordtomby.add(Pointy);
			} else {
				if(i!=0){
				koordtombx.add((float) -8.0);
				koordtomby.add((float) -8.0);
				}
			}
			i++;
			speedarray.add(event.getEventTime());
			postInvalidate();
			if(i == size){
				listx.add(koordtombx);
				listy.add(koordtomby);
				listspeed.add(speedarray);
				koordtombx = new ArrayList<Float>();
				koordtomby = new ArrayList<Float>();
				speedarray = new ArrayList<Long>();
				i = 0;
				readytosend = true;
			}
		return true;
	}
}
