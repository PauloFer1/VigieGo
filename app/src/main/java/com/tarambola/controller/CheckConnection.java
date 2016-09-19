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

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.getActiveNetworkInfo() != null) {
            Log.d("Connectivity", "Connection...");
            checkDataToSend();
        }
        else
            Log.d("Connectivity", "No connection...");
    }

    private void checkDataToSend()
    {

    }


}