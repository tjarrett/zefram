package com.viapx.zefram.lib;

public class LocationEventAction
{
    /**
     * Whether or not this is a custom action
     */
    private boolean custom;
    
    /**
     * The package to call when this action is selected (really think of this as the application)
     */
    private String actionPackage;
    
    /**
     * The class to call when this action is selected (think of this as the class within the application)
     */
    private String actionClass;
    
    /**
     * The extra string to be sent to the above class
     */
    private String extra;
    
    /**
     * The display name of this action
     */
    private String text;
    
    public LocationEventAction()
    {
        
    }

    public boolean isCustom()
    {
        return custom;
    }

    public void setCustom(boolean custom)
    {
        this.custom = custom;
    }

    public String getActionPackage()
    {
        return actionPackage;
    }

    public void setActionPackage(String actionPackage)
    {
        this.actionPackage = actionPackage;
    }

    public String getActionClass()
    {
        return actionClass;
    }

    public void setActionClass(String actionClass)
    {
        this.actionClass = actionClass;
    }

    public String getExtra()
    {
        return extra;
    }

    public void setExtra(String extra)
    {
        this.extra = extra;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
    


}//end LocationEventAction
