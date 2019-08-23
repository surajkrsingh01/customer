package com.shoppurscustomer.activities;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shoppurscustomer.R;
import com.shoppurscustomer.utilities.AppController;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class ForgotPasswordActivity extends NetworkBaseActivity {

    EditText editMobile;
    String myNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editMobile = findViewById(R.id.edit_mobile_no);
        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myNumber = editMobile.getText().toString();
                Log.d("mobile ", myNumber);
                if(TextUtils.isEmpty(myNumber)){
                    Log.d("mobile ", myNumber);
                    DialogAndToast.showDialog("Please Enter Your Mobile Number", ForgotPasswordActivity.this);
                }else {
                    if(myNumber.length()!=10){
                        DialogAndToast.showDialog("Please Enter Valid Mobile Number", ForgotPasswordActivity.this);
                    }else {
                        forgotPassword(myNumber);
                    }
                }
            }
        });
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void forgotPassword(String mobile){
        Map<String,String> params=new HashMap<>();
        String url=getResources().getString(R.string.url)+"/profile/forgot_password?mobile="+mobile;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.GET,url,null,"ForgotPassword");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
            showProgress(false);
        try {
            Log.d("response", response.toString());
            if (apiName.equals("ForgotPassword")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                  //  JSONObject dataObject = response.getJSONObject("result");
                    DialogAndToast.showDialog(response.getString("message"), ForgotPasswordActivity.this);
                } else {
                     DialogAndToast.showDialog(response.getString("message"), ForgotPasswordActivity.this);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ForgotPasswordActivity.this);
        }
    }


    public void jsonObjectApiRequest(int method,String url, JSONObject jsonObject, final String apiName){
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
}
