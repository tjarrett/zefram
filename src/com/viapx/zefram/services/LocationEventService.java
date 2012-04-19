package com.viapx.zefram.services;

import android.app.IntentService;
import android.os.Handler;
import android.widget.Toast;

/**
 * Abstract class for representing an IntentService that can handle Location events
 * @author tjarrett
 *
 */
public abstract class LocationEventService extends IntentService
{
    /**
     * The handler
     */
    private Handler handler = new Handler();

    /**
     * Constructor
     * @param name
     */
    public LocationEventService(String name)
    {
        super(name);
        
    }//end LocationEventService
    
    /**
     * Show the toast text
     * @param toastText
     */
    protected void showToast(String toastText)
    {
        handler.post(new DisplayToast(toastText));
        
    }//end showToast
    
    /**
     * Class for showing toasts inside of my services... needs to be done this way to avoid Exceptions
     * @see http://stackoverflow.com/questions/3955410/create-toast-from-intentservice
     * @author tjarrett
     *
     */
    private class DisplayToast implements Runnable
    {
        private String text;
        
        /**
         * Constructor
         * @param text
         */
        public DisplayToast(String text)
        {
            this.text = text;
            
        }//end DisplayToast

        /**
         * Run!
         */
        @Override
        public void run()
        {
            Toast.makeText(LocationEventService.this, text, Toast.LENGTH_SHORT).show();
            
        }//end run
        
    }//end DisplayToast

}//end LocationEventService
