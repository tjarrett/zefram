package com.viapx.zefram;

import java.sql.SQLException;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.LocationEvent;
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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The activity for viewing/editing a Zefram location
 * 
 * @author tjarrett
 */
public class LocationActivity extends MapActivity
{   
    /**
     * Dialog ID for alerting that a location name is invalid
     */
    static final int DIALOG_LOCATION_NAME_INVALID = 0;
    
    /**
     * Result code for an intent coming back from the ZeframActivity (picking the location on a map)
     */
    static final int INTENT_RESULT_LOCATION = 0;
    
    /**
     * Result code for an intent coming back from adding an event
     */
    static final int INTENT_RESULT_EVENT = 1;
    
    /**
     * The current location that is being added/editted/views
     */
    private Location location;
    
    /**
     * The DatabaseHelper for access our SQLite database
     */
    private DatabaseHelper databaseHelper = null;
    
    /**
     * Location Data Access Object
     */
    private Dao<Location, Integer> locationDao;
    
    /**
     * LocationEvent Data Access Object
     */
    private Dao<LocationEvent, Integer> locationEventDao;
    
    /**
     * The location name field
     */
    private EditText locationNameField;
    
    /**
     * The location radius field
     */
    private EditText locationRadiusField;

    /**
     * Action that indicates whether we are adding or editing
     */
    private String action;

    /**
     * Our MapView showing the selected location on the map
     */
    private MapView mapView;

    /**
     * Our MapView's controller -- just for ease of use
     */
    private MapController mapController;
    
    /**
     * Reference for sending messages to the ZeframLocationRegistrationService
     */
    private Messenger locationService = null;
    
    /**
     * The zefram locations overlay (for showing locations that the user has configured)
     */
    private LocationsOverlay locationsOverlay;
    
    /**
     * Our connection to the ZeframLocationRegistrationService
     */
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

    /**
     * Reference to the item that contains the event list
     */
    private LinearLayout eventList;

