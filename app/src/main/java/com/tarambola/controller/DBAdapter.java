package com.tarambola.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    private String[] allColumns = { "id", "id_number", "firmware", "hardware", "calibrate_date",
            "expiration_date", "number_rec", "rec_time_left", "prod_desc", "rec_start_date", "rec_end_date",
            "measure_length", "min_temp", "max_temp", "avg_temp", "activation_energy", "min_temp_read", "max_temp_read",
            "kinetic_temp", "breaches_duration", "breaches_count", "fst_down_measure", "last_down_neasure"};

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
        values.put("fst_down_measure",fstDownMeasure);
        values.put("last_down_measure",lastDownMeasure);

        long insertId = database.insert(READING_TABLE, null, values);
        // To show how to query
      //  Cursor cursor = database.query(READING_TABLE, allColumns, DB.ID + " = " + insertId, null,null, null, null);
       // cursor.moveToFirst();
        return true;
    }

    public boolean insertTemps(int readingID, short[] temps)
    {
        return true;
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


}
