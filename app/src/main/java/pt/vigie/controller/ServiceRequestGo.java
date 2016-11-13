package pt.vigie.controller;

/**
 * Created by paulofernandes on 08/09/16.
 */

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


public class ServiceRequestGo {

    public static enum REQUESTS {LOGIN, SEND_DATA};

    private static final String LOGIN_REQUEST = "http://api.vigiesolutions.com/vigiego/user/login";
    private static final String SEND_DATA_REQUEST = "http://www.taradev.pt/users/";

    private HashMap<Integer, String> requestMap;

    public ServiceRequestGo()
    {
        requestMap = new HashMap<Integer, String>();
        requestMap.put(REQUESTS.LOGIN.ordinal(), LOGIN_REQUEST);
        requestMap.put(REQUESTS.SEND_DATA.ordinal(), SEND_DATA_REQUEST);
    }

    /*
     *  Request Login service
     */
    public boolean requestLogin(String email, String password)
    {

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", ServiceRequestGo.md5(password));

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(LOGIN_REQUEST, params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String response) {
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // Loggin succeded
                    if(obj.getBoolean("status")){
                        Log.d("LOGIN:", "Success");
                    }
                    // Login failed
                    else{
                        Log.d("LOGIN:", "Failed");
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    Log.d("LOGIN:", "Error 1");
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // HTTP Res = 404
                if(statusCode == 404){
                    Log.d("LOGIN:", "Error 404");
                }
                // HTTP Res = 500
                else if(statusCode == 500){
                    Log.d("LOGIN:", "Error 500");
                }
                // HTTP Res code other than 404, 500
                else{
                    Log.d("LOGIN:", "Error 2");
                }
            }
        });

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////// HELPERS

    public static String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
