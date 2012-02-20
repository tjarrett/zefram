package com.viapx.zefram;

import java.sql.SQLException;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.db.DatabaseHelper;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * 
 * @author tjarrett
 *
 */
public class LocationListActivity extends Activity
{
    private static final String TAG = "Tim";
    
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
     * Get the OrmLite database helper for this Android project
     * @return
     */
    private OrmLiteSqliteOpenHelper getDatabaseHelper()
    {
        return OpenHelperManager.getHelper(this, DatabaseHelper.class);
        
    }//end getDatabaseHelper

}//end OrmLiteSqliteOpenHelper
