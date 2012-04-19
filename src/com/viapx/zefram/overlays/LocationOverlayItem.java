package com.viapx.zefram.overlays;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.viapx.zefram.lib.Location;

/**
 * Class used by LocationsOverlay to draw the location on the map
 * @author tjarrett
 *
 */
public class LocationOverlayItem extends OverlayItem
{
    /**
     * The point where the location will be drawn
     */
    private GeoPoint point;
    
    /**
     * The title to display
     */
    private String title;
    
    /**
     * The snippet to display
     */
    private String snippet;
    
    /**
     * The zefram Location object
     */
    private Location location;
    
    /**
     * Constructor
     * @param point
     * @param title
     * @param snippet
     * @param location
     */
    public LocationOverlayItem(GeoPoint point, String title, String snippet, Location location)
    {
        super(point, title, snippet);
        
        this.point = point;
        
        this.title = title;
        
        this.snippet = snippet;
        
        this.location = location;
        
    }//end LocationOverlayItem
    
    /**
     * Create the LocationOverlayItem from the info inside of the location
     * @param location
     */
    public LocationOverlayItem(Location location)
    {
        this(location.getGeoPoint(), location.getName(), "", location);
        
    }//end LocationOverlayItem
    
    /**
     * Returns the zefram location
     * @return
     */
    public Location getLocation()
    {
        return location;
        
    }//end getLocation

}//end LocationOverlayItem
