package com.tarambola.controller;

/**
 * Created by paulofernandes on 08/09/16.
 */

import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;


public class ServiceRequest {

    public static enum REQUESTS {LOGIN, SEND_DATA};

    private static final String LOGIN_REQUEST = "http://www.taradev.pt/login";
    private static final String SEND_DATA_REQUEST = "http://www.taradev.pt/users/";

    private HashMap<Integer, String> requestMap;

    public ServiceRequest()
    {
        requestMap = new HashMap<Integer, String>();
        requestMap.put(REQUESTS.LOGIN.ordinal(), LOGIN_REQUEST);
        requestMap.put(REQUESTS.SEND_DATA.ordinal(), SEND_DATA_REQUEST);
    };

}
