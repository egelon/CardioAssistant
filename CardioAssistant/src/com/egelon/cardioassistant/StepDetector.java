package com.egelon.cardioassistant;

import java.util.ArrayList;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class StepDetector implements SensorEventListener {

	private static final StepDetector instance = new StepDetector();
	private static boolean detecting = false;
	
	private static int mLimit = 100;
	
	private static float mLastValues[] = new float[3 * 2];
    private static float mScale[] = new float[2];
    private static float mYOffset = 0;
    
    private static float mLastDirections[] = new float[3 * 2];
    private static float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private static float mLastDiff[] = new float[3 * 2];
    private static int mLastMatch = -1;
    
    private static List<StepListener> mStepListeners = new ArrayList<StepListener>();
    
    static {
        int h = 480;
        mYOffset = h * 0.5f;
        mScale = new float[2];
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }
	
    private StepDetector() {
    }
    
    public static StepDetector getInstance() 
    {
        return instance;
    }
    
    public void addStepListener(StepListener sl) 
    {
        if (!mStepListeners.contains(sl)) 
        	mStepListeners.add(sl);
    }
    
    public void removeStepListener(StepListener sl) 
    {
        if (mStepListeners.contains(sl)) 
        	mStepListeners.remove(sl);
    }
    
    public static void setSensitivity(int sensitivity) 
    {
        mLimit = sensitivity;
    }
	
	
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

	@Override
	public void onSensorChanged(SensorEvent arg0) 
	{
        if (arg0 == null) 
        	throw new NullPointerException();

        if (!detecting) 
        	detectStep(arg0);
    }
	
	private void detectStep(SensorEvent event) 
	{
        if (event == null)
        	throw new NullPointerException();

        detecting = true;

        float vSum = 0;
        for (int i = 0; i < 3; i++) 
        {
            final float v = mYOffset + event.values[i] * mScale[0];
            vSum += v;
        }
        int k = 0;
        float v = vSum / 3;

        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
        if (direction == -mLastDirections[k])
        {
            // Direction changed
            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
            mLastExtremes[extType][k] = mLastValues[k];
            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

            if (diff > mLimit)
            {
                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                boolean isNotContra = (mLastMatch != 1 - extType);

                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra)
                {
                    for (StepListener stepListener : mStepListeners)
                    {
                        stepListener.onStep();
                    }
                    mLastMatch = extType;
                }
                else 
                {
                    mLastMatch = -1;
                }
            }
            mLastDiff[k] = diff;
        }
        mLastDirections[k] = direction;
        mLastValues[k] = v;

        detecting = false;
    }
	
	

}
