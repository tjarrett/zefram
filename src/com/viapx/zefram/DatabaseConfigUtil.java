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
    public static final Class<?>[] classes = new Class[] { Location.class, LocationEvent.class };

    public static void main(String[] args) throws Exception
    {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
