package com.viapx.zefram.overlays;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.viapx.zefram.lib.LocationOverlayItem;

/**
 * 
 * @author tjarrett
 * @see https://github.com/commonsguy/cw-advandroid/blob/master/Maps/NooYawkTouch/src/com/commonsware/android/maptouch/NooYawk.java
 */
public class LocationsOverlay extends ItemizedOverlay<LocationOverlayItem>
{
    private List<LocationOverlayItem> items = new ArrayList<LocationOverlayItem>();
    
    public LocationsOverlay(Drawable defaultMarker)
    {
        super(defaultMarker);
        
        //Gotta call populate() before anything else can happen... since it's protected... easy to just call it here...
        //http://code.google.com/p/android/issues/detail?id=2035
        populate();
    }
    
    public void add(LocationOverlayItem location)
    {
        items.add(location);
        populate();
    }

    @Override
    protected LocationOverlayItem createItem(int i)
    {
        return items.get(i);
        
    }

    @Override
    public int size()
    {
        return items.size();
    }
    
    

}