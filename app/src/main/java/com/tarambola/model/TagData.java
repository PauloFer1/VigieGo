package com.tarambola.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import eu.blulog.blulib.tdl2.Recording;

/**
 * Created by paulofernandes on 25/07/16.
 */
public class TagData implements Serializable{

    static public class RecTimeLeft
    {
        public RecTimeLeft(){};
        public RecTimeLeft(int days, int hours, int min, int secs){
            this.days=days;this.hours=hours;this.minutes=minutes;this.seconds=secs;
        };
        public void convertFromSecs(long secs){
            days = (int) TimeUnit.SECONDS.toDays(secs);
            hours = (int) (TimeUnit.SECONDS.toHours(secs) - (days*24));
            minutes = (int) (TimeUnit.SECONDS.toMinutes(secs) - (days*24) - (hours*60));
            seconds = (int) (secs - (days*24) - (hours*60) - (minutes*60));
        }

        public int days;
        public int hours;
        public int minutes;
        public int seconds;
    };

    private int         mDBID;

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
    private long        mMeasureLenght;
    private Map         mTempsMap;
    private int         mMinTemp;
    private int         mMaxTemp;
    private double      mAverageTemp;
    private long        mActivationEnergy;
    private int         mMinTempRead;
    private int         mMaxTempRead;
    private double      mKinectTemp;
    private PriorityQueue<Recording.Breach>    mRecBreaches;
    private int         mBreachesDuration;
    private int         mBreachesCount;
    private Date        mFstDownMeasuredate;
    private Date        mLastDownMeasureDate;
    private long        mLatitude; // divide by 10000000 to get coordinates
    private long        mLongitude;
    private int         mAltitude;

    public TagData()
    {

    }

    /* *********************************** GETTERS ****************************************** */

    /**
     *  Get ID of the database if saved
     * @return id int
     */
    public int getDBID() {
        return(mDBID);
    }

    /**
     *  Get Tag ID Number
     * @return idNumber String
     */
    public String getIdNumber() {
        return(mIdNumber);
    }
    /**
     *  Get Tag Firmware Version
     * @return firmwareVer String
     */
    public String getFirmwareVer(){
        return(mFirmwareVer);
    }
    /**
     *  Get Tag Hardware Version
     * @return hardwareVer String
     */
    public String getHardwareVer(){
        return(mHardwareVer);
    }
    public String getProdDesc(){
        return(mProdDesc);
    }
    public Date getCalibrateDate(){
        return(mCalibrateDate);
    }
    public Date getExpirationDate(){
        return(mExpirationDate);
    }
    public int getNumberRecs(){
        return(mNumberRecs);
    }
    public String getRecTimeLeft(){
        return mRecTimeLeft;
    }
    public Date getStartDateRec() {
        return mStartDateRec;
    }
    public Date getEndDateRec(){
        return mEndDateRec;
    }
    public short[] getTemps(){
        return mTemps;
    }
    public long getMeasureLength(){
        return mMeasureLenght;
    }
    public Map getTempsMap(){
        return mTempsMap;
    }
    public int getMinTemp(){
        return mMinTemp;
    }
    public int getMaxtemp(){
        return mMaxTemp;
    }
    public int getMinTempRead(){
        return mMinTempRead;
    }
    public int getMaxtempRead(){
        return mMaxTempRead;
    }
    public long getActivationEnergy(){
        return mActivationEnergy;
    }
    public double getAverageTemp() {
        return mAverageTemp;
    }
    public int getLastMeasure(){
        return((int)mTemps[mTemps.length-1]);
    }
    public double getKineticTemp(){
        return mKinectTemp;
    }
    public int getBreachesDuration(){return mBreachesDuration;}
    public PriorityQueue<Recording.Breach> getBreaches(){return mRecBreaches;}
    public int getBreachesCount(){return mBreachesCount;}
    public Date getFstDownMeasuredate(){return mFstDownMeasuredate;};
    public Date getLastDownMeasureDate(){return mLastDownMeasureDate;};
    public long getLatitude(){return mLatitude;};
    public long getLongitude(){return mLongitude;};
    public int getAltitude(){return mAltitude;};


    /* *********************************** SETTERS ****************************************** */

    /**
     * Set ID of database if saved
     * @param id the int of id database
     */
    public void setDBID(int id){
        this.mDBID = id;
    }
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
        this.mExpirationDate = date;
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
     * @param time String with the format XXXd YYh ZZmin WWs
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
     * Set Lenght of Measurement cycle [s]
     * @param lenght the int of the cycle
     */
    public void setMeasureLenght(long lenght){
        this.mMeasureLenght = lenght;
    }

    /**
     * Set Activation Energy
     * @param energy the int of the Energy Value
     */
    public void setActivationEnergy(long energy){
        this.mActivationEnergy = energy;
    }

    /**
     * Set Longitude Coordinate
     * @param longitude the long of the coordinate
     */
    public void setLongitude(long longitude){
        this.mLongitude = longitude;
    }
    /**
     * Set Latitude Coordinate
     * @param latitude the long of the coordinate
     */
    public void setLatitude(long latitude){
        this.mLatitude = latitude;
    }
    /**
     * Set Altitude Coordinate
     * @param altitude the intof the coordinate
     */
    public void setAltitude(int altitude){
        this.mAltitude = altitude;
    }

    /**
     * Set Array of temperatures readings
     * @param temps the Array of temperatures
     */
    public void setTemps(short[] temps){
        this.mTemps = temps;
       // calculateMinTempRead();
       // calculateMaxTempRead();
    }
    public void setKineticTemp(double temp){
        this.mKinectTemp = temp;
    }
    public void setmMinTempRead(int temp){
        this.mMinTempRead = temp;
    }
    public void setMaxTempRead(int temp){
        this.mMaxTempRead = temp;
    }
    public void setAvgTempRead(double temp){
        this.mAverageTemp = temp;
    }
    public void setBreaches(PriorityQueue<Recording.Breach> breaches){
        this.mRecBreaches = breaches;
    }
    public void setBreachesCount(int count){
        this.mBreachesCount = count;
    }
    public void setBreachesDuration(int duration){
        this.mBreachesDuration = duration;
    }
    public void setFstDownMeasuredate(Date date){
        this.mFstDownMeasuredate = date;
    }
    public void setLastDownMeasureDate(Date date){
        this.mLastDownMeasureDate = date;
    }
    //********************************* METHODS
    private void calculateAverageTemp()
    {
        long sum =0;
        for(int i=0; i<mTemps.length; ++i){
            sum += mTemps[i];
        }

        mAverageTemp = (int) (sum/mTemps.length);
    }
    private void calculateMinTempRead()
    {
        int min = 99999;
        for(int i = 0; i<mTemps.length; ++i)
        {
            if((int)mTemps[i]<min)
                min = (int)mTemps[i];
        }
        mMinTempRead = min;
    }
    private void calculateMaxTempRead()
    {
        int max = -99999;
        for(int i = 0; i<mTemps.length; ++i)
        {
            if((int)mTemps[i]>max)
                max = (int)mTemps[i];
        }
        mMaxTempRead= max;
    }
    private void calculateKinectTemp(){
        double R = 8.314459848;
        double topFunct = mActivationEnergy / R;

        double sumExp = 0;
         for(int i =0; i<mTemps.length; i++){
             double topExp = -mActivationEnergy/R*(mTemps[i]/10.0);
             double res = Math.exp(topExp);
             sumExp+=res;
         }
        double bottomExp = sumExp/mTemps.length;
        double bottomFunct = -Math.log(bottomExp);

        mKinectTemp = (float) (float)topFunct/(float)bottomFunct;
    }

}
