package com.shoppurs.activities;

import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shoppurs.R;
import com.shoppurs.utilities.AppController;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ChangePasswordActivity extends NetworkBaseActivity {

    private EditText editTextcurrentPwd;
    private EditText editTextnewPwd;
    private TextView text_info_2;

    private String flag,mobile,currentPwd, newPwd;
    private ImageView imageVisiblity;
    private boolean isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        flag = getIntent().getStringExtra("flag");
        editTextcurrentPwd = findViewById(R.id.edit_current_password);
        editTextnewPwd = findViewById(R.id.edit_new_password);
        text_info_2 = findViewById(R.id.text_info_2);
        imageVisiblity = (ImageView) findViewById(R.id.login_visibility);
        imageVisiblity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible) {
                    isVisible = true;
                    editTextnewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageVisiblity.setImageResource(R.drawable.invisible);
                }
                else{
                    isVisible = false;
                    editTextnewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageVisiblity.setImageResource(R.drawable.visible);
                }
                editTextnewPwd.setSelection(editTextnewPwd.getText().length());
            }
        });


        if(!TextUtils.isEmpty(flag) && flag.equals("ForgotPassword")) {
            editTextcurrentPwd.setVisibility(View.GONE);
            text_info_2.setText("Enter new password below to change your password");
        }
        else editTextcurrentPwd.setVisibility(View.VISIBLE);

        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPwd = editTextcurrentPwd.getText().toString();
                newPwd = editTextnewPwd.getText().toString();
                //Log.d("mobile ", currentPwd);


                if (!TextUtils.isEmpty(flag) && flag.equals("changePassword") && TextUtils.isEmpty(currentPwd)) {
                        DialogAndToast.showDialog("Please Enter Your Current Password ", ChangePasswordActivity.this);
                        return;
                }

                if(TextUtils.isEmpty(newPwd)){
                    DialogAndToast.showDialog("Please Enter Your New Password ", ChangePasswordActivity.this);
                    return;
                }else {
                    if(!TextUtils.isEmpty(flag) && flag.equals("ForgotPassword"))
                        resetPassword(newPwd);
                    else
                   changePassword(currentPwd, newPwd);
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

    private void changePassword(String oldPwd, String newPwd){
        Map<String,String> params=new HashMap<>();
        String url=getResources().getString(R.string.url_customer)+"/api/customers/profile/change_password";
        params.put("currentPassword", oldPwd);
        params.put("newPassword", newPwd);
        params.put("mobile", sharedPreferences.getString(Constants.MOBILE_NO, ""));
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        params.put("dbUserName", sharedPreferences.getString(Constants.DB_USER_NAME, ""));
        params.put("dbPassword", sharedPreferences.getString(Constants.DB_PASSWORD, ""));
        Log.d(TAG, params.toString());
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"ChangePassword");
    }


    private void resetPassword(String newPwd){
        Map<String,String> params=new HashMap<>();
        String url=getResources().getString(R.string.url_customer)+"/useradmin/customer/reset_password";
        params.put("newPssword", newPwd);
        params.put("mobile", getIntent().getStringExtra("mobile"));
        Log.d(TAG, params.toString());
        showProgress(true);
        jsonObjectApiRequestWDB(Request.Method.POST,url,new JSONObject(params),"resetPassword");
    }
    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            Log.d("response", response.toString());
            if (apiName.equals("ChangePassword")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    //  JSONObject dataObject = response.getJSONObject("result");
                    DialogAndToast.showDialog(response.getString("message"), ChangePasswordActivity.this);
                } else {
                    DialogAndToast.showDialog(response.getString("message"), ChangePasswordActivity.this);
                }
            }else if(apiName.equals("resetPassword")){
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    //  JSONObject dataObject = response.getJSONObject("result");
                    DialogAndToast.showDialog(response.getString("message"), ChangePasswordActivity.this);
                } else {
                    DialogAndToast.showDialog(response.getString("message"), ChangePasswordActivity.this);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
           // DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ChangePasswordActivity.this);
        }
    }

    public void jsonObjectApiRequestWDB(int method,String url, JSONObject jsonObject, final String apiName){
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
                DialogAndToast.showDialog(getResources().getString(R.string.common_error_message), ChangePasswordActivity.this);
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
