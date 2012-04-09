package com.viapx.zefram.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public abstract class LocationEventService extends IntentService
{
    private Handler handler = new Handler();

    public LocationEventService(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Show the toast text
     * @param toastText
     */
    protected void showToast(String toastText)
    {
        handler.post(new DisplayToast(toastText));
        
    }
    
    /**
     * Class for showing toasts inside of my services...
     * @see http://stackoverflow.com/questions/3955410/create-toast-from-intentservice
     * @author tjarrett
     *
     */
    private class DisplayToast implements Runnable
    {
        private String text;
        
        public DisplayToast(String text)
        {
            this.text = text;
            
        }

        @Override
        public void run()
        {
            Toast.makeText(LocationEventService.this, text, Toast.LENGTH_SHORT).show();
            
        }
        
    }

}
