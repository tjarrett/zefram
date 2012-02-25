package com.viapx.zefram;

import com.google.android.maps.GeoPoint;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.viapx.zefram.lib.*;
import com.viapx.zefram.lib.db.DatabaseHelper;
import com.viapx.zefram.overlays.GestureDetectorOverlay;
import com.viapx.zefram.overlays.LocationsOverlay;

import android.view.GestureDetector.OnGestureListener;

/**
 * 
 * @author tjarrett
 *
 * @see http://stackoverflow.com/questions/1678493/android-maps-how-to-long-click-a-map
 * @see http://stackoverflow.com/questions/4646584/how-to-get-lat-and-long-on-touch-event-from-goole-map
 * @see http://code.google.com/p/mapview-overlay-manager/source/browse/trunk/OverlayManager/src/de/android1/overlaymanager/OverlayManager.java
 * @see http://stackoverflow.com/questions/2176397/drawing-a-line-path-on-google-maps
 * 
 * 
 * 
 * http://stackoverflow.com/questions/3605219/default-marker-for-android-google-mapview
 */
public class ZeframActivity extends MapActivity
{
    /**
     * The MapView
     */
    private MapView mapView;

    /**
     * The controller for MapView
     */
    private MapController mapController;

    /**
     * The built-in MyLocation MapView overlay (show's current location)
     */
    private MyLocationOverlay userLocationOverlay;
    
    /**
     * The zefram locations overlay (for showing locations that the user has configured)
     */
    private LocationsOverlay locationsOverlay;
    
    /**
     * The DatabaseHelper for access our SQLite database
     */
    private DatabaseHelper databaseHelper = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        // Get our map view
        mapView = (MapView)findViewById(R.id.mapview);
        
        //Show the built in zoom controls
        mapView.setBuiltInZoomControls(true);

        // Get our controller
        mapController = mapView.getController();
        
        // Set a reasonable zoom level
        mapController.setZoom(18);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        
        //The first overlay has to be our gesture detection overlay
        Overlay gestureOverlay = new GestureDetectorOverlay(new OnGestureListener(){

            @Override
            public boolean onDown(MotionEvent e)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e)
            {
                int x = (int)e.getX();
                int y = (int)e.getY();
                
                GeoPoint geoPoint = mapView.getProjection().fromPixels(x, y);
                
                Intent result = new Intent();
                result.putExtra("latitude", geoPoint.getLatitudeE6());
                result.putExtra("longitude", geoPoint.getLongitudeE6());
                
                setResult(Activity.RESULT_OK, result); 
                finish();
                /*Toast.makeText(getApplicationContext(), "Clicked at " + x + ", " + y, Toast.LENGTH_SHORT).show();
                
                LocationOverlayItem location = new LocationOverlayItem(geoPoint, "Thing at " + x + "," + y, "Snippit..."); 
                locationsOverlay.add(location);*/
                
                
                //Toast.makeText(getApplicationContext(), "LongPress incoming...!", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
                
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {
                // TODO Auto-generated method stub
                return false;
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
                return false;
            }
            
        });
        mapView.getOverlays().add(gestureOverlay);

        // Let's lay over the user's current location
        userLocationOverlay = new MyLocationOverlay(this, mapView);

        // When we first get a location fix, animate over to the user's location
        userLocationOverlay.runOnFirstFix(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d("Tim", "Animated to location...");
                mapController.animateTo(userLocationOverlay.getMyLocation());

            }// end run

        });

        // Set the overlay to be included
        mapView.getOverlays().add(userLocationOverlay);

        userLocationOverlay.enableMyLocation();
        
        //Get our drawable icon
        Drawable marker = getResources().getDrawable(R.drawable.marker);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        
        //Now add our locations overlay...
        locationsOverlay = new LocationsOverlay(marker);
        mapView.getOverlays().add(locationsOverlay);
        
        //mapView.getOverlays().add(new GeoFenceOverlay(marker)); 

    }// end onCreate

    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        userLocationOverlay.enableMyLocation();
        
    }//end onResume

    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onPause()
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        userLocationOverlay.disableMyLocation();
        
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

    @Override
    protected boolean isRouteDisplayed()
    {
        // TODO Auto-generated method stub
        return false;
        
    }//end isRouteDisplayed

}//end ZeframActivity