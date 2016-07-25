package com.tarambola.model;

import android.content.Context;

import java.util.Date;
import java.util.Map;

/**
 * Created by paulofernandes on 25/07/16.
 */
public class TagData {

    static public class RecTimeLeft
    {
        public RecTimeLeft(){};
        public RecTimeLeft(int days, int hours, int min, int secs){
            this.days=days;this.hours=hours;this.minutes=minutes;this.seconds=secs;
        };

        public int days;
        public int hours;
        public int minutes;
        public int seconds;
    };

    /* TAG INFO */
    private String      mIdNumber;
    private String      mFirmwareVer;
    private String      mHardwareVer;
    private Date        mCalibrateDate;
    private Date        mExpirationDate;
    private int         mNumberRecs;
    private String      mRecTimeLeft;
    private String      mProdDesc;
    private Date        mStartDateRec;
    private Date        mEndDateRec;
    private short[]     mTemps;
    private int         mMeasureLenght;
    private Map         mTempsMap;
    private int         mMinTemp;
    private int         mMaxTemp;

    public TagData()
    {

    }

    /* *********************************** GETTERS ****************************************** */


    /* *********************************** SETTERS ****************************************** */

    /**
     * Set ID Number of the Tag
     * @param id the string of id number
     */
    public void setIdNumber(String id){
        this.mIdNumber = id;
    }

    /**
     * Set firmware version of the Tag
     * @param version the string of version
     */
    public void setFirmwareVer(String version){
        this.mFirmwareVer = version;
    }

    /**
     * Set hardware version of the Tag
     * @param version the string of version
     */
    public void setHardwareVer(String version){
        this.mHardwareVer = version;
    }

    /**
     * Set calibration date of the Tag
     * @param date the Date of Calibration
     */
    public void setCalibrateDate(Date date){
        this.mCalibrateDate = date;
    }

    /**
     * Set Expiration date of the Tag
     * @param date the Date of Expiration
     */
    public void setExpirationDate(Date date){
        this.mCalibrateDate = date;
    }

    /**
     * Set Number of records of the Tag
     * @param number the int number of records
     */
    public void setNumberRecs(int number){
        this.mNumberRecs = number;
    }
    /**
     * Set the record time left of the Tag
     * @param String time with the format XXXd YYh ZZmin WWs
     */
    public void setRecTimeLeft(String time){
        this.mRecTimeLeft = time;
    }

    /**
     * Set the description of the tag
     * @param desc String of the description
     */
    public void setProdDesc(String desc){
        this.mProdDesc = desc;
    }

    /**
     * Set start date record of the Tag
     * @param date the Date of record starting
     */
    public void setStartDateRec(Date date){
        this.mStartDateRec = date;
    }

    /**
     * Set end date record of the Tag
     * @param date the Date of record starting
     */
    public void setEndDateRec(Date date){
        this.mEndDateRec = date;
    }

    /**
     * Set Minimum aceptable temperature of tag
     * @param temp the int of the temperature value
     */
    public void setMinTemp(int temp){
        this.mMinTemp = temp;
    }

    /**
     * Set Maximum aceptable temperature of tag
     * @param temp the int of the temperature value
     */
    public void setMaxTemp(int temp){
        this.mMaxTemp = temp;
    }

    /**
     * Set Array of temperatures readings
     * @param temps the Array of temperatures
     */
    public void setTemps(short[] temps){
        this.mTemps = temps;
    }

}
