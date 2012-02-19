package com.viapx.zefram.lib;

import java.util.List;

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
    @DatabaseField(id=true)
    private int id;
    
    @DatabaseField
    private String name;
    
    @DatabaseField
    private int latitude;
    
    @DatabaseField
    private int lonitude;
    
    @DatabaseField
    private int radius;
    
    @DatabaseField
    private boolean active;
    
    private List<LocationEvent> events;
    

}//end Location
