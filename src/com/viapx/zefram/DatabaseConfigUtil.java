package com.viapx.zefram;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.viapx.zefram.lib.*;

/**
 * Creates the database configuration file to help avoid Android garbage
 * collection issues
 * 
 * @author tjarrett
 * @see http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#SEC39
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil
{
    /**
     * The "beans" that we want the database to be configured for
     */
    public static final Class<?>[] classes = new Class[] { Location.class, LocationEvent.class };

    /**
     * Main
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        writeConfigFile("ormlite_config.txt", classes);
        
    }//end main
    
}//end DatabaseConfigUtil
