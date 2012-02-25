package com.viapx.zefram;

import java.sql.SQLException;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.LocationUtils;
import com.viapx.zefram.lib.db.DatabaseHelper;
import com.viapx.zefram.overlays.GestureDetectorOverlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LocationActivity extends MapActivity
{   
    static final int DIALOG_LOCATION_NAME_INVALID = 0;
    
    /**
     * 
     */
    private Location location;
    
    /**
     * The DatabaseHelper for access our SQLite database
     */
    private DatabaseHelper databaseHelper = null;
    
    /**
     * 
     */
    private Dao<Location, Integer> locationDao;
    
    /**
     * 
     */
    private EditText locationNameField;
    
    /**
     * 
     */
    private Spinner locationRadiusField;

    private String action;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //Do the typical setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        //Store our location name field
        locationNameField = (EditText)findViewById(R.id.location_name_field);
        
        //Store our location radius field
        locationRadiusField = (Spinner)findViewById(R.id.location_radius_field);
        
        //Wire up the Done button
        Button doneButton = (Button)findViewById(R.id.location_done_button);
        doneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                LocationActivity.this.saveLocation();                
                
            }
            
        });
        
        // Get our map view
        MapView mapView = (MapView)findViewById(R.id.mapview);
        
        // Get our controller
        MapController mapController = mapView.getController();
        
        // Set a reasonable zoom level
        mapController.setZoom(19);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        
        //Set up the database connection
        databaseHelper = (DatabaseHelper)getDatabaseHelper();
        try {
            locationDao = databaseHelper.getDao(Location.class);
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
        action = getIntent().getExtras().getString("action");
        if ( "edit".equals(action) ) {
            //Get the ID from the intent
            int location_id = (int)getIntent().getExtras().getLong("location_id");
            
            //Load up location from the database
            try {
                location = locationDao.queryForId(location_id);
                locationNameField.setText(location.getName());
                
                
            } catch ( SQLException sqle ) {
                Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
                throw new RuntimeException(sqle); 
                
            }
            
        } else {
            location = new Location();
            LocationManager locationManager = (LocationManager)getSystemService(this.getApplicationContext().LOCATION_SERVICE);
            
            android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if ( lastLocation != null ) {
                LocationUtils.centerMapViewOnLocation(mapView, lastLocation);
                
            }
            
        }

        //Use the gesture overlay to listen for a click on the map
        Overlay gestureOverlay = new GestureDetectorOverlay(new OnGestureListener(){

            @Override
            public boolean onDown(MotionEvent e)
            {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
            {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {                
                
                //Toast.makeText(getApplicationContext(), "LongPress incoming...!", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
                
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e)
            {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Single tap on map", Toast.LENGTH_LONG).show();
                return true;
            }
            
        });
        mapView.getOverlays().add(gestureOverlay);
        


    }//end onCreate
    
    protected Dialog onCreateDialog(int id)
    {
        Dialog dialog = null;
        switch ( id ) {
            case DIALOG_LOCATION_NAME_INVALID:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("The location name that you entered is invalid.")
                .setTitle("ERROR")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        
                    }
                    
                });
                dialog = builder.create();
                break;
        }
        
        return dialog;
        
    }//end onCreateDialog
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if ( "edit".equals(action) ) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.location, menu);
            return true;
            
        }
        
        return false;
        
    }//end onCreateOptionsMenu
    
    /**
     * Listen for and handle an item selected in our menu
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item)
    {
        switch ( item.getItemId() ) {
            case R.id.menu_delete_location:
                deleteLocation();
                finish();
                break;
                
        }//end switch
        
        return super.onMenuItemSelected(featureId, item);
        
    }//end onMenuItemSelected 
    
    /**
     * 
     */
    private void saveLocation()
    {
        //Check that required fields are filled out
        String locationName = locationNameField.getText().toString();
        
        if ( locationName == null || "".equals(locationName.trim()) ) {
            showDialog(DIALOG_LOCATION_NAME_INVALID);
            return;
            
        }
        
        location.setName(locationName.trim());
        
        try {
            locationDao.createOrUpdate(location);
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
        
    }//end saveLocation
    
    private void deleteLocation()
    {
        try {
            locationDao.delete(location);
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
    }
    
    /**
     * Get the OrmLite database helper for this Android project
     * @return
     */
    private OrmLiteSqliteOpenHelper getDatabaseHelper()
    {
        return OpenHelperManager.getHelper(this, DatabaseHelper.class);
        
    }//end getDatabaseHelper

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
        
    }//end isRouteDisplayed

}//end LocationActivity
