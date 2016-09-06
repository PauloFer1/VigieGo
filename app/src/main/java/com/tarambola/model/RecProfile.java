package com.tarambola.model;

/**
 * Created by paulofernandes on 05/09/16.
 */
public class RecProfile {

    private static RecProfile ourInstance = new RecProfile();

    private int mSampling;
    private int mMin;
    private int mNOkMin;
    private int mMax;
    private int mNOkMax;

    public static RecProfile getInstance() {
        if(ourInstance==null)
            ourInstance = new RecProfile();
        return ourInstance;
    }


    private RecProfile(){}

    public void setProfile(int sampling, int min, int minNOk, int max, int maxNOk)
    {
        mSampling
    }

}
