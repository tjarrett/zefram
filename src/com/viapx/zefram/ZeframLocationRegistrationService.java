package com.viapx.zefram;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.db.DatabaseHelper;

import android.app.Service;
import android.content.Intent;
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
    static public final int MSG_REGISTER_LOCATION = 0;
    
    static public final int MSG_UNREGISTER_LOCATION = 1;
    
    /**
     * The DatabaseHelper for access our SQLite database
     */
    private DatabaseHelper databaseHelper = null;
    
    /**
     * The ORMLite DAO for locations
     */
    private Dao<Location, Integer> locationDao = null;
    
    /**
     * The binder for this service
     */
    private final IBinder binder = new ZeframLocationRegistrationServiceBinder();
    
    /**
     * Keep track of whether or not the service is running
     * @see http://stackoverflow.com/questions/600207/android-check-if-a-service-is-running
     */
    private static ZeframLocationRegistrationService instance = null;
    
    private final Messenger messenger = new Messenger(new Handler() {

        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg)
        {
            switch ( msg.what ) {
                case MSG_REGISTER_LOCATION:
                    addProximityAlertForLocation((Location)msg.obj);
                    break;
                    
                case MSG_UNREGISTER_LOCATION:
                    removeProximityAlertForLocation((Location)msg.obj);
                    break;
             
                default:
                    super.handleMessage(msg);
                    break;
            }//end switch
            
        }//end handleMessage
        
    });
    
    /**
     * Returns true if the service is running, false otherwise
     * @return
     */
    public static boolean getInstance()
    {
        return instance != null;
        
    }//end isInstanceCreated
    
    private void addProximityAlertForLocation(Location location)
    {
        Log.d(Z.TAG, "Adding proximity detection for location: " + location.getName());
        
    }
    
    private void removeProximityAlertForLocation(Location location)
    {
        Log.d(Z.TAG, "Removing proximity detection for location: " + location.getName());
        
    }
    
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
        return binder;
        
    }//end onBind
    
    /**
     * Called when this service is created.... 
     */
    @Override
    public void onCreate()
    {
        Log.d(Z.TAG, "ZeframLocationRegistrationService created");
        
        instance = this;
        
        //Go get the database helper
        databaseHelper = (DatabaseHelper)getDatabaseHelper();
        
        //Now get the data access object
        try {
            locationDao = databaseHelper.getDao(Location.class);
            
        } catch ( SQLException sqle ) {
            Log.e(Z.TAG, "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
        //Initialize proximity alerts
        initProximityAlerts();
        
    }//end onCreate
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(Z.TAG, "ZeframLocationRegistrationService received start id " + startId + ":" + intent);
        
        //We want this service to keep running until explicitly stopped... 
        return START_STICKY;
        
    }//end onStartCommand
    
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
    private OrmLiteSqliteOpenHelper getDatabaseHelper()
    {
        return OpenHelperManager.getHelper(this, DatabaseHelper.class);
        
    }//end getDatabaseHelper

}//end ZeframProximityService
