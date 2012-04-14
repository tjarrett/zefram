package com.viapx.zefram;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.LocationEvent;
import com.viapx.zefram.lib.db.DatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityAlertReceiver extends BroadcastReceiver
{
    /**
     * The DatabaseHelper for access our SQLite database
     */
    private DatabaseHelper databaseHelper = null;
    
    /**
     * The ORMLite DAO for locations
     */
    private Dao<Location, Integer> locationDao = null;
    
    /**
     * The ORMLite DAO for LocationEvents
     */
    private Dao<LocationEvent, Integer> locationEventDao = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(Z.TAG, "Received a proximity alert notice... with id " + intent.getExtras().getInt("location"));
        initDatabase(context);
        
        //Still here? Then we have our locationDao -- get our location
        Location location = null;
        
        try {
            location = locationDao.queryForId(intent.getExtras().getInt("location"));
            
        } catch ( SQLException sqle ) {
            Log.e(Z.TAG, "Could not get location dao", sqle);
            throw new RuntimeException(sqle);
            
        }
        
        //Make sure we actually got a location...
        if ( location == null ) {
            Log.d(Z.TAG, "Yikes! We got a proximity alert for a location that does not seem to be in the database!! Clean it up...");
            
            location = new Location();
            location.setName("missing location");
            location.setId(intent.getExtras().getInt("location"));
            
            ZeframLocationRegistrationService.getInstance().removeProximityAlertForLocation(location, false);
            return;
            
        }
        
        //Still here? Then we got our location and now we can do the other stuff that we need to do...
        Log.d(Z.TAG, "Got location: " + location.getName());
        
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        Log.d(Z.TAG, "FIX Got location from passive provider of " + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + " which is about " + lastLocation.distanceTo(location.getAndroidLocation()) + " from " + location.getName());
        
        
        float maxDistance = lastLocation.getAccuracy() + (float)location.getRadius();
        Log.d(Z.TAG, "FIX Last Location Accuracy: " + lastLocation.getAccuracy());
        Log.d(Z.TAG, "FIX The max tolerable distance would be... " + maxDistance);
        
        boolean entering = intent.getExtras().getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);
        String comingOrGoing = ( entering ) ? "entering" : "leaving";
        
        Location lastKnownLocation = ZeframLocationRegistrationService.getLastKnownLocation();
        if ( entering ) {
            if ( lastKnownLocation != null && lastKnownLocation.getId() == location.getId() ) {
                Log.d(Z.TAG, "Aborting because lastKnownLocation matches currently entering location...");
                return;
                
            } else {
                ZeframLocationRegistrationService.setLastKnownLocation(location);
                
            }
            
        } else {
            if ( lastKnownLocation == null ) {
                Log.d(Z.TAG, "Aborting because lastKnownLocation also null");
                return;
                
            } else {
                ZeframLocationRegistrationService.setLastKnownLocation(null);
                
            }
            
        }
        
        Log.d(Z.TAG, "We are " + comingOrGoing + " " + location.getName());
        Toast.makeText(context.getApplicationContext(), "Zefram detected that you are " + comingOrGoing + " " + location.getName(), Toast.LENGTH_SHORT).show();
        
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("location_id", location);
        fieldValues.put("onEnter", entering);
        
        List<LocationEvent> events;
        try {
            events = locationEventDao.queryForFieldValuesArgs(fieldValues);
            Log.d(Z.TAG, "Found " + events.size() + " events for location " + location.getName());
            
        } catch ( SQLException sqle ) {
            Log.e(Z.TAG, "Could not query for location events for location " + location.getName(), sqle);
            throw new RuntimeException(sqle);
            
        }
        
        for ( LocationEvent event : events ) {            
            Log.d(Z.TAG, "Found a event");
            Intent serviceIntent = new Intent();
            
            serviceIntent.setComponent(new ComponentName(event.getServicePackageName(), event.getServiceClassName()));
            serviceIntent.putExtra("extra", event.getExtra());
            
            ComponentName startedService = context.startService(serviceIntent);
            
            Log.d(Z.TAG, "Called service " + event.getServiceClassName());
            Log.d(Z.TAG, "startedService: " + startedService);
            
        }//end for LocationEvent
        
    }//end onReceive
    
    /**
     * Initializes the database connection
     * @param context
     */
    private void initDatabase(Context context)
    {
        if ( databaseHelper == null ) {
            //Get our dataabase
            databaseHelper = (DatabaseHelper)ZeframLocationRegistrationService.getDatabaseHelper(context.getApplicationContext());
            
        }
        
        if ( locationDao == null ) {
            //Build our database object
            try {
                locationDao = databaseHelper.getDao(Location.class);
                locationEventDao = databaseHelper.getDao(LocationEvent.class); 
                
            } catch ( SQLException sqle ) {
                Log.e(Z.TAG, "Could not get location dao", sqle);
                throw new RuntimeException(sqle);
                
            }
            
        }
        
    }//end initDatabase

}//end ProximityAlertReceiver class
