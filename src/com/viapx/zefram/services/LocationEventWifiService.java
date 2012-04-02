package com.viapx.zefram.services;

import com.viapx.zefram.Z;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author tjarrett
 *
 */
public class LocationEventWifiService extends IntentService
{
    /**
     * 
     */
    public LocationEventWifiService()
    {
        super(LocationEventWifiService.class.getName());
        
    }

    /**
     * 
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(Z.TAG, LocationEventWifiService.class.getName() + " received an intent!");
        
        

    }

}
