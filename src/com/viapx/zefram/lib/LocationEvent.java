package com.viapx.zefram.lib;

import java.io.Serializable;
import com.j256.ormlite.field.*;
import com.j256.ormlite.table.*;

/**
 * Represents an event in the database
 * @author tjarrett
 *
 */
@DatabaseTable(tableName="location_events")
public class LocationEvent implements Serializable
{
    public enum Type 
    {
        Wifi,
        Bluetooth,
        GPS,
        Ringer,
        Custom
        
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 3289730241288633064L;
    
    /**
     * The id for recording into the database
     */
    @DatabaseField(generatedId=true)
    private int _id;
    
    /**
     * The location this event belongs to
     */
    @DatabaseField(canBeNull=false, foreign=true)
    private Location location;
    
    @DatabaseField
    private Type type;
    
    /**
     * True if this fires when entering the location, false if it fires when leaving the location
     */
    @DatabaseField(canBeNull=false)
    private boolean onEnter = true;
    
    /**
     * The package of the service this event will call
     */
    @DatabaseField(canBeNull=false)
    private String servicePackageName;
    
    /**
     *  The class of the service that this event will call
     */
    @DatabaseField(canBeNull=false)
    private String serviceClassName;
    
    /**
     * The extra info to pass along -- this might be something like "Off" or "On"
     */
    @DatabaseField
    private String extra;
    
    /**
     * @return the id
     */
    public int getId()
    {
        return _id;
        
    }//end getId

    /**
     * @param id the id to set
     */
    public void setId(int id)
    {
        this._id = id;
        
    }//end setId
    
    /**
     * Get the location that this event belongs to
     * @return
     */
    public Location getLocation()
    {
        return this.location;
        
    }//end getLocation
    
    /**
     * Set the location that this event belongs to
     * @param location
     */
    public void setLocation(Location location)
    {
        this.location = location;
        
    }//end setLocation
    
    /**
     * Get the type of this event
     * @return
     */
    public Type getType()
    {
        return type;
        
    }//end getType
    
    /**
     * Set the type of this events
     * @param type
     */
    public void setType(Type type)
    {
        this.type = type;
        
    }//end setType
    
    /**
     * Returns whether this event fires when entering (true) or leaving (false) a location
     * @return
     */
    public boolean getOnEnter()
    {
        return onEnter;
        
    }//end getOnEnter
    
    /**
     * Sets whether this event fires when entering (true) or leaving (false) a location
     * @param onEnter
     */
    public void setOnEnter(boolean onEnter)
    {
        this.onEnter = onEnter;
        
    }//end setOnEnter
    
    /**
     * Set the service package name -- go ahead and make this public if it makes sense for your class
     * @param servicePackageName
     */
    public void setServicePackageName(String servicePackageName)
    {
        this.servicePackageName = servicePackageName;
        
    }//end setServicePackageName
    
    /**
     * Returns the service package name that this event will call
     * @return
     */
    public String getServicePackageName()
    {
        return servicePackageName;
        
    }//end getServicePackageName
    
    /**
     * Set the service class name that this event will call -- go ahead and make this public if it makes sense for your class
     * @param serviceClassName
     */
    public void setServiceClassName(String serviceClassName)
    {
        this.serviceClassName = serviceClassName;
        
    }//end setServiceClassName
    
    /**
     * Returns the service class name that this event will call
     * @return
     */
    public String getServiceClassName()
    {
        return serviceClassName;
        
    }//end getServiceClassName
    
    /**
     * Set the extra info that this event will pass along
     * @param extra
     */
    public void setExtra(String extra)
    {
        this.extra = extra;
        
    }//end setExtra
    
    /**
     * Returns the extra info that this event will pass along
     * @return
     */
    public String getExtra()
    {
        return extra;
        
    }//end getExtra

}//end LocationEvent
