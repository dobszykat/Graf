package com.example.grafologus;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class Graphologist_exam extends View{


	final String LOG_TAG = "debugger";
	private Paint paint = new Paint();
	public LayoutParams params;
	Button AcceptDrawing;
	private Path path = new Path();
	Coordinates c;
	int height = 0;
	int width = 0;
	
	public Graphologist_exam(Context context, Coordinates co, int height, int width) {
		super(context);
		c = co;
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(2f);
		
		AcceptDrawing = new Button(context);
		AcceptDrawing.setText("Elfogadom");
		
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);	
		this.height = height;
		this.width = width;
		Drawing(height, width);
		
	}
	public void Replay(){
		path.reset();
		postInvalidate();
		Drawing(height, width);
	}
	
	public void Drawing(final int height, final int width){
		Log.i("bejott", String.valueOf(c.koordtombx.get(0)));
		path.reset();
		postInvalidate();
		Log.i("sz", String.valueOf(width));
		Log.i("h", String.valueOf(height));
		Log.i("sz2", String.valueOf(c.width));
		Log.i("h2", String.valueOf(c.height));
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				String sebs = null;
				path.moveTo(c.koordtombx.get(0)*(float)height/c.height,
						c.koordtomby.get(0)*(float)width/c.width);
				for (int k = 1; k < c.koordtombx.size() - 1; k++) {
					if (c.koordtombx.get(k - 1) == -8 && c.koordtombx.get(k) != -8) {
						path.moveTo(c.koordtombx.get(k)*(float)height/c.height,
								c.koordtomby.get(k)*(float)width/c.width);
						for (int l = 0; l < 3; l++) {
							sebs = String.valueOf(c.speedarray.get(k))
									.substring(1, 3);
						}

						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						postInvalidate();
					} else if (c.koordtombx.get(k - 1) != -8
							&& c.koordtombx.get(k) != -8) {
						path.lineTo(c.koordtombx.get(k)*(float)height/c.height,
								c.koordtomby.get(k)*(float)width/c.width);
						for (int l = 0; l < 3; l++) {
							sebs = String.valueOf(c.speedarray.get(k))
									.substring(1, 3);
						}

						try {
							Thread.sleep(Long.parseLong(sebs));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						postInvalidate();
					}
				}

			}
		}, 1000);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvas.drawPath(path, paint);
	}
}

