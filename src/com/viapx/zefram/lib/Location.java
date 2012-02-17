package com.viapx.zefram.lib;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Location extends OverlayItem
{
    private GeoPoint point;
    
    private String title;
    
    private String snippet;
    
    public Location(GeoPoint point, String title, String snippet)
    {
        super(point, title, snippet);
        
        this.point = point;
        
        this.title = title;
        
        this.snippet = snippet;
        
    }

}
