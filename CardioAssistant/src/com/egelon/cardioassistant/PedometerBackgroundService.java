package com.egelon.cardioassistant;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
	private float previousY;
	private float currentY;
	private int numSteps;
	private int threshold;
	
	final static String MY_ACTION = "com.egelon.cardioassistant.PedometerBackgroundService.MY_ACTION";
	static final String LOG_TAG = "CA_PedometerService";
	static Intent intent = new Intent(MY_ACTION);
	

    //public PedometerBackgroundService() { super(); } 
    
	@Override
    public void onCreate() {
		Log.d( LOG_TAG, "onCreate" );
        // code to execute when the service is first created
		
		previousY = 0;
		currentY = 0;
		setNumSteps(0);
		acceleration = 0.00f;
		
		threshold = 2;
		
		//enable the listener
		enableAccelerometerListening();
    }
	
	private void enableAccelerometerListening ()
    {
    	//get a sensor service instance from the OS
    	sensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
    	//register a listener for the sensor events
    	sensorManager.registerListener(sensorEventListener,  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onDestroy();
    }
    
    

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
  			
  			//Toast.makeText(PedometerBackgroundService.this, "values x:"+x+",y:"+y+",z:"+z, Toast.LENGTH_SHORT).show();
  			/*
  			intent.putExtra("x_val", String.valueOf(x));
  			intent.putExtra("y_val", String.valueOf(y));
  			intent.putExtra("z_val", String.valueOf(z));
  			intent.putExtra("numSteps_val", String.valueOf(getNumSteps()));
  			*/
  			Bundle extras = new Bundle();
  			extras.putString("x_val", String.valueOf(x));
  			extras.putString("y_val", String.valueOf(y));
  			extras.putString("z_val", String.valueOf(z));
  			extras.putString("numSteps_val", String.valueOf(getNumSteps()));
  			
  			intent.putExtras(extras);
  			
  		    sendBroadcast(intent);
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





