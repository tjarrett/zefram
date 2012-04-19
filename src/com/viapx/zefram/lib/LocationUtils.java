package com.viapx.zefram.lib;

import android.location.Location;
import com.google.android.maps.*;

/**
 * Utility class for doing some location conversions
 * @author tjarrett
 *
 */
public class LocationUtils
{
    /**
     * Convert the given location into a geopoint and return it
     * 
     * @param location
     * @return
     */
    static public GeoPoint locationToGeoPoint(Location location)
    {
        //Calculate the locations
        int latitude = (int)(location.getLatitude()*1000000);
        int longitude = (int)(location.getLongitude()*1000000);
       
        //Make the geopoint
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        
        //Return it
        return geoPoint;
        
    }//end locationToGeoPoint
    
    /**
     * Given a mapview and a location, center the mapview on the given location
     * 
     * @param mapView
     * @param location
     */
    static public void centerMapViewOnLocation(MapView mapView, Location location)
    {
        //Get the controller
        MapController mapController = mapView.getController();
        
        //Get the geopoint
        GeoPoint geoPoint = locationToGeoPoint(location);
        
        //Center the map
        mapController.animateTo(geoPoint);
        
    }//end centerMapViewOnLocation

}//end LocationUtils
