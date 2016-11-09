package com.tarambola.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tarambola.model.Profile;
import com.tarambola.model.ProfileList;
import com.tarambola.model.TagData;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by paulofernandes on 20/09/16.
 */
public class DBAdapter {

    private SQLiteDatabase database;
    private DBConnection dbHelper;

    private static final String READING_TABLE = "reading";
    private static final String TEMPERATURE_TABLE = "temperature";
    private static final String PROFILE_TABLE = "profile";

    private String[] allColumns = { "id", "id_number", "firmware", "hardware", "calibrate_date",
            "expiration_date", "number_rec", "rec_time_left", "prod_desc", "rec_start_date", "rec_end_date",
            "measure_length", "min_temp", "max_temp", "avg_temp", "activation_energy", "min_temp_read", "max_temp_read",
            "kinetic_temp", "breaches_duration", "breaches_count", "fst_down_measure_date", "last_down_neasure_date"};

    private String[] allProfile = {"id", "name", "measure_length", "min_temp", "max_temp", "nok_min_time", "nok_max_time", "start_by_button"};

    private static DBAdapter ourInstance = new DBAdapter();

    public static DBAdapter getInstance() {
        if(ourInstance==null)
            ourInstance = new DBAdapter();
        return ourInstance;
    }

    public DBAdapter(){

    }
    public DBAdapter(Context context) {
        dbHelper = new DBConnection(context);
        database = dbHelper.getDatabase();
    }
    public void init(Context context){
        dbHelper = new DBConnection(context);
        database = dbHelper.getDatabase();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// GETTERS

    public boolean hasTags(){
        Cursor cursor = database.rawQuery("SELECT count(id) AS count FROM " + READING_TABLE, null);

        if( cursor.getInt(cursor.getColumnIndex("count")) > 0)
            return true;
        else
            return false;
    }

    public ProfileList getProfiles(){
        Cursor cursor = database.rawQuery("SELECT * FROM " + PROFILE_TABLE, null);

        ProfileList pf = new ProfileList();

        if (cursor.moveToFirst()) {
            int counter = 0;
            while (!cursor.isAfterLast()) {
                boolean startBtn = false;
                if(cursor.getInt(cursor.getColumnIndex("start_by_button"))==1)
                    startBtn = true;
                Profile p = new Profile(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getLong(cursor.getColumnIndex("measure_length")),
                        cursor.getInt(cursor.getColumnIndex("min_temp")),
                        cursor.getInt(cursor.getColumnIndex("max_temp")),
                        cursor.getInt(cursor.getColumnIndex("nok_min_time")),
                        cursor.getInt(cursor.getColumnIndex("nok_max_time")),
                        startBtn
                        );
                pf.addProfile(p);
                cursor.moveToNext();
            }
        }

        return pf;
    }

    public TagData[] getTags() {

        Cursor cursor = database.rawQuery("SELECT * FROM " + READING_TABLE, null);

        TagData tags[] = new TagData[cursor.getCount()];

        if (cursor.moveToFirst())
        {
            int counter = 0;
            while(!cursor.isAfterLast())
            {
                TagData tag= new TagData();
                tag.setDBID(cursor.getInt(cursor.getColumnIndex("id")));
                tag.setIdNumber(cursor.getString(cursor.getColumnIndex("id_number")));
                tag.setMaxTempRead(cursor.getInt(cursor.getColumnIndex("max_temp_read")));
                tag.setmMinTempRead(cursor.getInt(cursor.getColumnIndex("min_temp_read")));
                tag.setMaxTemp(cursor.getInt(cursor.getColumnIndex("max_temp")));
                tag.setMinTemp(cursor.getInt(cursor.getColumnIndex("min_temp")));
                tag.setActivationEnergy(cursor.getInt(cursor.getColumnIndex("activation_energy")));
                tag.setBreachesCount(cursor.getInt(cursor.getColumnIndex("breaches_count")));
                tag.setFirmwareVer(cursor.getString(cursor.getColumnIndex("firmware")));
                tag.setHardwareVer(cursor.getString(cursor.getColumnIndex("hardware")));
                tag.setKineticTemp(cursor.getInt(cursor.getColumnIndex("kinetic_temp")));
                tag.setNumberRecs(cursor.getInt(cursor.getColumnIndex("number_rec")));
                tag.setMeasureLenght(cursor.getInt(cursor.getColumnIndex("measure_length")));
                tag.setProdDesc(cursor.getString(cursor.getColumnIndex("prod_desc")));
                tag.setLatitude(cursor.getInt(cursor.getColumnIndex("latitude")));
                tag.setLongitude(cursor.getInt(cursor.getColumnIndex("longitude")));
                tag.setAltitude(cursor.getInt(cursor.getColumnIndex("altitude")));

                DateFormat format = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss", Locale.getDefault());

                Date date=null;
                try {date = format.parse(cursor.getString(cursor.getColumnIndex("calibrate_date")));} catch (ParseException e) {e.printStackTrace();}
                tag.setCalibrateDate(date);

                try {date = format.parse(cursor.getString(cursor.getColumnIndex("expiration_date")));} catch (ParseException e) {e.printStackTrace();}
                tag.setExpirationDate(date);

                try {date = format.parse(cursor.getString(cursor.getColumnIndex("rec_start_date")));} catch (ParseException e) {e.printStackTrace();}
                tag.setStartDateRec(date);

                try {date = format.parse(cursor.getString(cursor.getColumnIndex("rec_end_date")));} catch (ParseException e) {e.printStackTrace();}
                tag.setEndDateRec(date);

                try {date = format.parse(cursor.getString(cursor.getColumnIndex("calibrate_date")));} catch (ParseException e) {e.printStackTrace();}

                tags[counter] = tag;

                counter++;
                cursor.moveToNext();
            }
        }

        return tags;
    }

    public short[] getTempsByReading(int readingId)
    {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TEMPERATURE_TABLE + " WHERE reading_id=" + readingId, null);

        short temps[] = new short[cursor.getCount()];

        if (cursor.moveToFirst())
        {
            int counter =0;
            while(!cursor.isAfterLast())
            {
                temps[counter] = (short)cursor.getInt(cursor.getColumnIndex("value"));

                counter++;
                cursor.moveToNext();
            }
        }

        return temps;
    }

