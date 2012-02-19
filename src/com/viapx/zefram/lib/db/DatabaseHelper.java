package com.viapx.zefram.lib.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.viapx.zefram.*;

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
     * Constructor
     * @param context
     */
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        
    }//end DatabaseHelper

    /**
     * 
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource)
    {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        // TODO Auto-generated method stub

    }

}
