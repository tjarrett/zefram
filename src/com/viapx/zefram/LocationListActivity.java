//http://developer.android.com/guide/topics/location/obtaining-user-location.html

package com.viapx.zefram;

import java.sql.SQLException;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.db.DatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * 
 * @author tjarrett
 *
 */
public class LocationListActivity extends Activity
{    
    /**
     * The DatabaseHelper for access our SQLite database
     */
    private DatabaseHelper databaseHelper = null;
    
    /**
     * Adapter for updating our location list view
     */
    private SimpleCursorAdapter locationListAdapter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list);
        
        //Start-up our service if it's not already running...
        if ( !ZeframLocationRegistrationService.getInstance() ) {
            startService(new Intent(this, ZeframLocationRegistrationService.class));
            Log.d(Z.TAG, "Started the ZeframLocationRegistrationService");
            
        } else {
            Log.d(Z.TAG, "ZeframLocationRegistrationService already running...");
            
        }
        
        //Try to find our current location
        final LocationManager locationManager = (LocationManager)getSystemService(this.getApplicationContext().LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(android.location.Location arg0)
            {
                locationManager.removeUpdates(this);
                
            }

            @Override
            public void onProviderDisabled(String arg0)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onProviderEnabled(String arg0)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle arg2)
            {
                // TODO Auto-generated method stub
                
            }
        };
            
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        
        
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(android.location.Location arg0)
            {
                locationManager.removeUpdates(this);
                
            }

            @Override
            public void onProviderDisabled(String arg0)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onProviderEnabled(String arg0)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle arg2)
            {
                // TODO Auto-generated method stub
                
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
    }//end onCreate
    
    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart()
    {
        //Go get the database helper
        databaseHelper = (DatabaseHelper)getDatabaseHelper();
        
        //Get the locations DAO
        Dao<Location, Integer> locationDao;
        Cursor locationCursor;
        try {
             locationDao = databaseHelper.getDao(Location.class);
             
             //Go get our locations
             QueryBuilder<Location, Integer> qb = locationDao.queryBuilder();
             qb.orderBy("name", true);
             PreparedQuery<Location> query = qb.prepare();
             
             AndroidCompiledStatement compiledStatement = (AndroidCompiledStatement)query.compile(databaseHelper.getConnectionSource().getReadOnlyConnection(), com.j256.ormlite.stmt.StatementBuilder.StatementType.SELECT);
             locationCursor = compiledStatement.getCursor();
             
             locationListAdapter = new SimpleCursorAdapter(
                 getBaseContext(), 
                 R.layout.location_list_item, 
                 locationCursor, 
                 new String[]{"name"},
                 new int[]{R.id.location_name});
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 

        }
        
        //Still here? Then we got the cursor that we need...
        ListView locationList = (ListView)findViewById(R.id.location_list);
        locationList.setAdapter(locationListAdapter);
        
        locationList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Call the "Edit" activity explicitly
                Intent i = new Intent(LocationListActivity.this, LocationActivity.class);
                i.putExtra("action", "edit");
                i.putExtra("location_id", id);
                startActivity(i); 
                
            }
            
        });
        
        super.onStart();
        
    }//end onStart
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        
    }//end onResume

    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onPause()
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        
    }//end onPause

    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        //Clean up our database
        if ( databaseHelper != null ) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
            
        }
        
    }//end onDestroy
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_list, menu);
        return true;
        
    }//end onCreateOptionsMenu
    
    /**
     * Listen for and handle an item selected in our menu
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch ( item.getItemId() ) {
            case R.id.menu_create_location:
                //Call the "Edit" activity explicitly
                Intent i = new Intent(LocationListActivity.this, LocationActivity.class);
                i.putExtra("action", "add");
                startActivity(i); 
                return true;
        }//end switch
        
        return super.onMenuItemSelected(featureId, item);
        
    }//end onMenuItemSelected 
    
    /**
     * Get the OrmLite database helper for this Android project
     * @return
     */
    private OrmLiteSqliteOpenHelper getDatabaseHelper()
    {
        return OpenHelperManager.getHelper(this, DatabaseHelper.class);
        
    }//end getDatabaseHelper

}//end OrmLiteSqliteOpenHelper
