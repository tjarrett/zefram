package com.viapx.zefram.lib;

import java.util.List;

import com.google.android.maps.GeoPoint;

/**
 * Represents a location
 * @author tjarrett
 *
 */
public class Location
{
    private int _id;
    
    private String name;
    
    private GeoPoint geoPoint;
    
    private int radius;
    
    private boolean active;
    
    private List<LocationEvent> events;
    

}//end Location
