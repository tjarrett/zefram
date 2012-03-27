package com.viapx.zefram.lib;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class LocationOverlayItem extends OverlayItem
{
    private GeoPoint point;
    
    private String title;
    
    private String snippet;
    
    private Location location;
    
    public LocationOverlayItem(GeoPoint point, String title, String snippet, Location location)
    {
        super(point, title, snippet);
        
        this.point = point;
        
        this.title = title;
        
        this.snippet = snippet;
        
        this.location = location;
        
    }
    
    public LocationOverlayItem(Location location)
    {
        this(location.getGeoPoint(), location.getName(), "", location);
        
    }
    
    public Location getLocation()
    {
        return location;
        
    }

}
