package pt.vigie.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by paulofernandes on 13/09/16.
 */
@SuppressLint("ParcelCreator")
public class UploadReceiver extends ResultReceiver{

    private Receiver mReceiver;

    public UploadReceiver(Handler handler){
        super(handler);
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);

    }
    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }


    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
