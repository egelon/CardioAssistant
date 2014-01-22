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
        	numSteps = extras.getInt("numSteps_val");
        	
        	textViewX.setText(x);
        	textViewY.setText(y);
        	textViewZ.setText(z);
        	textViewSteps.setText(String.valueOf(numSteps));
        	
        	}
        	//Toast.makeText(arg0, "values x:"+x+",y:"+y+",z:"+z, Toast.LENGTH_SHORT).show();
        }
	};
	IntentFilter intentFilter = new IntentFilter();
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private Intent mServiceIntent;
	
	final static String RESET_STEPS = "com.egelon.cardioassistant.MainActivity.RESET_STEPS";
	private Intent mResetSteps = new Intent(RESET_STEPS);
	
	final static String SET_THRESHOLD = "com.egelon.cardioassistant.MainActivity.SET_THRESHOLD";
	private Intent mSetTreshold = new Intent(SET_THRESHOLD);
	
	static final String LOG_TAG = "CA_MainActivity";
	
	//accelerometer values
	private TextView textViewX;
	private TextView textViewY;
	private TextView textViewZ;
	
	//current sensitivity
	private TextView textViewSensitivity;
	
	//steps taken
	private TextView textViewSteps;
	private int numSteps;
	
	//reset pedometer button
	//private Button buttonReset;
	
	//exit app button
	private Button buttonExit;
	
	//SeekBar
	private SeekBar seekBar;
	private int threshold;

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
        numSteps = 0;
        
        //buttonReset = (Button) findViewById(R.id.buttonReset);
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
    	Bundle extras = new Bundle();
    	extras.putInt("threshold_val", threshold);
    	extras.putInt("numSteps_val", numSteps);
    	mServiceIntent.putExtras(extras);
    	
    	//mServiceIntent.putExtra("threshold_val", threshold);
    	//mServiceIntent.putExtra("numSteps_val", numSteps);
    	startService(mServiceIntent);
    	Log.d(LOG_TAG, "StartPedometerService");
    }
    
    public void ResetSteps (View view) 
    {
    	//pedometerService.setNumSteps(0);
    	//textViewSteps.setText(String.valueOf(pedometerService.getNumSteps()));
    	Log.d(LOG_TAG, "resetSteps");
    	
    	Bundle extras = new Bundle();
    	extras.putInt("threshold_val", threshold);
    	extras.putInt("numSteps_val", 0);
    	mResetSteps.putExtras(extras);
    	sendBroadcast(mResetSteps);
    	
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
    
    
    private OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener()
    {

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			threshold = seekBar.getProgress();
			textViewSensitivity.setText(String.valueOf(threshold));
			
			Bundle extras = new Bundle();
	    	extras.putInt("newThreshold_val", threshold);
	    	mSetTreshold.putExtras(extras);
	    	sendBroadcast(mSetTreshold);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    };
	
}
