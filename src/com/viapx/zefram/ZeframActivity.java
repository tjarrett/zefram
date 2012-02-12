package com.viapx.zefram;

import com.google.android.maps.GeoPoint;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.viapx.zefram.lib.*;

import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayGestureDetector;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.OverlayManager;
import de.android1.overlaymanager.ZoomEvent;

/**
 * 
 * @author tjarrett
 *
 * @see http://stackoverflow.com/questions/1678493/android-maps-how-to-long-click-a-map
 * @see http://stackoverflow.com/questions/4646584/how-to-get-lat-and-long-on-touch-event-from-goole-map
 * @see http://code.google.com/p/mapview-overlay-manager/source/browse/trunk/OverlayManager/src/de/android1/overlaymanager/OverlayManager.java
 */
public class ZeframActivity extends MapActivity
{
    private MapView mapView;

    private LocationManager locationManager;

    private MapController mapController;

    private MyLocationOverlay userLocationOverlay;
    
    private OverlayManager overlayManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get our map view
        mapView = (MapView)findViewById(R.id.mapview);
        
        //Show the built in zoom controls
        mapView.setBuiltInZoomControls(true);

        // Get our controller
        mapController = mapView.getController();
        
        //Get our overlay manager (see http://code.google.com/p/mapview-overlay-manager/)
        overlayManager = new OverlayManager(getApplication(), mapView);
        ManagedOverlay managedOverlay = overlayManager.createOverlay("zeframOverlay", getResources().getDrawable(R.drawable.pushpin_green));
        managedOverlay.setOnOverlayGestureListener(new ManagedOverlayGestureDetector.OnOverlayGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent arg0, ManagedOverlay arg1, GeoPoint arg2, ManagedOverlayItem arg3)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onLongPress(MotionEvent event, ManagedOverlay overlay)
            {
                int x = (int)event.getX();
                int y = (int)event.getY();
                
                GeoPoint geoPoint = mapView.getProjection().fromPixels(x, y);
                
                overlay.createItem(geoPoint);
                
                
                //Toast.makeText(getApplicationContext(), "LongPress incoming...!", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onLongPressFinished(MotionEvent arg0, ManagedOverlay arg1, GeoPoint arg2, ManagedOverlayItem arg3)
            {
                // TODO Auto-generated method stub
                
            }

            @Override
            public boolean onScrolled(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3, ManagedOverlay arg4)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onSingleTap(MotionEvent arg0, ManagedOverlay arg1, GeoPoint arg2, ManagedOverlayItem arg3)
            {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent arg0, ManagedOverlay arg1)
            {
                // TODO Auto-generated method stub
                return false;
            }
            
        });
        overlayManager.populate();
        
        // Set a reasonable zoom level
        mapController.setZoom(18);
        mapView.setClickable(true);
        mapView.setEnabled(true);

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
        Drawable marker = getResources().getDrawable(R.drawable.pushpin_green);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        
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

    @Override
    protected boolean isRouteDisplayed()
    {
        // TODO Auto-generated method stub
        return false;
        
    }//end isRouteDisplayed

}