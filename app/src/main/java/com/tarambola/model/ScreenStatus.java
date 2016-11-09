package com.tarambola.model;

/**
 * Created by paulofernandes on 30/09/16.
 */
public class ScreenStatus {

    private String mStatus;

    private static ScreenStatus ourInstance = new ScreenStatus();

    public static ScreenStatus getInstance(){
        if(ourInstance==null)
            ourInstance = new ScreenStatus();
        return ourInstance;
    }

    private ScreenStatus(){}

    /*
     *  get Status
     */
    public String getStatus()
    {
        return mStatus;
    }

    /*
     *  Set status
     */
    public void setStatus(String status)
    {
        mStatus = status;
    }
}
