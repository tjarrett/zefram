package com.viapx.zefram.services;

import com.viapx.zefram.Z;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * The service that handles turning off and on Wifi
 * 
 * @author tjarrett
 * @see http://developer.android.com/guide/topics/fundamentals/services.html
 */
public class LocationEventWifiService extends LocationEventService
{
    /**
     * The constructor
     */
    public LocationEventWifiService()
    {
        super(LocationEventWifiService.class.getName());
        Log.d(Z.TAG, "LocationEventWifiService started");

    }// end LocationEventWifiService constructor

    /**
     * Handle the intents as they roll on in
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        // Announce that we are in
        Log.d(Z.TAG, LocationEventWifiService.class.getName() + " received an intent!");

        // Look to make sure we have an "extra"
        String extra = intent.getExtras().getString("extra");
        if ( extra == null ) {
            Log.d(Z.TAG, "No `extra` in extras()... don't know whether to turn wifi off or on");
            return;
        }

        // Get the wifi manager
        WifiManager wm = (WifiManager)getSystemService(WIFI_SERVICE);

        // Turn it off or on as appropriate
        if ( "off".equals(extra.toLowerCase()) ) {
            wm.setWifiEnabled(false);
            showToast("Zefram just turned your wifi off");

        } else if ( "on".equals(extra.toLowerCase()) ) {
            wm.setWifiEnabled(true);
            showToast("Zefram just turned your wifi on");

        }

    }// end onHandleIntent

    /**
     * When I didn't have this... the damn thing didn't work...
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);

    }// end onStartCommand

}// end LocationEventWifiService