    public TagData getTagReading(int idReading)
    {
        Cursor cursor = database.query(READING_TABLE, allColumns, "id" + " = " + idReading, null,null, null, null);
        cursor.moveToFirst();

        TagData tag = new TagData();

        tag.setIdNumber(cursor.getString(cursor.getColumnIndex("id_number")));

        return tag;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// INSERT

    public long insertProfile(String name, int measureLengt, int minTemp, int nOkMinTime, int maxTemp, int nOkMaxTime, boolean startByButton){

        ContentValues values = new ContentValues();
        values.put("measure_length", measureLengt);
        values.put("name", name);
        values.put("min_temp", minTemp);
        values.put("nok_min_time", nOkMinTime);
        values.put("max_temp", maxTemp);
        values.put("nok_max_time", nOkMaxTime);
        int startBtn = 0;
        if(startByButton)
            startBtn = 1;
        values.put("start_by_button", startBtn);

        long insertId = database.insert(PROFILE_TABLE, null, values);

        return insertId;
    }

    public boolean insertReading(String idNumber,
                                 String firmware,
                                 String hardware,
                                 String calibrateDate,
                                 String expirationDae,
                                 int numberRec,
                                 String recTimeLeft,
                                 String prodDesc,
                                 String recStartDate,
                                 String recEndDate,
                                 long measureLength,
                                 int minTemp,
                                 int maxTemp,
                                 int avgTemp,
                                 long activationEnergy,
                                 int minTemRead,
                                 int maxTempRead,
                                 long kineticTemp,
                                 long breachesDuration,
                                 int breachesCount,
                                 String fstDownMeasure,
                                 String lastDownMeasure) {
        ContentValues values = new ContentValues();
        values.put("id_number", idNumber);
        values.put("firmware",firmware);
        values.put("calibrate_date",calibrateDate);
        values.put("expiration_date",expirationDae);
        values.put("number_rec", numberRec);
        values.put("rec_time_left",recTimeLeft);
        values.put("prod_desc",prodDesc);
        values.put("rec_start_date",recStartDate);
        values.put("rec_end_date",recEndDate);
        values.put("measure_length",measureLength);
        values.put("min_temp",minTemp);
        values.put("max_temp",maxTemp);
        values.put("avg_temp",avgTemp);
        values.put("activation_energy",activationEnergy);
        values.put("min_temp_read",minTemRead);
        values.put("max_temp_read",maxTempRead);
        values.put("kinetic_temp",kineticTemp);
        values.put("breaches_duration",breachesDuration);
        values.put("breaches_count",breachesCount);
        values.put("fst_down_measure_date",fstDownMeasure);
        values.put("last_down_measure_date",lastDownMeasure);

        long insertId = database.insert(READING_TABLE, null, values);
        // To show how to query
      //  Cursor cursor = database.query(READING_TABLE, allColumns, DB.ID + " = " + insertId, null,null, null, null);
       // cursor.moveToFirst();
        return true;
    }
    public long insertReading(TagData tag) {
        ContentValues values = new ContentValues();
        values.put("id_number", tag.getIdNumber());
        values.put("firmware",tag.getFirmwareVer());
        values.put("calibrate_date",tag.getCalibrateDate().toString());
        values.put("expiration_date",tag.getExpirationDate().toString());
        values.put("number_rec", tag.getNumberRecs());
        values.put("rec_time_left",tag.getRecTimeLeft());
        values.put("prod_desc",tag.getProdDesc());
        values.put("rec_start_date",tag.getStartDateRec().toString());
        values.put("rec_end_date",tag.getEndDateRec().toString());
        values.put("measure_length",tag.getMeasureLength());
        values.put("min_temp",tag.getMinTemp());
        values.put("max_temp",tag.getMaxtemp());
        values.put("avg_temp",tag.getAverageTemp());
        values.put("activation_energy",tag.getActivationEnergy());
        values.put("min_temp_read",tag.getMinTempRead());
        values.put("max_temp_read",tag.getMaxtempRead());
        values.put("kinetic_temp",tag.getKineticTemp());
        values.put("breaches_duration",tag.getBreachesDuration());
        values.put("breaches_count",tag.getBreachesCount());
        values.put("fst_down_measure_date",tag.getFstDownMeasuredate().toString());
        values.put("last_down_measure_date",tag.getLastDownMeasureDate().toString());

        long insertId = database.insert(READING_TABLE, null, values);
        // To show how to query
        //  Cursor cursor = database.query(READING_TABLE, allColumns, DB.ID + " = " + insertId, null,null, null, null);
        // cursor.moveToFirst();
        return insertId;
    }

    public boolean insertTemps(int readingID, short[] temps)
    {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// UPDATES

    public void updateProfile(int id, String name, int measureLengt, int minTemp, int nOkMinTime, int maxTemp, int nOkMaxTime, boolean startByButton)
    {
        ContentValues values = new ContentValues();
        values.put("measure_length", measureLengt);
        values.put("name", name);
        values.put("min_temp", minTemp);
        values.put("nok_min_time", nOkMinTime);
        values.put("max_temp", maxTemp);
        values.put("nok_max_time", nOkMaxTime);
        int startBtn = 0;
        if(startByButton)
            startBtn = 1;
        values.put("start_by_button", startBtn);

        database.update(PROFILE_TABLE, values, "id="+id, null);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// DELETE

    public void deleteReading (int idReading)
    {
        database.delete(READING_TABLE, "id" + " = " + idReading, null);
    }

    public void deleteTemps (int idReading)
    {
        database.delete(TEMPERATURE_TABLE, "reading_id" + " = " + idReading, null);
    }
    public void deleteProfileByName(String name){
        database.delete(PROFILE_TABLE, "name" + " LIKE '" + name + "'", null);
    }

    /////////////////////////// CLOSE DATABASE
    public void closeDB()
    {
        database.close();
    }

}
