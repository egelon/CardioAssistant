package com.egelon.cardioassistant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PedometerBackgroundService extends Service
{	
	/*
	private final IBinder mBinder = new MyBinder();
	
	//custom binder class for the service
	public class MyBinder extends Binder
	{
		PedometerBackgroundService getService() 
		{
			return PedometerBackgroundService.this;
		}
	}
*/
	
	
	//sensor manager
	private SensorManager sensorManager;
	private float acceleration;
	
	//values for calculation
	private float previousY;
	private float currentY;
	private int numSteps;
	private int threshold;
	
	final static String MY_ACTION = "MY_ACTION";
	static final String LOG_TAG = "CA_PedometerService";
	Intent intent = new Intent("com.egelon.cardioassistant.PedometerBackgroundService.MY_ACTION");
	

	//event handler for sensor events
	private SensorEventListener sensorEventListener = new SensorEventListener()
	{
		@Override
		public void onSensorChanged (SensorEvent sensorEvent)
		{
			//get the acceleration values from the accelerometer
			float x = sensorEvent.values[0];
			float y = sensorEvent.values[1];
			float z = sensorEvent.values[2];
			
			//store the current y value
			currentY = y;
			
			//Measure if step is taken
			if(Math.abs(currentY - previousY) > threshold)
			{
				setNumSteps(getNumSteps() + 1);
			}
			
			//store the previous y
			previousY = y;
			
			intent.putExtra("com.egelon.PedometerService.x_val", x);
			intent.putExtra("com.egelon.PedometerService.y_val", y);
			intent.putExtra("com.egelon.PedometerService.z_val", z);
			intent.putExtra("com.egelon.PedometerService.numSteps_val", getNumSteps());
			
		    sendBroadcast(intent);
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/*

	private static SensorManager sensorManager = null;
	private static Sensor mAccelerometer = null;
	
    private static StepDetector stepDetector = null;

    private static PowerManager powerManager = null;
    private static WakeLock wakeLock = null;
    private static NotificationManager notificationManager = null;
    private static Notification notification = null;
    
    private static Intent passedIntent = null;
	
    private static List<IStepServiceCallback> mCallbacks = new ArrayList<IStepServiceCallback>();
    
    private static int numberOfSteps = 0;
    private static boolean running = false;
    
    private static int NOTIFY = 0x1001;
    private static AtomicBoolean updating = new AtomicBoolean(false);
    */

    public PedometerBackgroundService() { super(); } 
    

	@Override
    public void onCreate() {
		Log.d( LOG_TAG, "onCreate" );
        // code to execute when the service is first created
		
		previousY = 0;
		currentY = 0;
		setNumSteps(0);
		acceleration = 0.00f;
		
		threshold = 10;
		
		//enable the listener
		enableAccelerometerListening();
		
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }
    
    
    
    
    
    private void enableAccelerometerListening ()
    {
    	//get a sensor service instance from the OS
    	sensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
    	//register a listener for the sensor events
    	sensorManager.registerListener(sensorEventListener,  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }


	public int getNumSteps() {
		return numSteps;
	}


	public void setNumSteps(int numSteps) {
		this.numSteps = numSteps;
	}
	
	public int getThreshold() {
		return threshold;
	}


	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}




