package com.tarambola.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by paulofernandes on 12/09/16.
 */
public class CheckConnection extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent arg1){
        boolean isConnected = arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        if(isConnected){
            Log.d("Debug", "No connection...");
        }
        else{
        //    Toast.makeText(context, "Internet Connected 11", Toast.LENGTH_LONG).show();
            Log.d("Debug", "connection...");
        }
    }


}