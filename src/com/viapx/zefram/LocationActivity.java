package com.viapx.zefram;

import java.sql.SQLException;

import com.google.android.maps.GeoPoint;
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
import com.viapx.zefram.overlays.LocationsOverlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class LocationActivity extends MapActivity
{   
    static final int DIALOG_LOCATION_NAME_INVALID = 0;
    
    static final int INTENT_RESULT_LOCATION = 0;
    
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
    private EditText locationRadiusField;

    /**
     * 
     */
    private String action;

    /**
     * 
     */
    private MapView mapView;

    /**
     * 
     */
    private MapController mapController;
    
    /**
     * 
     */
    private Messenger locationService = null;
    
    /**
     * The zefram locations overlay (for showing locations that the user has configured)
     */
    private LocationsOverlay locationsOverlay;
    
    private ServiceConnection serviceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            locationService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName className)
        {
            locationService = null;
            
        }
        
    };
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //Do the typical setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        bindService(new Intent(this, ZeframLocationRegistrationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        //Store our location name field
        locationNameField = (EditText)findViewById(R.id.location_name_field);
        
        //Build our location radius field...
        //Find it...
        locationRadiusField = (EditText)findViewById(R.id.location_radius_field);
        locationRadiusField.setText("30");
        
        //Build the adapter
        ArrayAdapter<CharSequence> radiusAdapter = ArrayAdapter.createFromResource(this, R.array.radius_values, android.R.layout.simple_spinner_item);
        radiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        //Wire up the Done button
        Button doneButton = (Button)findViewById(R.id.location_done_button);
        doneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                LocationActivity.this.saveLocation();     
                LocationActivity.this.finish();
                
            }
            
        });
        
        //Wire up the Cancel button
        Button cancelButton = (Button)findViewById(R.id.location_cancel_button);
        cancelButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view)
            {
                LocationActivity.this.finish();
                
            }
            
        });
        
        mapView = (MapView)findViewById(R.id.mapview);
        
        mapController = mapView.getController();
        
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
                //Get the location
                location = locationDao.queryForId(location_id);
                
                //Populate the name field
                locationNameField.setText(location.getName());
                
                //Populate the radius field...
                String radius = Integer.toString(location.getRadius());
                locationRadiusField.setText(radius);
                
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
                //Toast.makeText(getApplicationContext(), "Single tap on map", Toast.LENGTH_LONG).show();
                //Call the "Edit" activity explicitly
                Intent i = new Intent(LocationActivity.this, ZeframActivity.class);
                startActivityForResult(i, INTENT_RESULT_LOCATION);  
                return true;
            }
            
        });
        mapView.getOverlays().add(gestureOverlay);
        
        //Get our drawable icon
        Drawable marker = getResources().getDrawable(R.drawable.better_marker);
        marker.setBounds(0, 0, marker.getIntrinsicWidth()/4, marker.getIntrinsicHeight()/4);
        
        //Now add our locations overlay...
        locationsOverlay = new LocationsOverlay(marker);
        locationsOverlay.add(location);
        
        mapView.getOverlays().add(locationsOverlay);

    }//end onCreate
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        unbindService(serviceConnection);
        super.onDestroy();
        
    }//end onDestroy
    
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
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        if ( requestCode == INTENT_RESULT_LOCATION ) {
            if ( resultCode == RESULT_OK ) {
                //A location was picked
                int longitude = data.getExtras().getInt("longitude");
                int latitude = data.getExtras().getInt("latitude");
                
                //Update the object
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                
                //Move the map
                GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                mapController.animateTo(geoPoint);
                
                
            }
            
        }
        
    }//end onActivityResult
    
    private void removeProximityAlertForLocation(Location location, boolean delete)
    {
        Log.d(Z.TAG, "Unregistering location...");
        
        int shouldDelete = ( delete ) ? 1 : -1;
        
        Message msg = Message.obtain(null, ZeframLocationRegistrationService.MSG_UNREGISTER_LOCATION, location.getId(), shouldDelete, null);
        
         try {
            locationService.send(msg);
            
        } catch ( RemoteException re ) {
            Log.e(LocationListActivity.class.getName(), "Could not send message", re); 
            throw new RuntimeException(re); 
            
        }
        
    }
    
    private void addProximityAlertForLocation(Location location)
    {
        Message msg = Message.obtain(null, ZeframLocationRegistrationService.MSG_REGISTER_LOCATION, location.getId(), -1, null);
        
        try {
           locationService.send(msg);
           
       } catch ( RemoteException re ) {
           Log.e(LocationListActivity.class.getName(), "Could not send message", re);
           throw new RuntimeException(re); 
           
       }        
        
    }

    
    /**
     * Create or save the currently displayed location
     */
    private void saveLocation()
    {
        //Check that required fields are filled out
        String locationName = locationNameField.getText().toString();
        
        //If something is wrong with the name, complain
        if ( locationName == null || "".equals(locationName.trim()) ) {
            showDialog(DIALOG_LOCATION_NAME_INVALID);
            return;
            
        }
        
        //Otherwise store the name
        location.setName(locationName.trim());
        
        //Get the radius (in feet) as an int by trying to parse it as an int
        int radius;
        
        try {
            radius = Integer.parseInt(locationRadiusField.getText().toString());
            
        } catch ( NumberFormatException nfe ) {
            Log.e(LocationListActivity.class.getName(), "Could not parse as int ", nfe);
            //todo: show dialog
            return;
            
        }
        
        //If we made it this far, then it was some sort of int...
        location.setRadius(radius);
        
        try {
            //Update the location in the database
            locationDao.createOrUpdate(location);
            
            //Remove any existing proximity alert location...
            removeProximityAlertForLocation(location, false);
            
            //Add back in the proximity alert location
            addProximityAlertForLocation(location);
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
    }//end saveLocation
    
    /**
     * Delete the current location
     */
    private void deleteLocation()
    {
        //Remove the proximity alert for this location
        removeProximityAlertForLocation(location, true);
        
    }//end deleteLocation
    
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
