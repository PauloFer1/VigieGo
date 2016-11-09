package com.tarambola.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tarambola.controller.ServiceRequestGo;
import com.tarambola.model.TagData;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import eu.blulog.blulib.json.HTTP;
import eu.blulog.blulib.json.JSONArray;

/**
 * Created by paulofernandes on 19/09/16.
 */
public class WebServiceRequest {

    private Context mContext;
    private static final String LOGIN_REQUEST = "http://api.vigiesolutions.com/v1/vigie-go/user/login";
    private static final String SEND_DATA_REQUEST = "http://api.vigiesolutions.com/v1/vigie-go/temperatures/save";
    private static final String GET_REPOSRT_PDF = "http://api.vigiesolutions.com/v1/vigie-go/report/";

    /*
     *  Helper to Encrypt String
     */
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

    public WebServiceRequest(){

    }
    public WebServiceRequest(Context context){
        mContext = context;
    }

    /*
    *  Request Login service
    */
    public void doLogin(String email, String password)
    {

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", ServiceRequestGo.md5(password));
        //params.put("password", password);

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

    }

    public void sendData()
    {

        RequestParams params = new RequestParams();

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("notes", "Test api support");
        }catch (JSONException err){}
        try {
            entity = new StringEntity(jsonParams.toString());
        }catch (UnsupportedEncodingException err) {}
        entity.setContentType(new BasicHeader(org.apache.http.protocol.HTTP.CONTENT_TYPE, "application/json"));
        client.post(mContext, SEND_DATA_REQUEST, entity, "application/json", new AsyncHttpResponseHandler(){
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
    }

    public void sendJSON()
    {
        JSONObject Parent = new JSONObject();
        JSONArray array = new JSONArray();

       /* for (int i = 0 ; i < datastreamList.size() ; i++)
        {
            JSONObject jsonObj = new JSONObject();

            jsonObj.put("id", datastreamList.get(i).GetId());
            jsonObj.put("current_value", datastreamList.get(i).GetCurrentValue());
            array.put(jsonObj);
        }*/
        try {
            Parent.put("datastreams", array);
            Parent.put("version", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        URI uri = null;
        try {
            uri = new URI("the server address goes here");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) uri.toURL().openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setDoOutput(true);
        try {
            conn.setRequestMethod("POST");
        }catch (ProtocolException err){}
        conn.addRequestProperty("Content-Type", "application/json");
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(conn.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.write(Parent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void senDataWithVolley(TagData tag)
    {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, SEND_DATA_REQUEST, constructJSON(tag), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.get("http_code").toString().equals("200")) // success
                    {
                        // TODO delete data from database
                        Log.d("Request", "Data sended and saved successfully");
                    }
                    else
                    {
                        Log.d("Request", "Server side Error. Coudn't save data...");
                    }
                } catch (JSONException e) {
                    Log.d("Request", "Error...");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Request", "Data not sended");
            }
        });

        /*
        StringRequest postRequest = new StringRequest(Request.Method.POST, SEND_DATA_REQUEST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // ToDo, delete data from database
                Log.d("Response", response);
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.getMessage());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", "Alif");
                params.put("domain", "http://itsalif.info");

                return params;
            }
        };
        */
        queue.add(jsObjRequest);
    }

    public JSONObject constructJSON(TagData tag)
    {
        JSONObject tagInfo = new JSONObject();
        JSONArray temps = new JSONArray();

        try {

            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            tagInfo.put("mIdNumber", tag.getIdNumber());
            tagInfo.put("mFirmwareVer", tag.getFirmwareVer());
            tagInfo.put("mHardwareVer", tag.getHardwareVer());
            tagInfo.put("mCalibrateDate", fmt.format(tag.getCalibrateDate()));
            tagInfo.put("mExpirationDate", fmt.format(tag.getExpirationDate()));
            tagInfo.put("mNumberRecs", tag.getNumberRecs());
            tagInfo.put("mRecTimeLeft", tag.getRecTimeLeft());
            tagInfo.put("mProdDesc", tag.getProdDesc());
            tagInfo.put("mStartDateRec", fmt.format(tag.getStartDateRec()));
            tagInfo.put("mEndDateRec", fmt.format(tag.getEndDateRec()));
            tagInfo.put("mMeasureLength", tag.getMeasureLength());
            tagInfo.put("mMinTemp", tag.getMinTemp());
            tagInfo.put("mMaxTemp", tag.getMaxtemp());
            tagInfo.put("mActivationEnergy", tag.getActivationEnergy());
            tagInfo.put("mAverageTemp", tag.getAverageTemp());
            tagInfo.put("mMinTempRead", tag.getMinTempRead());
            tagInfo.put("mMaxTempRead", tag.getMaxtempRead());
            tagInfo.put("mKineticTemp", tag.getKineticTemp());
            tagInfo.put("mLongitude", tag.getLongitude());
            tagInfo.put("mLatitude", tag.getLatitude());
            tagInfo.put("mAltitude", tag.getAltitude());

            for(int i =0; i<tag.getTemps().length; i++){
                temps.put(tag.getTemps()[i]);
            }

            tagInfo.put("temps", temps);

        } catch (JSONException err){
            Log.d("JSON", "ERROR: " + err.toString());
        }

        Log.i("JSON", tagInfo.toString());

        return tagInfo;
    }
}
