package com.jordan.coffeecalc;


import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;

public class CountDownService extends Service {
	public static Fragment mFrag;
	private static final int NOTIFICATION_ID = 1232234230;
	public static final int ACTION_START = 0;
	public static final int ACTION_CANCEL = 1;
	
	private boolean mFinished;
	private Looper mTimerLooper;
	private Handler mHandler;
	private boolean mRunning = false;
	Button mButton;
	private Binder mBinder = new CountDownServiceBinder();
	private boolean mConnected = false;
	private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
	
	@Override
	public void onCreate(){
		HandlerThread back = new HandlerThread("CoffeCountdownTimer");
		back.start();
		mTimerLooper = back.getLooper();
		mHandler = new TimerHandler(mTimerLooper);
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int ids){
		Log.e(null,"adsfadsfasd "+(intent == null));
		if(intent!= null){
			int action = intent.getIntExtra("action", -1);
			Message mesg = mHandler.obtainMessage(action);
			mHandler.dispatchMessage(mesg);
		}
		return START_NOT_STICKY;
	}

	public void startCountDown(long time, Button button){
		Message mesg = mHandler.obtainMessage(ACTION_START,(int)time, -1);
		mButton = button;
		mHandler.dispatchMessage(mesg);
	}
	
	public void setConnected(boolean status){
		mConnected = status;
		if(!mConnected){
			//mButton = null;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	
	protected void startTimer(int time){
		if(mRunning){
			return;
		}
		
		mRunning = true;
		mHandler.post(new Timer(time));
	}
	
	
	private class Timer implements Runnable{
		final long mStartTime = System.currentTimeMillis();
		final int mCountDown;
		
		public Timer(int countDown){
			mCountDown = countDown;
		}
		
		@Override
		public void run() {
			if(mConnected){
				mainThreadHandler.post(new Runnable(){
					@Override
					public void run() {
						doButton();
					}
					
				});
				
			}else{
				doNotification();
			}
		
	}
		NotificationCompat.Builder builder; 
	private void doNotification(){
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		if (!mRunning){
			manager.cancel(NOTIFICATION_ID);
			return;
		}
		
		if(builder == null){
			builder = new NotificationCompat.Builder(CountDownService.this);
			
			Intent cancel = new Intent(CountDownService.this, CountDownService.class);
			cancel.putExtra("action", ACTION_CANCEL);
			
			builder.addAction(R.drawable.ic_launcher,
					  "Cancel",
					  PendingIntent
					  	.getService(CountDownService.this,100, cancel, 0));
		}
		
		
		builder.setPriority(Notification.PRIORITY_HIGH);
		builder.setAutoCancel(false);
		builder.setSmallIcon(R.drawable.ic_launcher);
		
		
		
		
		int unit = 1000; //seconds
		long diff = 0;
		int finish = mCountDown*1000;
		if((diff = System.currentTimeMillis()-mStartTime) < finish){
			builder.setProgress(finish, (int)Math.min(diff,finish), false);
			long countDown = (finish - diff)/unit;
			builder.setContentText(countDown+" seconds until brewing is finished");
			manager.notify(NOTIFICATION_ID, builder.build());
			mHandler.postDelayed(this, 1000);
		}else{
			builder = new NotificationCompat.Builder(CountDownService.this);
			builder.setPriority(Notification.PRIORITY_HIGH);
			builder.setAutoCancel(false);
			builder.setSmallIcon(R.drawable.ic_launcher);
			mRunning = false;
			builder.setContentText("Brewing is finished");
			builder.setAutoCancel(true);
			builder.setTicker("Your coffee is ready");
			builder.setVibrate(new long[]{1000,250,1000,250,1000});
			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			if(alert == null){// alert is null, using backup
			    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			    if(alert == null) {  
			        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);                
			    }
			}
			builder.setSound(alert);
			manager.notify(NOTIFICATION_ID, builder.build());
			}
		}	
	
	public void doButton(){
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancelAll();
		
	
		
		if (!mRunning){
			mButton.setText("Set timer");
			mButton.setEnabled(true);
			mButton = null;
			return;
		}
		
		int unit = 1000; //seconds
		long diff = 0;
		int finish = mCountDown*1000;
		if((diff = System.currentTimeMillis()-mStartTime) < finish){
			
			long countDown = (finish - diff)/unit;
			mButton.setText(countDown + "");
			mHandler.postDelayed(this, 1000);
		}else{
			mRunning = false;
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			if (r != null){
				r.play();
			}
			mButton.setText("Finished");
			mButton.setEnabled(true);
			mButton = null;
		}
		
	}
	
	}
	

	private class TimerHandler extends Handler{
		public TimerHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message mesg){
			switch(mesg.what){
			case ACTION_START:
				int time = mesg.arg1;
				startTimer(time);
				break;
			case ACTION_CANCEL:
				Log.e(null,"Cancel");
				mRunning = false;
				break;
			}
		}
	}	
	
	public boolean onResumeUpdate(){
		return mRunning;
	}
	
	
	
	public class CountDownServiceBinder extends Binder{
		public CountDownService getService(){
			return CountDownService.this;
		}
	}
		
	
	
}
