package com.viapx.zefram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Class to kick start the Zefram service that listens for proximity changes
 * @author tjarrett
 * @see http://www.vogella.de/articles/AndroidServices/article.html
 *
 */
public class SystemStartupReceiver extends BroadcastReceiver
{
    /**
     * Listen for a system startup and start up our service
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(Z.TAG, "In system startup receiver");
        Intent service = new Intent(context, ZeframLocationRegistrationService.class);
        context.startService(service);

    }//end onReceive

}//end SystemStartupReceiver
