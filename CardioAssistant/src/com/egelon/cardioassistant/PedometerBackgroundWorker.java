package com.egelon.cardioassistant;


import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;

public class PedometerBackgroundWorker extends IntentService {
	
	public PedometerBackgroundWorker() {
		super(null);
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = Logger.getLogger(PedometerBackgroundWorker.class.getSimpleName());
	
	private static SensorManager sensorManager = null;
    //private static StepDetector stepDetector = null;

    private static PowerManager powerManager = null;
    private static WakeLock wakeLock = null;
    private static NotificationManager notificationManager = null;
    private static Notification notification = null;
    
    private static Intent passedIntent = null;
	
    //private static List<IStepServiceCallback> mCallbacks = new ArrayList<IStepServiceCallback>();
    
    private static int mSteps = 0;
    private static boolean running = false;
    
    private static int NOTIFY = 0x1001;
    private static AtomicBoolean updating = new AtomicBoolean(false);


	
	public void onCreate() {
        super.onCreate();
        logger.info("onCreate");
        
        //get an instance of the system notification manager
        
        //show the Running notification
        initNotification();
        logger.info("Notification created");

        //get an instance of the PowerManager in order to invoke a WakeLock so the application doesn't get killed by the garbage collector
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        logger.info("Power manager invoked");
        //create the wakelock
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CardioAssistant_PedometerBackgroundService");
        //and acquire it
        if (!wakeLock.isHeld()) 
        	wakeLock.acquire();
/*
        if (stepDetector == null) {
            stepDetector = StepDetector.getInstance();
            stepDetector.addStepListener(this);
        }
*/
        //get an instance of the Sensor Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //register our custom listener to the accelerator sensor with the GAME sensor delay
        //sensorManager.registerListener(stepDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        running = true;
    }
	

	protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        //...
        // Do work here, based on the contents of dataString
        //...
    }
	
	private void initNotification() {
        //notification = new Notification(R.drawable.ic_launcher, "Pedometer started.", System.currentTimeMillis());
        //notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        
        
        //first we build our notification
        NotificationCompat.Builder mBuilder =
        	    new NotificationCompat.Builder(this)
        	    .setSmallIcon(R.drawable.ic_launcher)
        	    .setContentTitle("Pedometer started.")
        	    .setContentText("Status : Running");
        
        //now we set the notification ID
        int mNotificationId = 001;
        
        
        //then we add an instance of the notification manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        
        //and finally we display the notification in the notification bar
        notificationManager.notify(mNotificationId, mBuilder.build());
        
    }
}
