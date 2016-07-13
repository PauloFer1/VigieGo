package com.tarambola.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.view.ContextThemeWrapper;

import com.tarambola.view.R;

import eu.blulog.blulib.exceptions.BluException;
import eu.blulog.blulib.exceptions.NoBlutagDedectedException;
import eu.blulog.blulib.nfc.BluTag;

/**
 * Created by Juliusz on 2015-01-22.
 */
public abstract class BackgroundTagProcessor extends AsyncTask<Tag, Integer, String> {
    private int progressBarStyle= ProgressDialog.STYLE_SPINNER;
    private ProgressDialog progressDialog = null;
    protected Activity context;
    protected int title;
    protected int message;
    protected Tag tag;
    private TagOperation tagOperation= TagOperation.Other;

    public void dismissProgressDialog(){
        if (progressDialog!=null  && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public enum TagOperation{StartNewRecording, Other};

    public BackgroundTagProcessor(Activity context,
                                  int title, int message, Tag tag) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.tag = tag;
    }

    public BackgroundTagProcessor(Activity context,
                                  int title, int message, Tag tag, TagOperation tagOperation) {
        this(context, title, message, tag);
        this.tagOperation=tagOperation;
    }

    public BackgroundTagProcessor(Activity context,
                                  int title, int message, Tag tag, int progressBarStyle) {
        this(context, title, message, tag);
        this.progressBarStyle=progressBarStyle;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Dialog), progressBarStyle);
//        progressDialog = new ProgressDialog(context, progressBarStyle);
        progressDialog.setProgressStyle(progressBarStyle);
        progressDialog.setTitle(context.getString(title));
        progressDialog.setMessage(context.getString(message, context.getString(R.string.nfc_device_name)));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (progressBarStyle== ProgressDialog.STYLE_HORIZONTAL){
            BluTag.get().setProgressBar(progressDialog);
/* nie działa tak jak chciałbym
            ProgressBar progressBar = (ProgressBar) progressDialog.findViewById(android.R.id.progress);
            if (progressBar!=null)
//                progressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.Blu), PorterDuff.Mode.MULTIPLY);
                progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
*/
        }

    }


    @Override
    protected String doInBackground(Tag... tag) {
        try {
            processTag(tag[0]);
        }
        catch (NoBlutagDedectedException e){
            return context.getString(R.string.noBlutagDedected, context.getString(R.string.nfc_device_name));
        }
        catch (BluException e) {
            if (tagOperation== TagOperation.StartNewRecording && e.getCodeError()==0xF1)
                return BluException.RECORDING_ALREADY_STARTED;
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... message) {
    }

    @Override
    protected void onPostExecute(String status) {
        if (context.isFinishing())
            return;
        dismissProgressDialog();
        postExecute(status);

    }

    protected abstract void postExecute(String status);

    protected abstract void processTag(Tag tag) throws BluException;

}
