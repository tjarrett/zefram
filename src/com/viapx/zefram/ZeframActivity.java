package com.viapx.zefram;

import com.google.android.maps.GeoPoint;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.viapx.zefram.lib.*;

public class ZeframActivity extends MapActivity
{
    private MapView mapView;
    
    private LocationManager locationManager;
    
    private MapController mapController;
    
    private MyLocationOverlay userLocationOverlay;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Get our map view
        mapView = (MapView)findViewById(R.id.mapview);
        
        //Get our controller
        mapController = mapView.getController();
        
        //Let's lay over the user's current location
        userLocationOverlay = new MyLocationOverlay(this, mapView);
        
        //When we first get a location fix, animate over to the user's location
        userLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run()
            {
                Log.d("Tim", "Animated to location..."); 
                mapController.animateTo(userLocationOverlay.getMyLocation());
                
            }//end run
            
        });
        
        //Set the overlay to be included
        mapView.getOverlays().add(userLocationOverlay);
        
        //Set a reasonable zoom level
        mapController.setZoom(18);
        mapView.setClickable(true);
        mapView.setEnabled(true);
        
        userLocationOverlay.enableMyLocation();
        
        /*
        
        //Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        

        //Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                
                int latitude = (int)(location.getLatitude()*1000000);
                int longitude = (int)(location.getLongitude()*1000000);
                
                GeoPoint gp = new GeoPoint(latitude, longitude);
                
                mapController.animateTo(gp);
                mapController.zoomIn();
                
                mapController.setZoom(6);
                
                locationManager.removeUpdates(this); 
                
                
                Log.d("Tim", Float.toString(location.getAccuracy()));
                
                 
                
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

        // Register the listener with the Location Manager to receive location updates
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 10, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 10, locationListener);
        
        
       
        
        
        //Log.d("Tim", "Lat: " + location.getLatitude());
        //Log.d("Tim", "Long: " + location.getLongitude());

        //GeoPoint geoPoint = new GeoPoint((int)location.getLatitude()*1000000, (int)location.getLongitude()*1000000);
        
        

        
        Location lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.d("Tim", "Last Known: " + lastKnown.getLatitude() + "; " + lastKnown.getLongitude());
        
        LocationUtils.centerMapViewOnLocation(mapView, lastKnown);
        
        mapController.setZoom(18);*/
        
        //mapController.animateTo(geoPoint);
        
    }//end onCreate
    
    @Override
    protected void onResume() {
        super.onResume();
        /*mSensorManager.registerListener(mRotateView,
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);*/
        userLocationOverlay.enableMyLocation();
    }

    @Override
    protected boolean isRouteDisplayed()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}