    /**
     * Our list of location events
     */
    private List<LocationEvent> locationEvents;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //Do the typical setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        //Wire up to our service
        bindService(new Intent(this, ZeframLocationRegistrationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        
        //Store our location name field
        locationNameField = (EditText)findViewById(R.id.location_name_field);
        
        //Build our location radius field...
        //Find it...
        locationRadiusField = (EditText)findViewById(R.id.location_radius_field);
        locationRadiusField.setText("30");
        locationRadiusField.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
              //Get the radius (in feet) as an int by trying to parse it as an int
                int radius;
                
                try {
                    radius = Integer.parseInt(locationRadiusField.getText().toString());
                    
                } catch ( NumberFormatException nfe ) {
                    Log.e(LocationListActivity.class.getName(), "Could not parse as int ", nfe);
                    //todo: show dialog
                    return;
                    
                }
                
                location.setRadius(radius);
                
                mapView.invalidate();    
                
            }
            
        });
        
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

    }//end onCreate
    
    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart()
    {
        Log.d(Z.TAG, "In onStart");
        
        //Set up the database connection
        databaseHelper = (DatabaseHelper)getDatabaseHelper();
        try {
            locationDao = databaseHelper.getDao(Location.class);
            locationEventDao = databaseHelper.getDao(LocationEvent.class);
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
        if ( location == null ) {
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
            }
            
        }
        
        // TODO Auto-generated method stub
        super.onStart();
        
    }//end onStart
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onResume()
     */
    @Override
    protected void onResume()
    {    
        Log.d(Z.TAG, "In onResume");
        Log.d(Z.TAG, "Location long: " + location.getLongitudeDegrees());
        
        //Set up our MapView
        mapView = (MapView)findViewById(R.id.mapview);
        
        mapController = mapView.getController();
        
        // Set a reasonable zoom level
        mapController.setZoom(19);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        

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
                
                if ( "edit".equals(action) ) {
                    i.putExtra("location_id", location.getId());
                    
                }
                
                startActivityForResult(i, INTENT_RESULT_LOCATION);  
                return true;
            }
            
        });
        mapView.getOverlays().add(gestureOverlay);
        
        //Get our drawable icon
        Drawable marker = getResources().getDrawable(R.drawable.better_marker_2);
        marker.setBounds(0, 0, marker.getIntrinsicWidth()/4, marker.getIntrinsicHeight()/4);
        
        //Now add our locations overlay...
        locationsOverlay = new LocationsOverlay(marker);
        locationsOverlay.add(location);
        mapView.getOverlays().add(locationsOverlay);
        
        //We never want satelitte mode
        mapView.setSatellite(false);
        
        if ( location.getLatitude() == 0 && location.getLongitude() == 0 ) {
            LocationManager locationManager = (LocationManager)getSystemService(this.getApplicationContext().LOCATION_SERVICE);
            android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            
            if ( lastLocation != null && mapView != null) {
                LocationUtils.centerMapViewOnLocation(mapView, lastLocation);
                
            }
            
        } else {
            mapController.animateTo(location.getGeoPoint());
            
        }
        
        //Force a redraw
        mapView.invalidate();
        
        //Redraw the event list
        redrawEventList();
        
        //For my own debugging purposes, show the longitude and latitude
        TextView longLatText = (TextView)findViewById(R.id.latitude_and_longtiude);
        longLatText.setText("Long: " + location.getLongitudeDegrees() + "; Lat: " + location.getLatitudeDegrees());
        
        // TODO Auto-generated method stub
        super.onResume();
        
    }//end onResume
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onPause()
     */
    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        
        mapView = null;
        locationsOverlay = null;
        eventList = null;
        locationEvents = null;
        
    }//end onPause
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        //Release the service
        unbindService(serviceConnection);
        
        //Release the database
        if ( databaseHelper != null ) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
            
        }
        
        super.onDestroy();
        
    }//end onDestroy
    
    /**
     * Called when a dialog is being created
     */
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
                
            case R.id.menu_add_location_event:
                showLocationEventActivity();
                break;
                
        }//end switch
        
        return super.onMenuItemSelected(featureId, item);
        
    }//end onMenuItemSelected 

    /**
     * Called when an activity result is being returned...
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        Log.d(Z.TAG, "In onActivityResult");
        if ( requestCode == INTENT_RESULT_LOCATION ) {
            if ( resultCode == RESULT_OK ) {
                //A location was picked
                int longitude = data.getExtras().getInt("longitude");
                int latitude = data.getExtras().getInt("latitude");
                
                Log.d(Z.TAG, "Got back: " + latitude + ", " + longitude);
                
                //Update the object
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                
                Log.d(Z.TAG, "Location long: " + location.getLongitudeDegrees());
                
            } 
            
        }
        
    }//end onActivityResult
    
    /**
     * Call this to force the event list to be redrawn from the database
     */
    private void redrawEventList()
    {
        //Show the events associated with this location
        eventList = (LinearLayout)findViewById(R.id.event_list);
        eventList.removeAllViews();
        try {
            locationEvents = locationEventDao.queryForEq("location_id", location.getId());
            Log.d(Z.TAG, "I found " + locationEvents.size() + " events");
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Unable to query for location events", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
        for ( final LocationEvent event : locationEvents ) {
            TextView row = (TextView)View.inflate(this, R.layout.location_event_list_event_item, null);
            String when = ( event.getOnEnter() ) ? "When Arriving: " : "When Leaving: ";
            row.setText(when + event.getDisplayName());
            row.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v)
                {
                    new AlertDialog.Builder(LocationActivity.this)
                    .setTitle("Delete " + event.getDisplayName() + "?")
                    .setMessage("Are you really really sure that you want to delete " + event.getDisplayName() + "?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                locationEventDao.delete(event);
                            } catch ( SQLException sqle ) {
                                Log.e(LocationListActivity.class.getName(), "Unable to delete " + event.getDisplayName(), sqle);
                                throw new RuntimeException(sqle); 
                                
                            }
                            redrawEventList();
                        }})
                     .setNegativeButton(android.R.string.no, null).show();

                    return true;
                    
                }});
            eventList.addView(row);
            
        }
        
    }//end redrawEventList
    
    /**
     * Passes a request to remove a proximity alert for this location to the service responsible for ACTUALLY doing that work
     * @param location      The location that we no longer want proximity alerts for
     * @param delete        Whether we should delete this location from the database in addition
     */
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
        
    }//end removeProximityAlertForLocation
    
    /**
     * Add a proximity alert to the given location 
     * @param location
     */
    private void addProximityAlertForLocation(Location location)
    {
        Message msg = Message.obtain(null, ZeframLocationRegistrationService.MSG_REGISTER_LOCATION, location.getId(), -1, null);
        
        try {
           locationService.send(msg);
           
       } catch ( RemoteException re ) {
           Log.e(LocationListActivity.class.getName(), "Could not send message", re);
           throw new RuntimeException(re); 
           
       }        
        
    }//end addProximityAlertForLocation
    
    /**
     * Create or save the currently displayed location
     */
    private boolean saveLocation()
    {
        //Check that required fields are filled out
        String locationName = locationNameField.getText().toString();
        
        //If something is wrong with the name, complain
        if ( locationName == null || "".equals(locationName.trim()) ) {
            showDialog(DIALOG_LOCATION_NAME_INVALID);
            return false;
            
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
            return false;
            
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
        
        return true;
        
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
        return ZeframLocationRegistrationService.getDatabaseHelper(getApplicationContext()); 
        
    }//end getDatabaseHelper

    /**
     * Needed as part of MapActivity... not really used...
     */
    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
        
    }//end isRouteDisplayed
    
    /**
     * Show LocationEventActivity
     */
    private void showLocationEventActivity()
    {
        showLocationEventActivity(null);
        
    }//end showLocationEventActivity
    
    /**
     * Show the LocationEventActivity for the given event represented by id
     * @param id    The id of the event to display, null if adding
     */
    private void showLocationEventActivity(Integer id)
    {
        if ( !saveLocation() ) {
            return;
        }
        
        //Figure out if we are adding or not
        boolean adding = ( id == null );
        String action = ( adding ) ? "add" : "edit";
        
        Intent i = new Intent(this, LocationEventActivity.class);
        i.putExtra("action", action);
        
        if ( !adding ) {
            i.putExtra("event_id", id);
            
        }
        
        i.putExtra("location_id", location.getId());
        
        startActivity(i);
        
    }//end showLocationEventActivity
    
}//end LocationActivity
