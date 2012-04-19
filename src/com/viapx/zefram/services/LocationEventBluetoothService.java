package com.viapx.zefram.services;

import com.viapx.zefram.Z;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

/**
 * Class for handling Bluetooth changes
 * @author tjarrett
 *
 */
public class LocationEventBluetoothService extends LocationEventService
{
    /**
     * Constructor
     */
    public LocationEventBluetoothService()
    {
        super(LocationEventBluetoothService.class.getName());
        Log.d(Z.TAG, "LocationEventBluetoothService started");
        
    }//end LocationEventBluetoothService

    /**
     * Handle incoming intents
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(Z.TAG, LocationEventBluetoothService.class.getName() + " received an intent!");
        
        // Look to make sure we have an "extra"
        String extra = intent.getExtras().getString("extra");
        if ( extra == null ) {
            Log.d(Z.TAG, "No `extra` in extras()... don't know what to do with the bluetooth...");
            return;
        }
        
        //Get the adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if ( "off".equals(extra.toLowerCase()) ) {
            bluetoothAdapter.disable();
            showToast("Zefram just turned off your bluetooth");
            
        } else if ( "on".equals(extra.toLowerCase()) ) {
            bluetoothAdapter.enable();
            showToast("Zefram just turned on your bluetooth");
            
        }

    }//end onHandleIntent
    
    /**
     * When I didn't have this... the damn thing didn't work...
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);

    }// end onStartCommand

}//end LocationEventBluetoothService
