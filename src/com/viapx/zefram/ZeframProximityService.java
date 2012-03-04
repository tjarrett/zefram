package com.viapx.zefram;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * @author tjarrett
 * @see http://developer.android.com/reference/android/app/Service.html
 * @see http://www.vogella.de/articles/AndroidServices/article.html
 */
public class ZeframProximityService extends Service
{
    /**
     * The binder for this service
     */
    private final IBinder binder = new ZeframProximityServiceBinder();
    
    /**
     * Keep track of whether or not the service is running
     * @see http://stackoverflow.com/questions/600207/android-check-if-a-service-is-running
     */
    private static ZeframProximityService instance = null;
    
    /**
     * Returns true if the service is running, false otherwise
     * @return
     */
    public static boolean isServiceStarted()
    {
        return instance != null;
        
    }//end isInstanceCreated

    /**
     * Return the binder
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
        
    }//end onBind
    
    /**
     * Called when this service is created.... 
     */
    @Override
    public void onCreate()
    {
        Log.d(Z.TAG, "ZeframProximityService created");
        
        instance = this;
        
    }//end onCreate
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(Z.TAG, "ZeframProximityService received start id " + startId + ":" + intent);
        
        //We want this service to keep running until explicitly stopped... 
        return START_STICKY;
        
    }//end onStartCommand
    
    
    @Override
    public void onDestroy()
    {
        Log.d(Z.TAG, "ZeframProximityService destroyed");
        
        instance = null;
        
    }//end onDestroy
    
    /**
     * Class for handling the binding with this service
     * @author tjarrett
     *
     */
    public class ZeframProximityServiceBinder extends Binder
    {
        /**
         * Returns the service
         * @return
         */
        public ZeframProximityService getService()
        {
            return ZeframProximityService.this;
            
        }//end ZeframProximityService
        
    }//end ZeframProximityServiceBinder

}//end ZeframProximityService
