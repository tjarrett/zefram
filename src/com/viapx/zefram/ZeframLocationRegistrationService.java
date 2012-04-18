package com.viapx.zefram;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.db.DatabaseHelper;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * This class keeps track of the various locations and whether or not they are registrered with receive proximity alerts
 * 
 * @author tjarrett
 * @see http://developer.android.com/reference/android/app/Service.html
 * @see http://www.vogella.de/articles/AndroidServices/article.html
 */
public class ZeframLocationRegistrationService extends Service
{
    /**
     * Message flag indicating that we want to register the given location
     */
    static public final int MSG_REGISTER_LOCATION = 0;
    
    /**
     * Message flag indiciating that we want to unregister the given location
     */
    static public final int MSG_UNREGISTER_LOCATION = 1;
    
    /**
     * The intent to be set in the proximity alert
     */
    static private final String PROX_ALERT_INTENT = "com.viapx.zefram.PROXIMITY_ALERT";
    
    /**
     * The last location recorded (often null)
     */
    static private Location lastLocation = null;
    
    /**
     * The location manager
     */
    private LocationManager locationManager;
    
    /**
     * Intent filter for directing intents to the right place
     */
    private IntentFilter intentFilter;
    
    /**
     * The DatabaseHelper for access our SQLite database
     */
    private DatabaseHelper databaseHelper = null;
    
    /**
     * The ORMLite DAO for locations
     */
    private Dao<Location, Integer> locationDao = null;
    
    /**
     * Keep track of whether or not the service is running
     * @see http://stackoverflow.com/questions/600207/android-check-if-a-service-is-running
     */
    private static ZeframLocationRegistrationService instance = null;
    
