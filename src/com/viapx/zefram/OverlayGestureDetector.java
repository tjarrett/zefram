package com.viapx.zefram;


import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Overlay for listening to gestures for the MapView. The MapView treats this class as an OnGestureListener. This class will in turn call 
 * the given OnGestureListener
 * 
 * @author tjarrett
 * @see http://stackoverflow.com/questions/1678493/android-maps-how-to-long-click-a-map
 */
public class OverlayGestureDetector extends Overlay implements OnGestureListener
{
    /**
     * The gesture detector to use -- let the overlay know we want gestures
     */
    private GestureDetector detector;
    
    /**
     * The listener that has been set
     */
    private OnGestureListener listener;
    
    /**
     * Constructor -- if you use this you need to supply your own listener via setOnGestureListener()
     */
    public OverlayGestureDetector()
    {
        detector = new GestureDetector(this);
        
    }//end OverlayGestureDetector
    
    /**
     * Constructor -- but you supply your own listener right now
     * @param listener
     */
    public OverlayGestureDetector(OnGestureListener listener)
    {
        this();
        setOnGestureListener(listener);
        
    }//end OverlayGestureDetector

    /**
     * Set a gesture listener
     * @param listener
     */
    public void setOnGestureListener(OnGestureListener listener)
    {
        this.listener = listener;
        
    }//end setOnGestureListener

    /* (non-Javadoc)
     * @see com.google.android.maps.Overlay#onTouchEvent(android.view.MotionEvent, com.google.android.maps.MapView)
     */
    @Override
    public boolean onTouchEvent(MotionEvent e, MapView mapView)
    {
        if ( detector.onTouchEvent(e) ) {
            return true;
            
        } else {
            return false;
            
        }

    }//end onTouchEvent

    @Override
    public boolean onDown(MotionEvent e)
    {
        if ( listener != null ) {
            return listener.onDown(e);
            
        }
        
        return false;
        
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if ( listener != null ) {
            return listener.onFling(e1, e2, velocityX, velocityY);
            
        }
        
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e)
    {
        if ( listener != null ) {
            listener.onLongPress(e);
            
        }
        
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        if ( listener != null ) {
            return listener.onScroll(e1, e2, distanceX, distanceY);
            
        }
        
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e)
    {
        if ( listener != null ) {
            listener.onShowPress(e);
            
        }
        
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        if ( listener != null ) {
            return listener.onSingleTapUp(e);
            
        }
        
        return false;
    }

}//end OverlayGestureDetector
