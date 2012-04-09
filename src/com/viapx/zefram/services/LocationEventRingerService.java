package com.viapx.zefram.services;

import com.viapx.zefram.Z;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class LocationEventRingerService extends LocationEventService
{
    /**
     * The constructor
     */
    public LocationEventRingerService()
    {
        super(LocationEventRingerService.class.getName());
        Log.d(Z.TAG, "LocationEventRingerService started");

    }// end LocationEventWifiService constructor

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // Announce that we are in
        Log.d(Z.TAG, LocationEventRingerService.class.getName() + " received an intent!");

        // Look to make sure we have an "extra"
        String extra = intent.getExtras().getString("extra");
        if ( extra == null ) {
            Log.d(Z.TAG, "No `extra` in extras()... don't know what to do with the ringer...");
            return;
        }

        // Get the manager
        AudioManager manager = (AudioManager)getSystemService(AUDIO_SERVICE);

        // Set the appropriate ringer level
        if ( "vibrate".equals(extra.toLowerCase()) ) {
            manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            showToast("Zefram just set your ringer to vibrate");

        } else if ( "normal".equals(extra.toLowerCase()) ) {
            manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            showToast("Zefram just set your ringer to normal");
            
        } else if ( "silent".equals(extra.toLowerCase()) ) {
            manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            showToast("Zefram just set your ringer to silent");
            
        }

    }
    
    /**
     * When I didn't have this... the damn thing didn't work...
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);

    }// end onStartCommand

}