    /**
     * Messenger for interacting with this service
     */
    private final Messenger messenger = new Messenger(new Handler() {

        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg)
        {
            Log.d(Z.TAG, "handling message...");
            Location location;
            
            switch ( msg.what ) {
                case MSG_REGISTER_LOCATION:                        
                    try {
                        location = locationDao.queryForId(msg.arg1);
                        
                    } catch ( SQLException sqle ) {
                        Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
                        throw new RuntimeException(sqle);  
                        
                    }
                    
                    addProximityAlertForLocation(location);
                    break;
                    
                case MSG_UNREGISTER_LOCATION:                    
                    try {
                        location = locationDao.queryForId(msg.arg1);
                        
                    } catch ( SQLException sqle ) {
                        Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
                        throw new RuntimeException(sqle); 
                        
                    }
                    
                    boolean shouldDelete = ( msg.arg2 == 1 );
                    
                    removeProximityAlertForLocation(location, shouldDelete);
                    break;
             
                default:
                    super.handleMessage(msg);
                    break;
            }//end switch
            
        }//end handleMessage
        
    });

    /**
     * The proximity alert broadcast receiver
     */
    private BroadcastReceiver proximityAlertReceiver;
    
    /**
     * Returns an instance of this service if it is running, false otherwise
     * @return
     */
    public static ZeframLocationRegistrationService getInstance()
    {
        return instance;
        
    }//end isInstanceCreated
    
    /**
     * Returns true if the service is running, false otherwise
     * @return
     */
    public static boolean isRunning()
    {
        return instance != null;
        
    }//end isRunning
    
    /**
     * Add a proximity alert for the given location
     * 
     * @param location
     */
    public void addProximityAlertForLocation(Location location)
    {
        Log.d(Z.TAG, "Adding proximity detection for location: " + location.getName());
        
        //Build our intent
        Intent intent = new Intent(PROX_ALERT_INTENT);
        intent.putExtra("location", location.getId());
        
        //Prepare our pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), location.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        
        //Register it all up
        locationManager.addProximityAlert(location.getLatitudeDegrees(), location.getLongitudeDegrees(), location.getRadiusInMeters(), -1, pendingIntent); 

        
    }//end addProximityAlertForLocation
    
    /**
     * Remove the proximity alert for the given location
     * @param location
     */
    public void removeProximityAlertForLocation(Location location, boolean delete)
    {
        Log.d(Z.TAG, "Removing proximity detection for location: " + location.getName());
        
        //Build our intent
        Intent intent = new Intent(PROX_ALERT_INTENT);
        intent.putExtra("location", location.getId());
        
        //Prepare our pending intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), location.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        
        locationManager.removeProximityAlert(pendingIntent);
        
        if ( delete ) {
            try {
                locationDao.delete(location);
                
            } catch ( SQLException sqle ) {
                Log.e(LocationListActivity.class.getName(), "Could not delete location", sqle);
                throw new RuntimeException(sqle);

            }
            
        }
        
    }//end removeProximityAlertForLocation
    
    /**
     * Enables proximity alerts for all locations -- called in onCreate
     */
    private void initProximityAlerts()
    {
        Log.d(Z.TAG, "Initializing proximity alerts...");
        
        //Go get all the locations
        for ( Location location : locationDao ) {
            addProximityAlertForLocation(location);
            
        }//end for
        
    }//end initProximityAlerts

    /**
     * Return the binder
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        return messenger.getBinder();
        
    }//end onBind
    
    /**
     * Called when this service is created.... 
     */
    @Override
    public void onCreate()
    {
        Log.d(Z.TAG, "ZeframLocationRegistrationService created");
        
        instance = this;
        
        proximityAlertReceiver = new ProximityAlertReceiver();
        intentFilter = new IntentFilter(PROX_ALERT_INTENT);
        registerReceiver(proximityAlertReceiver, intentFilter);
        
        //Get our location manager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        //Go get the database helper
        databaseHelper = (DatabaseHelper)getDatabaseHelper(getApplicationContext());
        
        //Now get the data access object
        try {
            locationDao = databaseHelper.getDao(Location.class); 
            
        } catch ( SQLException sqle ) {
            Log.e(Z.TAG, "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
        /*timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run()
            {
                Log.d(Z.TAG, "Service still running...");
                
            }}, 0, 1500);*/
        
        //Initialize proximity alerts
        initProximityAlerts();
        
    }//end onCreate
    
    /**
     * Called by the system every time a client explicitly starts the service by calling startService(Intent), 
     * providing the arguments it supplied and a unique integer token representing the start request. Do not call this method directly. 
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        instance = this;
        
        Log.d(Z.TAG, "ZeframLocationRegistrationService received start id " + startId + ":" + intent);
        
        //We want this service to keep running until explicitly stopped... 
        return START_STICKY;
        
    }//end onStartCommand
    
    /**
     * Called when the service is destroyed
     */
    @Override
    public void onDestroy()
    {
        Log.d(Z.TAG, "ZeframLocationRegistrationService destroyed");
        
        instance = null;
        
        //Clean up our database
        if ( databaseHelper != null ) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
            
        }
        
        //Clear our receiver
        unregisterReceiver(proximityAlertReceiver);
        proximityAlertReceiver = null;
        
        //Let papa do his work
        super.onDestroy();
        
    }//end onDestroy
    
    /**
     * Class for handling the binding with this service
     * @author tjarrett
     *
     */
    public class ZeframLocationRegistrationServiceBinder extends Binder
    {
        /**
         * Returns the service
         * @return
         */
        public ZeframLocationRegistrationService getService()
        {
            return ZeframLocationRegistrationService.this;
            
        }//end ZeframProximityService
        
    }//end ZeframProximityServiceBinder
    
    /**
     * Get the OrmLite database helper for this Android project
     * @return
     */
    static public OrmLiteSqliteOpenHelper getDatabaseHelper(Context context)
    {
        return OpenHelperManager.getHelper(context.getApplicationContext(), DatabaseHelper.class);
        
    }//end getDatabaseHelper
    
    /**
     * Return the last known location
     * @return
     */
    static public Location getLastKnownLocation()
    {
        return lastLocation;
        
    }//end getLastKnownLocation
    
    /**
     * Set the last known location
     * @param location
     */
    static public void setLastKnownLocation(Location location)
    {
        synchronized(PROX_ALERT_INTENT) {
            lastLocation = location;
            
        }
        
    }//end setLastKnownLocation

}//end ZeframProximityService

