package com.viapx.zefram.lib.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.viapx.zefram.*;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.LocationEvent;

/**
 * Database helper class for creating connections to the database
 * @author tjarrett
 *
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    /**
     * The name of the database
     */
    private static final String DATABASE_NAME = "zefram.db";

    /**
     * The version of the database (don't forget to increase this if database structure changes
     */
    private static final int DATABASE_VERSION = 1;
    
    /**
     * The list of classes supported by this helper...
     */
    public static final Class<?>[] classes = new Class[] {
        Location.class,
        LocationEvent.class
    };

    /**
     * Constructor
     * @param context
     */
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        
    }//end DatabaseHelper

    /**
     * This is called when the database is first created. Usually you should call createTable 
     * statements here to create the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
    {
        //TODO - Figure out a way to get this out of the code... this stuff does not belong here but 
        //it will do for now...
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            
            //Create a table for each entry in classes
            for ( int i=0; i<classes.length; i++ ) {
                TableUtils.createTable(connectionSource, classes[i]);
                
            }//end for i
            
        } catch ( SQLException sqle ) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", sqle);
            throw new RuntimeException(sqle);
            
        }

    }//end onCreate

    /**
     * This is called when your application is upgraded and it has a higher version number. This 
     * allows you to adjust the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        // TODO Auto-generated method stub

    }//end onUpgrade

}//end DatabaseHelper
