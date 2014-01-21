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

public class MainActivity extends Activity 
{
	//private PedometerBackgroundService pedometerService;

	private MyReceiver myReceiver = null;
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
      myReceiver = new MyReceiver();
      IntentFilter intentFilter = new IntentFilter();      
      intentFilter.addAction(PedometerBackgroundService.MY_ACTION);
      //startService(mServiceIntent);
      registerReceiver(myReceiver, intentFilter);

      Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onPause() {
      super.onPause();
      Log.d(LOG_TAG, "onPause");
      //unbindService(mConnection);
      stopService(mServiceIntent);
      if (myReceiver != null)
    	  unregisterReceiver (myReceiver);
    }
    
	private final class MyReceiver extends BroadcastReceiver
	{
		static final String LOG_TAG = "MyReceiver";
		//public MyReceiver() { super(); };
        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
        	Log.d( LOG_TAG, "onReceive" );
        	String x = arg1.getStringExtra("com.egelon.PedometerService.x_val");
        	String y = arg1.getStringExtra("com.egelon.PedometerService.y_val");  
        	String z = arg1.getStringExtra("com.egelon.PedometerService.z_val");
        	String stepsTaken = arg1.getStringExtra("com.egelon.PedometerService.numSteps_val");
        	
        	textViewX.setText(x);
        	textViewX.setText(y);
        	textViewX.setText(z);
        	textViewSteps.setText(stepsTaken);
        }
	};
	
}
