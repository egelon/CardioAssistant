package com.egelon.cardioassistant;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.util.Log;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	//private PedometerBackgroundService pedometerService;

	private BroadcastReceiver myReceiver = new BroadcastReceiver ()
	{
		static final String LOG_TAG = "MyReceiver";

        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
        	Log.d( LOG_TAG, "onReceive" );
        	
        	Bundle extras = arg1.getExtras();
        	if(extras != null)
        	{
        	
        	
        	String x = extras.getString("x_val");
        	String y = extras.getString("y_val");  
        	String z = extras.getString("z_val");
        	//String stepsTaken = arg1.getStringExtra("numSteps_val");
        	String stepsTaken = extras.getString("numSteps_val");
        	
        	textViewX.setText(x);
        	textViewY.setText(y);
        	textViewZ.setText(z);
        	textViewSteps.setText(stepsTaken);
        	
        	}
        	//Toast.makeText(arg0, "values x:"+x+",y:"+y+",z:"+z, Toast.LENGTH_SHORT).show();
        }
	};
	IntentFilter intentFilter = new IntentFilter();
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private Intent mServiceIntent;
	static final String LOG_TAG = "CA_MainActivity";
	
	//accelerometer values
	private TextView textViewX;
	private TextView textViewY;
	private TextView textViewZ;
	
	//current sensitivity
	private TextView textViewSensitivity;
	
	//steps taken
	private TextView textViewSteps;
	
	//reset pedometer button
	private Button buttonReset;
	
	//exit app button
	private Button buttonExit;
	
	//SeekBar
	private SeekBar seekBar;
	private int threshold;
	private OnSeekBarChangeListener seekBarListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //attach our TextViews to the corresponding UI elements
        textViewX = (TextView) findViewById(R.id.textViewX);
        textViewY = (TextView) findViewById(R.id.textViewY);
        textViewZ = (TextView) findViewById(R.id.textViewZ);
        
        textViewSensitivity = (TextView) findViewById(R.id.textSensitivity);
        
        textViewSteps = (TextView) findViewById(R.id.stepsTakenCounterLabel);
        
        buttonReset = (Button) findViewById(R.id.buttonReset);
        buttonExit = (Button) findViewById(R.id.buttonExit);
        
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        
        //initialize value for seekBar
        seekBar.setProgress(10);
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        threshold = 10;
        textViewSensitivity.setText(String.valueOf(threshold));
        
        mServiceIntent = new Intent(this, PedometerBackgroundService.class);
        Log.d(LOG_TAG, "onCreate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void openSettings ()
    {
    	
    }
    
    public void StartPedometerService (View view) 
    {
    	/*
    	 * Creates a new Intent to start the PedometerBackgroundWorker service
    	 * IntentService.
    	 */
    	//mServiceIntent = new Intent(this, PedometerBackgroundService.class);
    	//start the service
    	startService(mServiceIntent);
    	Log.d(LOG_TAG, "StartPedometerService");
    }
    
    public void resetSteps () 
    {
    	//pedometerService.setNumSteps(0);
    	//textViewSteps.setText(String.valueOf(pedometerService.getNumSteps()));
    	Log.d(LOG_TAG, "resetSteps");
    }
    
    @Override
    protected void onResume() {
      super.onResume();
      //Intent intent= new Intent(this, PedometerBackgroundService.class);
      //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
      //Register BroadcastReceiver to receive accelerometer data from service
      //if (myReceiver == null)
      //{
	            
	      intentFilter.addAction(PedometerBackgroundService.MY_ACTION);
	      //startService(mServiceIntent);
	      registerReceiver(myReceiver, intentFilter);
      //}
      Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onPause() {
      super.onPause();
      Log.d(LOG_TAG, "onPause");
      //unbindService(mConnection);
      stopService(mServiceIntent);
      
      try {
    	  unregisterReceiver (myReceiver);
      }
      catch (IllegalArgumentException e) 
      {
    	    if (e.getMessage().contains("Receiver not registered")) 
    	    {
    	        // Ignore this exception. This is exactly what is desired
    	        Log.w(LOG_TAG,"Tried to unregister the reciver when it's not registered");
    	    } 
    	    else
    	    {
    	        // unexpected, re-throw
    	        throw e;
    	    }
    	}
    }
    
    @Override
    protected void onStop() {
      super.onStop();
      Log.d(LOG_TAG, "onStop");
      //unbindService(mConnection);
      if (myReceiver != null)
    	  unregisterReceiver (myReceiver);
      stopService(mServiceIntent);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	//public class MyReceiver extends 
	
}
