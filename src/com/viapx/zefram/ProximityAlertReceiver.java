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
        Log.d(Z.TAG, "Received a proximity alert notice...");
        initDatabase(context);
        
        //Still here? Then we have our locationDao -- get our location
        Location location = null;
        
        try {
            location = locationDao.queryForId(intent.getExtras().getInt("location"));
            
        } catch ( SQLException sqle ) {
            Log.e(Z.TAG, "Could not get location dao", sqle);
            throw new RuntimeException(sqle);
            
        }
        
        //Still here? Then we got our location and now we can do the other stuff that we need to do...
        Log.d(Z.TAG, "Got location: " + location.getName());
        
        boolean entering = intent.getExtras().getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);
        Log.d(Z.TAG, "We are " + (( entering ) ? "entering" : "leaving") + " " + location.getName());
        
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
            Log.d(Z.TAG, "Found a " + event.getType() + " event");
            Intent serviceIntent = new Intent();
            
            serviceIntent.setComponent(new ComponentName(event.getServicePackageName(), event.getServiceClassName()));
            serviceIntent.putExtra("extra", event.getExtra());
            
            ComponentName startedService = context.startService(serviceIntent);
            
            Log.d(Z.TAG, "Called service " + event.getServiceClassName());
            Log.d(Z.TAG, "startedService: " + startedService);
            
        }//end for LocationEvent
        
        /*
        
        if ( intent.getExtras().getBoolean(LocationManager.KEY_PROXIMITY_ENTERING) ) {
            Log.d(Z.TAG, "Entering location " + location.getName());
            
            // http://stackoverflow.com/questions/2728465/how-to-call-one-android-application-from-another-android-application
            // http://mobileorchard.com/android-app-development-implementing-remote-android-services-with-aidl/
            
        } else {
            Log.d(Z.TAG, "Leaving location " + location.getName());
                
        }*/
        
    }//end onReceive
    
    /**
     * Initializes the database connection
     * @param context
     */
    private void initDatabase(Context context)
    {
        if ( databaseHelper == null ) {
            //Get our dataabase
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            
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
