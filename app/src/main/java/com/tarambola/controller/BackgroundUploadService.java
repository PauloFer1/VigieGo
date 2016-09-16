package com.tarambola.controller;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.tarambola.model.TagData;

/**
 * Created by paulofernandes on 12/09/16.
 */
public class BackgroundUploadService extends IntentService {

    private TagData     mTagData;


    public BackgroundUploadService() {

        super("VigieUploadService");

    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
      //  String dataString = workIntent.getDataString();

       // mTagData = (TagData)workIntent.getExtras().getSerializable("TagData");

        ResultReceiver rec = workIntent.getParcelableExtra("receiver");

        //String val = workIntent.getStringExtra("foo");

        Bundle bundle = new Bundle();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean hasInternet = cm.getActiveNetworkInfo() != null;

        bundle.putString("resultValue", "My Result Value. Passed in: " + hasInternet);

        rec.send(Activity.RESULT_OK, bundle);


       // if()

    }
}
