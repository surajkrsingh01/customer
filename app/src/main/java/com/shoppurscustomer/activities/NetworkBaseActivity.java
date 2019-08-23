package com.shoppurscustomer.activities;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.shoppurscustomer.utilities.AppController;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.JsonArrayRequest;
import com.shoppurscustomer.utilities.JsonArrayRequestV2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkBaseActivity extends BaseActivity {

    private String dbname,  dbuser, dbpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbname = sharedPreferences.getString(Constants.DB_NAME,"");
        dbuser = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbpassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
    }

    protected void jsonArrayApiRequest(int method, String url, JSONObject jsonObject, final String apiName){
        Log.i(TAG,url);
        Log.i(TAG,jsonObject.toString());
        JsonArrayRequest jsonObjectRequest=new JsonArrayRequest(method,url,jsonObject,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,response.toString());
                showProgress(false);
                onJsonArrayResponse(response,apiName);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,"Json Error "+error.toString());
                onServerErrorResponse(error,apiName);
                //  DialogAndToast.showDialog(getResources().getString(R.string.connection_error),BaseActivity.this);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + sharedPreferences.getString(Constants.JWT_TOKEN, ""));
                //params.put("VndUserDetail", appVersion+"#"+deviceName+"#"+osVersionName);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void jsonObjectApiRequest(int method,String url, JSONObject jsonObject, final String apiName){
        try {
            jsonObject.put("dbUserName",dbuser);
            jsonObject.put("dbPassword",dbpassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,url);
        Log.i(TAG,jsonObject.toString());

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(method,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,response.toString());
                showProgress(false);
                onJsonObjectResponse(response,apiName);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,"Json Error "+error.toString());
                showProgress(false);
                onServerErrorResponse(error,apiName);
                // DialogAndToast.showDialog(getResources().getString(R.string.connection_error),BaseActivity.this);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + sharedPreferences.getString(Constants.JWT_TOKEN, ""));
                //params.put("VndUserDetail", appVersion+"#"+deviceName+"#"+osVersionName);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void jsonObjectApiRequestForLoginReg(int method,String url, JSONObject jsonObject, final String apiName){
        try {
            jsonObject.put("dbUserName",dbuser);
            jsonObject.put("dbPassword",dbpassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG,url);
        Log.i(TAG,jsonObject.toString());

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(method,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,response.toString());
                showProgress(false);
                onJsonObjectResponse(response,apiName);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,"Json Error "+error.toString());
                showProgress(false);
                onServerErrorResponse(error,apiName);
                // DialogAndToast.showDialog(getResources().getString(R.string.connection_error),BaseActivity.this);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    protected void stringApiRequest(int method,String url, final String apiName){
        Log.i(TAG,url);
        StringRequest jsonObjectRequest=new StringRequest(method,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,response.toString());
                showProgress(false);
                onStringResponse(response,apiName);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,"Json Error "+error.toString());
                showProgress(false);
                onServerErrorResponse(error,apiName);
                // DialogAndToast.showDialog(getResources().getString(R.string.connection_error),BaseActivity.this);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + sharedPreferences.getString(Constants.JWT_TOKEN, ""));
                //params.put("VndUserDetail", appVersion+"#"+deviceName+"#"+osVersionName);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    protected void jsonArrayV2ApiRequest(int method,String url, JSONArray jsonObject, final String apiName){

        Log.i(TAG,url);
        Log.i(TAG,jsonObject.toString());

        JsonArrayRequestV2 jsonObjectRequest=new JsonArrayRequestV2(method,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG,response.toString());
                AppController.getInstance().getRequestQueue().getCache().clear();
                showProgress(false);
                    onJsonObjectResponse(response,apiName);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i(TAG,"Json Error "+error.toString());
                AppController.getInstance().getRequestQueue().getCache().clear();
                showProgress(false);
                onServerErrorResponse(error,apiName);
                //  DialogAndToast.showDialog(getResources().getString(R.string.connection_error),BaseActivity.this);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + sharedPreferences.getString(Constants.JWT_TOKEN, ""));
                //params.put("VndUserDetail", appVersion+"#"+deviceName+"#"+osVersionName);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void onJsonArrayResponse(JSONArray jsonArray, String apiName) {

    }


    public void onJsonObjectResponse(JSONObject jsonObject, String apiName) {
    }

    public void onStringResponse(String response, String apiName) {

    }

    public void onJsonParserResponse(JSONException error, String apiName) {

    }

    public void onServerErrorResponse(VolleyError error, String apiName) {

    }

    public void onStatusNotOk(String message, String apiName) {

    }
}
