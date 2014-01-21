package com.egelon.cardioassistant;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    	Intent mServiceIntent = new Intent(this, PedometerBackgroundWorker.class);
    	//start the service
    	startService(mServiceIntent);
    }
    
    public void resetSteps () 
    {
    	
    }
    
}
