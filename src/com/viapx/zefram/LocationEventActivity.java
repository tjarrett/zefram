package com.viapx.zefram;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.viapx.zefram.lib.Location;
import com.viapx.zefram.lib.LocationEvent;
import com.viapx.zefram.lib.LocationEventAction;
import com.viapx.zefram.lib.db.DatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class LocationEventActivity extends Activity
{
    static final int DIALOG_SELECT_EVENT_OCCUR = 1; 
    
    static final int DIALOG_SELECT_EVENT_ACTION = 2;
    
    /**
     * The actions to display in the spinner (keyed by string displayed in the spinner)
     */
    private Map<String, LocationEventAction> actions;
    
    private ArrayList<String> spinnerActions;

    private Spinner actionSpinner;
    
    private String activityAction = "add";
    
    private Integer eventId = null;
    
    private Location location;
    
    private LocationEvent event;
    
    private LinearLayout customFieldContainer;
    
    /**
     * The DatabaseHelper for access our SQLite database
     */
    private DatabaseHelper databaseHelper = null;
    
    /**
     * 
     */
    private Dao<Location, Integer> locationDao;
    
    /**
     * 
     */
    private Dao<LocationEvent, Integer> locationEventDao;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_event);  
        
        //Go get our actions from the xml file
        try {
            populateActionsFromXml();
            
        } catch ( Exception e ) {
            Log.e(LocationListActivity.class.getName(), "Error building spinner", e);
            throw new RuntimeException(e); 
        } 
        
        //Now put our spinner together...
        buildEventActionSpinner();
        
        //Wire up the Done button
        Button doneButton = (Button)findViewById(R.id.location_done_button);
        doneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view)
            {
                saveEvent();
                
            }
            
        });
        
        //Wire up the Cancel button
        Button cancelButton = (Button)findViewById(R.id.location_cancel_button);
        cancelButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view)
            {
                finish();
                
            }
            
        });
        
        //Get the container for custom fields
        customFieldContainer = (LinearLayout)findViewById(R.id.location_event_custom_field_container);
        
    }//end onCreate
    
    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart()
    {
        
        //Set up the database connection
        databaseHelper = (DatabaseHelper)getDatabaseHelper();
        try {
            locationDao = databaseHelper.getDao(Location.class);
            locationEventDao = databaseHelper.getDao(LocationEvent.class);
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }

        //Figure out if we are adding or editing
        activityAction = getIntent().getExtras().getString("action");      
        
        //If editing, load up the event
        if ( "edit".equals(activityAction) ) {
            eventId = getIntent().getExtras().getInt("event_id");
            try {
                //Get the event from the database
                event = locationEventDao.queryForId(eventId);
                
                //Figure out which radio button to check -- and then check it
                int radioButtonId = ( event.getOnEnter() ) ? R.id.location_event_arriving_radio_button : R.id.location_event_leaving_radio_button;
                RadioButton rb = (RadioButton)findViewById(radioButtonId);
                rb.setChecked(true);
               
                //Now take care of the spinner -- figure out which index to select and select it
                int index = spinnerActions.indexOf(event.getDisplayName());
                this.actionSpinner.setSelection(index);
                
            } catch ( SQLException sqle ) {
                Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
                throw new RuntimeException(sqle); 
                
            }
            
            
        }

        
        //Load up location from the database
        int location_id = (int)getIntent().getExtras().getInt("location_id");
        try {
            //Get the location
            location = locationDao.queryForId(location_id);
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not get location dao", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
        // TODO Auto-generated method stub
        super.onStart();
        
    }//end onStart
    

    /* (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
        
    }//end onStop
    
    protected void onDestroy()
    {
        //Release the database
        if ( databaseHelper != null ) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
            
        }
        
        super.onDestroy();
    }
    
    public Dialog onCreateDialog(int id)
    {
        Dialog dialog = null;
        AlertDialog.Builder builder = null;
        switch ( id ) {
            case DIALOG_SELECT_EVENT_OCCUR:
                builder = new AlertDialog.Builder(this);
                builder.setMessage("You must select when this event should occur")
                .setTitle("ERROR")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // TODO Auto-generated method stub
                        
                    }
                });
                dialog = builder.create();
                break;
            
            case DIALOG_SELECT_EVENT_ACTION:
                builder = new AlertDialog.Builder(this);
                builder.setMessage("You must select an event action.")
                .setTitle("ERROR")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        
                    }
                    
                });
                dialog = builder.create();
                break;
            
        }
        
        return dialog;
        
    }
    
    private void populateActionsFromXml() throws XmlPullParserException, IOException
    {
        actions = new Hashtable<String, LocationEventAction>();
        spinnerActions = new ArrayList<String>();
        spinnerActions.add("Select One...");
        
        // See http://android-er.blogspot.com/2010/04/read-xml-resources-in-android-using.html
        XmlResourceParser xrp = getResources().getXml(R.xml.location_event_actions);
        
        xrp.next();
        
        int eventType = xrp.getEventType();
        LocationEventAction lea = null;
        
        while ( eventType != XmlResourceParser.END_DOCUMENT ) {
            //Listen for tag starts
            if ( eventType == XmlResourceParser.START_TAG ) {
                if ( "action".equals(xrp.getName()) ) {
                    //Start our object
                    lea = new LocationEventAction();
                    
                    boolean custom = "true".equals(xrp.getAttributeValue(null, "custom"));
                    String actionPackage = xrp.getAttributeValue(null, "package");
                    String actionClass = xrp.getAttributeValue(null, "class");
                    String extra = xrp.getAttributeValue(null, "extra");
                    
                    lea.setCustom(custom);
                    lea.setActionPackage(actionPackage);
                    lea.setActionClass(actionClass);
                    lea.setExtra(extra);
                    
                }
                
            } else if ( eventType == XmlResourceParser.TEXT ) {
                String text = xrp.getText();
                lea.setText(text);
                
                actions.put(lea.getText(), lea);
                spinnerActions.add(lea.getText());
                
            }
            
            eventType = xrp.next();
            
        }//end while
        
        Log.d(Z.TAG, "At the end of running, we have " + actions.size() + " actions");
        
    }
    
    private void buildEventActionSpinner()
    {
        //See http://www.mkyong.com/android/android-spinner-drop-down-list-example/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            spinnerActions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        actionSpinner = (Spinner)findViewById(R.id.location_event_action_spinner);
        
        actionSpinner.setAdapter(adapter);
        
        actionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Figure out which action we got...
                LocationEventAction action = actions.get(spinnerActions.get(position));
                
                if ( action != null && "Call Custom Service".equals(action.getText()) ) {
                    showCustomFields();
                    
                } else {
                    hideCustomFields();
                    
                }
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
                
            }
            
        });
        
    }//end buildEventActionSpinner
    
    /**
     * Get the OrmLite database helper for this Android project
     * @return
     */
    private OrmLiteSqliteOpenHelper getDatabaseHelper()
    {
        return ZeframLocationRegistrationService.getDatabaseHelper(getApplicationContext()); 
        
    }//end getDatabaseHelper
    
    /**
     * 
     */
    private void saveEvent()
    {
        //Figure out if we are doing this on-enter or on-departure
        RadioButton arrivingButton = (RadioButton)findViewById(R.id.location_event_arriving_radio_button);
        RadioButton leavingButton = (RadioButton)findViewById(R.id.location_event_leaving_radio_button);
        
        boolean onEnter = true;
        
        if ( arrivingButton.isChecked() && !leavingButton.isChecked() ) {
            onEnter = true;
            
        } else if ( !arrivingButton.isChecked() && leavingButton.isChecked() ) {
            onEnter = false;
            
        } else {
            showDialog(DIALOG_SELECT_EVENT_OCCUR);
            return;
            
        }
        
        //Get the action info based on the spinner...
        LocationEventAction lea = null;
        String selectedItem = (String)actionSpinner.getSelectedItem();
        if ( actions.containsKey(selectedItem) ) {
            lea = actions.get(selectedItem);
            
        } else {
            showDialog(DIALOG_SELECT_EVENT_ACTION);
            return;
        }
        
        
        if ( "add".equals(activityAction) ) {
            event = new LocationEvent();
            
        }
        

        
        if ( customFieldContainer.getVisibility() == View.VISIBLE ) {
            EditText viewPackageName = (EditText)findViewById(R.id.location_event_action_custom_package);
            EditText viewClassName = (EditText)findViewById(R.id.location_event_action_custom_class);
            EditText viewExtra = (EditText)findViewById(R.id.location_event_action_custom_extra);
            
            String classOnly = viewClassName.getText().toString();
            classOnly = classOnly.substring(classOnly.lastIndexOf(".")+1);
            
            event.setLocation(location);
            event.setOnEnter(onEnter);
            event.setServicePackageName(viewPackageName.getText().toString());
            event.setServiceClassName(viewClassName.getText().toString());
            event.setExtra(viewExtra.getText().toString());
            event.setDisplayName("Run custom class " + classOnly); 
            
        } else {
            event.setLocation(location);  
            event.setOnEnter(onEnter);
            event.setServicePackageName(lea.getActionPackage());
            event.setServiceClassName(lea.getActionClass());
            event.setExtra(lea.getExtra());
            event.setDisplayName(lea.getText()); 
            
        }
        

        
        try {
            locationEventDao.createOrUpdate(event);
            Log.d(Z.TAG, "Event saved!");
            
        } catch ( SQLException sqle ) {
            Log.e(LocationListActivity.class.getName(), "Could not save locatione event", sqle);
            throw new RuntimeException(sqle); 
            
        }
        
        finish();

    }//end saveEvent
    
    private void showCustomFields()
    {
        customFieldContainer.setVisibility(View.VISIBLE);
        
        
    }
    
    private void hideCustomFields()
    {
        customFieldContainer.setVisibility(View.GONE);
        
        
    }

}
