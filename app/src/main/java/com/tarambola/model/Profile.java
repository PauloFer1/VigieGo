package com.tarambola.model;

import java.util.Date;
import java.util.Map;
import java.util.PriorityQueue;

import eu.blulog.blulib.tdl2.Recording;

/**
 * Created by paulofernandes on 01/09/16.
 */
public class Profile {

    /* TAG INFO */
    private String      mName;
    private long        mMeasureLenght;
    private int         mMinTemp;
    private int         mMaxTemp;
    private int         mNOkMinTime;
    private int         mNOkMaxTime;
    private boolean     mStartByButton;

    public Profile(){};
    public Profile(String name, long measureTime, int minTemp, int maxTemp, int minNOkTemp, int maxNOkTemp, boolean startByBtn)
    {
        mName = name;
        mMeasureLenght = measureTime;
        mMinTemp = minTemp;
        mMaxTemp = maxTemp;
        mNOkMinTime = minNOkTemp;
        mNOkMaxTime = maxNOkTemp;
        mStartByButton = startByBtn;
    }

    //////////////////////////////
    /////////// GETTERS

    public String getName(){
        return mName;
    }
    public long getMeasureLenght()
    {
        return mMeasureLenght;
    }
    public int getMinTemp(){
        return mMinTemp;
    }
    public int getMaxTemp(){
        return mMaxTemp;
    }
    public int getNOkMinTime(){
        return mNOkMinTime;
    }
    public int getNOkMaxTime(){
        return mNOkMaxTime;
    }
    public boolean getStartByButton(){
        return mStartByButton;
    }

    //////////////////////////////
    /////////// SETTERS

    public void setName(String name){mName = name;}
    public void setMeasureLenght(long time){time = mMeasureLenght;}
    public void setMinTemp(int temp){temp = mMinTemp;}
    public void setmMaxTemp(int temp){temp = mMaxTemp;}
    public void setNOkMinTime(int time){mNOkMinTime = time;}
    public void setNOkMaxTime(int time){mNOkMaxTime = time;}
    public void setStartByButton(boolean flag){flag = mStartByButton;}
}
