package com.viapx.zefram.lib;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.maps.GeoPoint;
import com.j256.ormlite.field.*;
import com.j256.ormlite.table.*;

/**
 * Represents a location
 * @author tjarrett
 *
 */
@DatabaseTable(tableName="locations")
public class Location
{
    /**
     * Default serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The unique id
     */
    @DatabaseField(generatedId=true)
    private int _id;
    
    /**
     * The name of the location
     */
    @DatabaseField(index=true)
    private String name;
    
    /**
     * The latitude of the location
     */
    @DatabaseField
    private int latitude;
    
    /**
     * The longitude of the location
     */
    @DatabaseField
    private int longitude;
    
    /**
     * The radius around the location that is active
     */
    @DatabaseField
    private int radius;
    
    /**
     * Whether or not this location is currently being listened for
     */
    @DatabaseField
    private boolean active;
    
    /**
     * The list of events
     */
    private List<LocationEvent> events;

    /**
     * @return the id
     */
    public int getId()
    {
        return _id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id)
    {
        this._id = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the latitude in microdegrees (same as GeoPoint.getLatitudeE6())
     * @return the latitude
     */
    public int getLatitude()
    {
        return latitude;
    }
    
    /**
     * Returns the latitude in degrees
     * @return the latitude as a double
     */
    public double getLatitudeDegrees()
    {
        return latitude / 1E6;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(int latitude)
    {
        this.latitude = latitude;
    }
    
    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(double latitude)
    {
        this.latitude = (int)(latitude * 1E6);
    }

    /**
     * Returns the longitude in microdegrees (same as GeoPoint.getLongitudeE6())
     * @return the lonitude
     */
    public int getLongitude()
    {
        return longitude;
    }
    
    /**
     * Returns the longitude in degrees
     * @return longitude as double
     */
    public double getLongitudeDegrees()
    {
        return longitude / 1E6;
    }

    /**
     * @param longitude the lonitude to set
     */
    public void setLongitude(int longitude)
    {
        this.longitude = longitude;
    }
    
    /**
     * @param latitude the latitude to set
     */
    public void setLongitude(double longitude)
    {
        this.longitude = (int)(longitude * 1E6);
    }
    
    /**
     * Returns the radius in feet
     * @return the radius in feet
     */
    public int getRadius()
    {
        return radius;
    }
    
    /**
     * Returns the radius in meters
     * @return radius in meters
     */
    public float getRadiusInMeters()
    {
        return (float)radius * .3048f;
    }
    
    /**
     * @param radius radius the radius to set in feet
     */
    public void setRadius(int radius)
    {
        this.radius = radius;
        
    }

    /**
     * @return the active
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    /**
     * Returns a GeoPoint representing this location
     * @return
     */
    public GeoPoint getGeoPoint()
    {
        return new GeoPoint(getLatitude(), getLongitude());
        
    }//end getGeoPoint

}//end Location
