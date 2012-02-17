package com.viapx.zefram.overlays;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.viapx.zefram.lib.Location;

/**
 * 
 * @author tjarrett
 * @see https://github.com/commonsguy/cw-advandroid/blob/master/Maps/NooYawkTouch/src/com/commonsware/android/maptouch/NooYawk.java
 */
public class LocationsOverlay extends ItemizedOverlay<Location>
{
    private List<Location> items = new ArrayList<Location>();
    
    public LocationsOverlay(Drawable defaultMarker)
    {
        super(defaultMarker);
        // TODO Auto-generated constructor stub
        populate();
    }
    
    public void add(Location location)
    {
        items.add(location);
        populate();
    }

    @Override
    protected Location createItem(int i)
    {
        return items.get(i);
        
    }

    @Override
    public int size()
    {
        return items.size();
    }
    
    

}
