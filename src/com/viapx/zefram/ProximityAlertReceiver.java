package com.viapx.zefram;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.db.DatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(Z.TAG, "Received a proximity alert notice...");
        
        //Get our dataabase
        databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        
        //Build our database object
        try {
            locationDao = databaseHelper.getDao(Location.class);
            
        } catch ( SQLException sqle ) {
            Log.e(Z.TAG, "Could not get location dao", sqle);
            throw new RuntimeException(sqle);
            
        }
        
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
        
    }//end onReceive

}
