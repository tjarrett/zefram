package com.viapx.zefram;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.LocationOverlayItem;
import com.viapx.zefram.lib.LocationUtils;
import com.viapx.zefram.overlays.GestureDetectorOverlay;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class LocationActivity extends MapActivity
{   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //Do the typical setup stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);
        
        // Get our map view
        MapView mapView = (MapView)findViewById(R.id.mapview);
        
        // Get our controller
        MapController mapController = mapView.getController();
        
        // Set a reasonable zoom level
        mapController.setZoom(19);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        
        //We'll need these down below
        Location location;
        
        String action = getIntent().getExtras().getString("action");
        if ( "edit".equals(action) ) {
            //Load up location from the database
            
        } else {
            location = new Location();
            LocationManager locationManager = (LocationManager)getSystemService(this.getApplicationContext().LOCATION_SERVICE);
            LocationUtils.centerMapViewOnLocation(mapView, locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            
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

    @Override
    protected boolean isRouteDisplayed()
    {
        return false;
        
    }//end isRouteDisplayed

}//end LocationActivity
