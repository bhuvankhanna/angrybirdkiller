package com.mobinise.AngryBirdKiller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AngryBirdKiller extends Activity {
	public static int screenwidth;
	public static int screenheight;
	public Canvas canvas;
	Display display;
	private GestureDetector gestureDetector;
	private static final int SWIPE_MIN_DISTANCE = 1;
	private static final int SWIPE_MAX_OFF_PATH = 4;
	private static final int SWIPE_THRESHOLD_VELOCITY = 20;
	float centerX;
	float centerY;
	float radius;
	Paint paint;
	int hitCount = 0;
	int saveCount;
	double t;
	int windFlag = 0;   //0=left to right and 1=right to left
	int windFactorDelta = 0;
	TextView txtScore;
	TextView txtLevel;
	int score = 0;
	ImageView imgArrow;
	Bitmap bitmapArrow;
	ImageView imgTarget;
	Bitmap bitmapTarget;
	ImageView imgBow;
	Bitmap bitmapBow;
	Button btnRefresh;
	double timeStart;
	double timeShoot;
	double timeHit;
	Bitmap screen;
	SharedPreferences sp;
	TextView txtHighScore;
	int windFactor = 0;
	int level = 1;
	double timeDelay;
	long animationTimeTarget = 5000;
	Context context;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		score = sp.getInt("CURRENTSCORE", 0);
		txtScore = (TextView)findViewById(R.id.txtScore);
		txtScore.setText("Score: " + score);

		txtHighScore = (TextView)findViewById(R.id.txtHighScore);
		txtHighScore.setText("High Score:" + sp.getInt("HIGHSCORE", 0));

		txtLevel = (TextView)findViewById(R.id.txtLevel);
		txtLevel.setText("Level:" + level);


	}

	Handler mHandler;
	MotionEvent slideEvent1;
	MotionEvent slideEvent2;
	View.OnTouchListener gestureListener;

	static Bundle gameState;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		
		context = this;

		mHandler = new Handler();

		sp = context.getSharedPreferences("gameData", 0);

		txtHighScore = (TextView)findViewById(R.id.txtHighScore);
		txtHighScore.setText("High Score:" + sp.getInt("HIGHSCORE", 0));

		txtLevel = (TextView)findViewById(R.id.txtLevel);
		txtLevel.setText("Level:" + level);

		txtScore = (TextView)findViewById(R.id.txtScore);
		txtScore.setText("Score: " + score);
		display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		screenwidth = display.getWidth(); 
		screenheight = display.getHeight();
		ImageView iv =(ImageView)findViewById(R.id.imageView1);
		//   iv.setImageResource(R.drawable.background);
		screen = Bitmap.createBitmap(screenwidth, screenheight, Bitmap.Config.ARGB_8888);
		saveCount = 1;
		iv.setImageBitmap(screen);
		canvas = new Canvas(screen);
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(20);
		canvas.drawLine(50, 0, 50, screenwidth, paint);
		paint.setStrokeWidth(1);
		centerX = 300f;
		centerY = 150f;
		radius = 20f;
		canvas.save(saveCount);
		bitmapArrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_transparent);
		canvas.drawBitmap(bitmapArrow,40, screenheight/2, null);
		canvas.drawColor(Color.GREEN);
		btnRefresh = (Button)findViewById(R.id.btnAaaoAaao);
		btnRefresh.setText("Start");
		btnRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				canvas.drawBitmap(bitmapArrow,40, screenheight/2, paint);
				timeStart = System.currentTimeMillis();

				txtScore = (TextView)findViewById(R.id.txtScore);
				//  txtScore.setText("Score: " + savedInstanceState.getInt("Score"));
				display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

				screenwidth = display.getWidth(); 
				screenheight = display.getHeight();
				ImageView iv =(ImageView)findViewById(R.id.imageView1);
				//   iv.setImageResource(R.drawable.background);
				Bitmap screen = Bitmap.createBitmap(screenwidth, screenheight, Bitmap.Config.ARGB_8888);
				saveCount = 1;
				iv.setImageBitmap(screen);
				canvas = new Canvas(screen);
				paint = new Paint();
				paint.setColor(Color.WHITE);
				paint.setStrokeWidth(10);
				canvas.drawLine(50, 0, 50, screenwidth, paint);
				paint.setStrokeWidth(1);
				canvas.drawColor(Color.TRANSPARENT);
				centerX = 300f;
				centerY = 150f;
				radius = 20f;
				// canvas.drawCircle(centerX, centerY, radius, paint);
				canvas.save(saveCount);
				bitmapArrow = Bitmap.createBitmap(40,20,Bitmap.Config.ARGB_8888);
				imgArrow =(ImageView)findViewById(R.id.imageArrow);
				imgArrow.setBackgroundResource(R.drawable.arrowimage);
				imgArrow.setImageBitmap(bitmapArrow);
				imgArrow.setVisibility(ImageView.INVISIBLE);
				bitmapTarget = Bitmap.createBitmap(40,20,Bitmap.Config.ARGB_8888);

				imgTarget =(ImageView)findViewById(R.id.imageTarget);
				imgTarget.setBackgroundResource(R.drawable.angrybirds);
				imgTarget.setImageBitmap(bitmapTarget);

				imgTarget.setVisibility(ImageView.INVISIBLE);
				canvas.drawBitmap(bitmapTarget, centerX, centerY, paint);

				// Animation targetAnimation = AnimationUtils.loadAnimation(AngryBirdKiller.this, R.anim.target);

				TranslateAnimation targetAnimation = new TranslateAnimation(600, 600, 0, screenheight);
				//arrowAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(AngryBirdKiller.this, R.anim.arrow);
				targetAnimation.setDuration(animationTimeTarget);
				imgTarget.startAnimation(targetAnimation);
				canvas.drawBitmap(bitmapTarget, centerX, centerY, paint);

			}
		});




		bitmapTarget = Bitmap.createBitmap(150,150,Bitmap.Config.ARGB_8888);

		imgTarget =(ImageView)findViewById(R.id.imageTarget);
		imgTarget.setBackgroundResource(R.drawable.angrybirds);
		imgTarget.setImageBitmap(bitmapTarget);

		canvas.drawBitmap(bitmapTarget, centerX, centerY, paint);
		imgTarget.setVisibility(ImageView.VISIBLE);
		if(level==0){

			TranslateAnimation targetAnimation = new TranslateAnimation(screenwidth-150, screenwidth-150, 0, screenheight);

			targetAnimation.setDuration(animationTimeTarget);

		}else{

			TranslateAnimation targetAnimation = new TranslateAnimation(screenwidth-150, screenwidth-150, 0, screenheight);

			targetAnimation.setDuration(animationTimeTarget);


		}

		gestureDetector = new GestureDetector(new SimpleOnGestureListener(){




			@Override
			public boolean onDown(MotionEvent e) {
				
				
				bitmapArrow = Bitmap.createBitmap((int)e.getX(),(int)e.getY(),Bitmap.Config.ARGB_8888);
				imgArrow =(ImageView)findViewById(R.id.imageArrow);
				MarginLayoutParams params = (MarginLayoutParams)imgArrow.getLayoutParams();
				params.setMargins((int)e.getX(), (int)e.getY(), (int)e.getX()+160, (int)e.getY()+140);
				imgArrow.setLayoutParams(params);
				
				imgArrow.setBackgroundResource(R.drawable.arrow_transparent);
				imgArrow.setImageBitmap(bitmapArrow);
				imgArrow.setVisibility(ImageView.VISIBLE);

				return super.onDown(e);
			}



			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				try {


					slideEvent1 = e1;
					slideEvent2 = e2;
					timeShoot = System.currentTimeMillis();
					timeDelay = timeShoot-timeStart;

					double velocityArrow = ((screenwidth)-slideEvent1.getX())/400;
					t = (screenwidth-slideEvent1.getX())/velocityArrow;
					float m1 = (float) Math.atan((slideEvent1.getY() - slideEvent2.getY())/(slideEvent1.getX()- slideEvent2.getX()));
					RotateAnimation arrowRotate =  new RotateAnimation(0, (float) (m1*57.32),slideEvent2.getX()+20 , slideEvent2.getY()+10);
					arrowRotate.setDuration(100);
					arrowRotate.setFillAfter(true);
					imgArrow.startAnimation(arrowRotate);
					imgArrow.clearAnimation();
					int arrowColor = Color.GREEN;
					paint.setColor(arrowColor);

					Log.v("AngryBirdKiller", "#####Angle of Arrow: " + m1*57.32);
					Log.v("AngryBirdKiller", "### Got left swipe!" + e1.getX()+ "  " +e1.getY());
					canvas.drawLine(e1.getX(), e1.getY(), screenwidth, e1.getY(), paint);
					float x = slideEvent1.getX() - (slideEvent1.getY()/m1);
					float y = m1*(screenwidth - slideEvent1.getY()) + slideEvent1.getY();
					
					if(slideEvent1.getX() - slideEvent2.getX() > SWIPE_MIN_DISTANCE) {

						//float m1 = (float) Math.atan((slideEvent1.getY() - slideEvent2.getY())/(slideEvent1.getX()- slideEvent2.getX()));
						float m2 = 1.57f;

                        float angle = (m2-m1)/(1+(m1*m2));

                        angle = (float)(3.14*angle)/180;

                    	timeHit = t+timeDelay;
                    	double totalDistanceTarget = screenheight;

                    	double velocityTarget = totalDistanceTarget/animationTimeTarget;
                    	Log.v("AngryBirdKiller", "velocityTarget:" + velocityTarget);

                    	double targetTop = ((velocityTarget*timeHit)-20);
                    	double targetBottom = ((velocityTarget*timeHit));

                    	Log.v("AngryBirdKiller", "Time Delay:" + timeDelay);
                    	Log.v("AngryBirdKiller", "screenheight:" + screenheight);
                    	Log.v("AngryBirdKiller", "screenwidth:" + screenwidth);
                    	Log.v("AngryBirdKiller", "Time to hit:" + timeHit);
                    	Log.v("AngryBirdKiller", "Point of intersection:" + slideEvent1.getY());
                    	Log.v("AngryBirdKiller", "target location top:" + targetTop);
                    	Log.v("AngryBirdKiller", "target location bottom:" + targetBottom);

                    	for(int i=0; i<screenwidth;){

                			canvas.drawLine(i, 0, i, screenheight, paint);
                			canvas.drawLine(0, i, screenwidth, i, paint);
                			i=i+20;

                		}


                    	if(slideEvent1.getY()>targetTop && slideEvent1.getY()<targetBottom){

                    		score++;
                    		txtScore.setText("Score: " + score);
                    		if(windFlag==1){

                    			windFlag = 0;
                    			windFactor = windFactor -30;

                    		}else{

                    			windFactor = windFactor +30;
                    			windFlag = 1;
                    		}



                    		if(animationTimeTarget>3000){

                    			animationTimeTarget = animationTimeTarget -100;
                    			score++;
                    			SharedPreferences sp = context.getSharedPreferences("gameData", 0);
                    			Editor edit =  sp.edit();


                    			if(score > sp.getInt("HIGHSCORE", 0)){


                    				edit.putInt("HIGHSCORE", score);
                    				edit.commit();
                    				txtHighScore.setText("High Score:"+ score);

                    			}



                    		}else{


                    			animationTimeTarget = 5000;
                    			Toast t = Toast.makeText(getApplicationContext(), "Congrats!! Level: " + level, 8000);
                    			t.show();
                    			level++;


                    			txtLevel.setText("Level:" + level);


                    		}
                    		canvas.drawCircle(screenwidth-50, slideEvent1.getY(), 20, paint);
                    		imgTarget.setVisibility(ImageView.INVISIBLE);
                    		Toast.makeText(AngryBirdKiller.this, "Bird HIT!!", 8000).show();
                    		Log.v("AngryBirdKiller", "Bird Hit!!");
                    		paint.setColor(Color.WHITE);
                    		canvas.drawBitmap(bitmapTarget, x, y, paint);
                    		paint.setColor(Color.RED);
                    		canvas.drawLine((screenwidth-50), 0, (screenwidth-50), screenheight, paint);
                    		canvas.drawCircle(370, e1.getY(), 20, paint);
                    		//Thread.sleep((long) t);
                    		imgTarget.clearAnimation();
                    		canvas.drawBitmap(bitmapTarget, 300, slideEvent1.getY(), paint);
                    		TranslateAnimation birdHitAnimation = new TranslateAnimation(330, screenwidth, slideEvent1.getY(), slideEvent1.getY()+windFactor);
                    		birdHitAnimation.setDuration(300);
                    		TranslateAnimation birdHitAnimation2 = new TranslateAnimation(330, screenwidth, slideEvent1.getY(), slideEvent1.getY()+windFactor);
                    		birdHitAnimation.setDuration(300);
                    		imgArrow.clearAnimation();
                    		imgArrow.startAnimation(birdHitAnimation);
                    		imgTarget.startAnimation(birdHitAnimation);
                    		imgTarget.setVisibility(ImageView.VISIBLE);
                    		canvas.drawLine(slideEvent1.getX(), slideEvent1.getY(), 360, slideEvent1.getY(), paint);
                    		canvas.drawText("Score: ", 0, 0, paint);

                    	}


					}

                	Log.v("AngryBirdKiller", "### Got gesture!" + slideEvent1.getX()+ "  " +slideEvent1.getY());


				} catch (Exception e) {
					// nothing
				}
				return false;
			}





		});


		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {


				bitmapArrow = Bitmap.createBitmap(190,70,Bitmap.Config.ARGB_8888);

				imgArrow =(ImageView)findViewById(R.id.imageArrow);
				imgArrow.setBackgroundResource(R.drawable.arrow_transparent);
				imgArrow.setImageBitmap(bitmapArrow);
				canvas.drawBitmap(bitmapArrow, 50, 220, paint);




				if (gestureDetector.onTouchEvent(event)) {

					imgTarget.setVisibility(ImageView.VISIBLE);
					Animation targetAnimation = AnimationUtils.loadAnimation(AngryBirdKiller.this, R.anim.target);

					targetAnimation = new TranslateAnimation(300, 300, 0, screenheight);
					//	arrowAnimation = (TranslateAnimation) AnimationUtils.loadAnimation(AngryBirdKiller.this, R.anim.arrow);
					targetAnimation.setDuration(animationTimeTarget);
					imgTarget.startAnimation(targetAnimation);
					return true;
				}
				return false;
			}
		};


		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub




			}
		});
		iv.setOnTouchListener(gestureListener);



		//  canvas.drawLine(, startY, stopX, stopY, paint)



	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SharedPreferences sp = context.getSharedPreferences("gameData", 0);
		Editor edit =  sp.edit();
		edit.putInt("CURRENTSCORE", 0);
		edit.putInt("LEVEL", 1);

		if(score > sp.getInt("HIGHSCORE", 0)){


			edit.putInt("HIGHSCORE", score);
			edit.commit();


		}





	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		SharedPreferences sp = context.getSharedPreferences("gameData", 0);
		Editor edit =  sp.edit();
		edit.putInt("CURRENTSCORE", score);
		edit.putInt("LEVEL", level);
		edit.commit();
	}
}