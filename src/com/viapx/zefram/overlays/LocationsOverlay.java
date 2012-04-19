package com.viapx.zefram.overlays;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.viapx.zefram.lib.Location;

/**
 * An overlay for displaying Zefram location(s) on a MapView
 * @author tjarrett
 * @see https://github.com/commonsguy/cw-advandroid/blob/master/Maps/NooYawkTouch/src/com/commonsware/android/maptouch/NooYawk.java
 */
public class LocationsOverlay extends ItemizedOverlay<LocationOverlayItem>
{
    /**
     * The list of items to display
     */
    private List<LocationOverlayItem> items = new ArrayList<LocationOverlayItem>();
    
    /**
     * Constructor
     * @param defaultMarker     The icon to use as the default marker
     */
    public LocationsOverlay(Drawable defaultMarker)
    {
        super(boundCenter(defaultMarker));
        
        //Gotta call populate() before anything else can happen... since it's protected... easy to just call it here...
        //http://code.google.com/p/android/issues/detail?id=2035
        populate();
        
    }//end LocationsOverlay
    
    /**
     * Add a location to be displayed
     * @param location
     */
    public void add(Location location)
    {
        LocationOverlayItem loi = new LocationOverlayItem(location);
        add(loi);
        
    }//end add
    
    /**
     * Add a LocationOverlayItem to the map
     * @param location
     */
    public void add(LocationOverlayItem location)
    {
        items.add(location);
        populate();
        
    }//end add

    /**
     * Return the item at that particular index
     */
    @Override
    protected LocationOverlayItem createItem(int i)
    {
        return items.get(i);
        
    }//end createItem

    /**
     * The number of items that we have
     */
    @Override
    public int size()
    {
        return items.size();
        
    }//end size
    
    /**
     * Force things to be redrawn
     */
    public void invalidate()
    {
        populate();
        
    }//end invalidate

    /* (non-Javadoc)
     * @see com.google.android.maps.ItemizedOverlay#draw(android.graphics.Canvas, com.google.android.maps.MapView, boolean)
     * @see http://stackoverflow.com/questions/6029529/draw-circle-of-certain-radius-on-map-view-in-android
     * @see http://stackoverflow.com/questions/2077054/how-to-compute-a-radius-around-a-point-in-an-android-mapview
     */
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow)
    {
        if ( shadow ) {
            
        } else {
            for ( LocationOverlayItem locationItem : items ) {
                drawCircle(canvas, mapView, locationItem.getLocation());
                
            }//end for
            
        }
        
        // TODO Auto-generated method stub
        super.draw(canvas, mapView, shadow);
        
    }//end draw
    
    /**
     * Convert meters to radius
     * @param meters
     * @param map
     * @param latitude
     * @return
     */
    public static int metersToRadius(float meters, MapView map, double latitude) 
    {
        return (int) (map.getProjection().metersToEquatorPixels(meters) * (1 / android.util.FloatMath.cos((float)Math.toRadians(latitude))));
        
    }//end metersToRadius    
    
    /**
     * Draw a circle around the location representing it's "geofence"
     * @param canvas
     * @param mapView
     * @param location
     */
    public void drawCircle(Canvas canvas, MapView mapView, Location location)
    {
        Point p = mapView.getProjection().toPixels(location.getGeoPoint(), null);
        int radius = metersToRadius(location.getRadiusInMeters(), mapView, location.getLatitudeDegrees());
        
        Paint paint = new Paint();
        paint.setARGB(45, 83, 127, 198);
        paint.setAntiAlias(true);
       
        canvas.drawCircle(p.x, p.y, radius, paint);
        
        paint = new Paint();
        paint.setARGB(255, 1, 103, 245);
        paint.setAntiAlias(true);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth((int)(radius*.05));
        
        canvas.drawCircle(p.x, p.y, radius, paint);
        
    }//end drawCircle

}//end LocationsOverlay
