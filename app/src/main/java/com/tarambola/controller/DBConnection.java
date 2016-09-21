package com.tarambola.controller;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by paulofernandes on 20/09/16.
 */
public class DBConnection extends SQLiteOpenHelper {

    private String DB_PATH;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "vigiego_db.db";

    public SQLiteDatabase mDataBase;

    private Context mContext;

    private boolean mHasDataBase = false;

    public DBConnection(Context context)
    {

        super(context,DATABASE_NAME,null,1);

        mContext = context;
        DB_PATH = "/data/data/" + context.getApplicationContext().getPackageName()+"/databases/";

        Log.d("PATH:", DB_PATH);

        boolean dbexist = checkdatabase();
        if (dbexist) {
            opendatabase();
            mHasDataBase = true;
            Log.d("DATABASE:", "Database already exist");
        } else {
            Log.d("DATABASE", "Database doesn't exist");
            if(createdatabase())
                mHasDataBase = true;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /*
     *  If database doesn't exists on android path copy it to path
     */
    public boolean createdatabase()
    {
        boolean dbexist = checkdatabase();
        if (dbexist) {
            //System.out.println(" Database exists.");
            return true;
        } else {
            this.getReadableDatabase();
            return copydatabase();
        }
    }

    /*
     *  Check if database exists on android path
     */
    private boolean checkdatabase()
    {
        //SQLiteDatabase checkdb = null;
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            //checkdb = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }

        return checkdb;
    }

    /*
     *  Copy database from asset folder to package/databases
     */
    private boolean copydatabase()
    {
        //Open your local db as the input stream
        InputStream input = null;
        try {
            input = mContext.getAssets().open("db/"+DATABASE_NAME);
        }catch (IOException err){
            return false;
        }

        // Path to the just created empty db
        String outfilename = DB_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream output = null;
        try {
            output = new FileOutputStream(outfilename);
        }catch (IOException err){
            return false;
        }

        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            //Close the streams
            output.flush();
            output.close();
            input.close();

        }catch (IOException err){
            return false;
        }
        return true;
    }
    public void opendatabase() {
        //Open the database
        String dbPath = DB_PATH + DATABASE_NAME;
        mDataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if (mDataBase != null) {
            mDataBase.close();
        }
        super.close();
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////// GETTERS

    public SQLiteDatabase getDatabase()
    {
        return mDataBase;
    }
}
