package com.egelon.cardioassistant;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PedometerBackgroundService extends Service
{	
	//sensor manager
	private SensorManager sensorManager;
	private float acceleration;
	
	//values for calculation
	private float previousX;
	private float currentX;
	
	private float previousY;
	private float currentY;
	
	private float previousZ;
	private float currentZ;
	
	private int numSteps;
	private int threshold;
	
	final static String MY_ACTION = "com.egelon.cardioassistant.PedometerBackgroundService.MY_ACTION";
	static final String LOG_TAG = "CA_PedometerService";
	static Intent intent = new Intent(MY_ACTION);
	
	
	//=====================================
	private BroadcastReceiver resetStepsReceiver = new BroadcastReceiver ()
	{
		static final String LOG_TAG = "MyReceiver";

        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
        	Log.d( LOG_TAG, "onReceive" );
        	
        	Bundle extras = arg1.getExtras();
        	if(extras != null)
        	{
        		setThreshold(extras.getInt("threshold_val"));
        		setNumSteps(extras.getInt("numSteps_val"));
        	}
        	Toast.makeText(arg0, "threshold:"+threshold+"; numSteps reset", Toast.LENGTH_SHORT).show();
        }
	};
	IntentFilter resetStepsIntentFilter = new IntentFilter();
	
	//=====================================
	private BroadcastReceiver setThresholdReceiver = new BroadcastReceiver ()
	{
		static final String LOG_TAG = "setThresholdReceiver";

        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
        	Log.d( LOG_TAG, "onReceive" );
        	
        	Bundle extras = arg1.getExtras();
        	if(extras != null)
        	{
        		setThreshold(extras.getInt("newThreshold_val"));
        	}
        	Toast.makeText(arg0, "new threshold:"+threshold, Toast.LENGTH_SHORT).show();
        }
	};
	IntentFilter setThresholdIntentFilter = new IntentFilter();
	

    
	@Override
    public void onCreate() {
		Log.d( LOG_TAG, "onCreate" );
        // code to execute when the service is first created
		
		previousY = 0;
		currentY = 0;
		setNumSteps(0);
		acceleration = 0.00f;
		
		threshold = 20;
		
		//enable the listener
		enableAccelerometerListening();
    }
	
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
        super.onStartCommand(intent, flags, startId);
        if (intent != null) 
        {
            Bundle bundle = intent.getExtras();
            threshold = bundle.getInt("threshold_val");
        }
        Toast.makeText(getApplicationContext(), "threshold:"+threshold, Toast.LENGTH_SHORT).show();
        
        //bind intent receivers
        resetStepsIntentFilter.addAction(MainActivity.RESET_STEPS);
	    registerReceiver(resetStepsReceiver, resetStepsIntentFilter);
	    
	    setThresholdIntentFilter.addAction(MainActivity.SET_THRESHOLD);
	    registerReceiver(setThresholdReceiver, setThresholdIntentFilter);
        
        return START_STICKY;
	}
	
	private void enableAccelerometerListening ()
    {
    	//get a sensor service instance from the OS
    	sensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
    	//register a listener for the sensor events
    	sensorManager.registerListener(sensorEventListener,  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    	mInitialized = false;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }
    
    private boolean mInitialized = false;;

    
    
    private double mLastX;
    private double mLastY;
    private double mLastZ;
    private final float NOISE = (float) 2.5;
  //event handler for sensor events
  	private SensorEventListener sensorEventListener = new SensorEventListener()
  	{
  		@Override
  		public void onSensorChanged (SensorEvent event)
  		{
  			
  			/*
  			
  			
  			//get the acceleration values from the accelerometer
  			float x = event.values[0];
  			float y = event.values[1];
  			float z = event.values[2];
  			
  			//store the current y value
  			currentX = x;
  			currentY = y;
  			currentZ = z;
  			
  			//Measure if step is taken
  			if ( (Math.abs(currentY) > 4.1 && Math.abs(currentY) < 7.6) && (Math.abs(currentX) > 1.4 && Math.abs(currentX) < 3.4))
  			{
  				setNumSteps(getNumSteps() + 1);
  			}
  			
  			//store the previous y
  			previousX = x;
  			previousY = y;
  			previousZ = z;
  			*/
  			 // event object contains values of acceleration, read those
  			 double x = event.values[0];
  			 double y = event.values[1];
  			 double z = event.values[2];
  			 
  			 final double alpha = 0.8; // constant for our filter below
  			 
  			double[] gravity = {0,0,0};
  			 
  			 // Isolate the force of gravity with the low-pass filter.
  			 gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
  			 gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
  			 gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
  			 
  			// Remove the gravity contribution with the high-pass filter.
  			 x = event.values[0] - gravity[0];
  			 y = event.values[1] - gravity[1];
  			 z = event.values[2] - gravity[2];
  			 
  			if (mInitialized == false) 
  			{
  			 //sensor is used for the first time, initialize the last read values
  			 mLastX = x;
  			 mLastY = y;
  			 mLastZ = z;
  			 mInitialized = true;
  			 } 
  			else
  			{
  			 // sensor is already initialized, and we have previously read values.
  			 // take difference of past and current values and decide which
  			 // axis acceleration was detected by comparing values
  			 
  			double deltaX = Math.abs(mLastX - x);
  			 double deltaY = Math.abs(mLastY - y);
  			 double deltaZ = Math.abs(mLastZ - z);
  			 if (deltaX < NOISE)
  			 deltaX = (float) 0.0;
  			 if (deltaY < NOISE)
  			 deltaY = (float) 0.0;
  			 if (deltaZ < NOISE)
  			 deltaZ = (float) 0.0;
  			 mLastX = x;
  			 mLastY = y;
  			 mLastZ = z;
  			 
  			 /*
  			if (deltaX > deltaY) {
  			 // Horizontal shake
  			 // do something here if you like
  			 
  			} else if (deltaY > deltaX) {
  			 // Vertical shake
  			 // do something here if you like
  			 
  			} else if ((deltaZ > deltaX) && (deltaZ > deltaY)) {
  			 // Z shake
  			 //stepsCount = stepsCount + 1;
  			 //if (stepsCount > 0) {
  			 //txtCount.setText(String.valueOf(stepsCount));
  			 //}
  				setNumSteps(getNumSteps() + 1);
  			}
  			*/
  			 
  			 if(deltaX > deltaY)
  			 {
  				setNumSteps(getNumSteps() + 1);
  			 }
  			//Toast.makeText(PedometerBackgroundService.this, "values x:"+x+",y:"+y+",z:"+z, Toast.LENGTH_SHORT).show();

  			Bundle extras = new Bundle();
  			extras.putString("x_val", String.valueOf(event.values[0]));
  			extras.putString("y_val", String.valueOf(event.values[1]));
  			extras.putString("z_val", String.valueOf(event.values[2]));
  			extras.putInt("numSteps_val", getNumSteps());
  			
  			intent.putExtras(extras);
  			
  		    sendBroadcast(intent);
  		}
  		}

  		@Override
  		public void onAccuracyChanged(Sensor arg0, int arg1) {
  			// TODO Auto-generated method stub
  			
  		}
  	};
    

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





