package com.example.grafologus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class Graphologist_exam extends View{

	
	final String LOG_TAG = "debugger";
	private Paint paint = new Paint();
	public LayoutParams params;
	Button AcceptDrawing;
	private Path path = new Path();
	private Paint circlePaint = new Paint();
	private Path circlePath = new Path();
	Coordinates c;
	int height = 0;
	int width = 0;
	float heightrate = 1;
	float widthrate = 1;
	boolean finishdrawing = true;
	boolean firstpart = true;
	float lastxcoord = -8;
	float lastycoord = -8;
	long lastspeed = -8;
	
	public boolean FinishDrawing() {return finishdrawing;}
	
	public Graphologist_exam(Context context) {
		super(context);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(2f);
		
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.RED);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f);
	
		
	}
	public void Replay(){
		Reset();
		
	}
	
	public void Reset(){
		circlePath.reset();
		path.reset();
		postInvalidate();
	}
	
	
	public void Drawing(Coordinates co,final int height, final int width){
		Log.i("elso?", String.valueOf(firstpart));
		finishdrawing = false;
		c = co;
		this.height = height;
		this.width = width;
		heightrate = (float)height/c.height;
		widthrate = (float)width/c.width;
		Log.i("bejott", String.valueOf(c.koordtombx.get(0)));
		new Thread(){
			
			@Override
			public void run() {
				Log.i("bejott nagyon", String.valueOf(c.koordtombx.get(0)));
				
				long speed = 0; long prevspeed = 0;
				if(firstpart || lastxcoord == -8){
					firstpart = false;
					path.moveTo(c.koordtombx.get(0)*heightrate,
							c.koordtomby.get(0)*widthrate);
				}
				else {
					path.moveTo(lastxcoord*heightrate,
							lastycoord*widthrate);
					path.lineTo(c.koordtombx.get(0)*heightrate,
						c.koordtomby.get(0)*widthrate);
				}
				for (int k = 1; k < c.koordtombx.size(); k++) {
					prevspeed = c.speedarray.get(k-1);
					 speed = c.speedarray.get(k)-prevspeed;
					if (c.koordtombx.get(k - 1) == -8 && c.koordtombx.get(k) != -8) {
						path.moveTo(c.koordtombx.get(k)*heightrate,
								c.koordtomby.get(k)*widthrate);
						circlePath.reset();	
					} else if (c.koordtombx.get(k - 1) != -8	&& c.koordtombx.get(k) != -8) {
						path.lineTo(c.koordtombx.get(k)*heightrate,
								c.koordtomby.get(k)*widthrate);
						circlePath.reset();
						circlePath.addCircle(c.koordtombx.get(k)*heightrate, c.koordtomby.get(k)*widthrate, 30, Path.Direction.CW);
					}
					try {
						Thread.sleep(speed);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					postInvalidate();
				}

				lastxcoord = c.koordtombx.get(c.koordtombx.size()-1);
				lastycoord = c.koordtomby.get(c.koordtomby.size()-1);
				lastspeed = c.speedarray.get(c.speedarray.size()-1);
				Log.i("vege","kroek");
				finishdrawing = true;
			}
			

			}.start();
			
	}
	
	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawPath(path, paint);
		canvas.drawPath(circlePath, circlePaint);
	}
}

