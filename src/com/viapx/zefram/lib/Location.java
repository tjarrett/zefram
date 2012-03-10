package com.viapx.zefram.lib;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

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
     * @return the latitude
     */
    public int getLatitude()
    {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(int latitude)
    {
        this.latitude = latitude;
    }

    /**
     * @return the lonitude
     */
    public int getLongitude()
    {
        return longitude;
    }

    /**
     * @param longitude the lonitude to set
     */
    public void setLongitude(int longitude)
    {
        this.longitude = longitude;
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

}//end Location
