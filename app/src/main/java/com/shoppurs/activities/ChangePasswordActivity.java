package com.shoppurs.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ChangePasswordActivity extends NetworkBaseActivity {

    private EditText editTextcurrentPwd;
    private EditText editTextnewPwd;

    private String currentPwd, newPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextcurrentPwd = findViewById(R.id.edit_current_password);
        editTextnewPwd = findViewById(R.id.edit_new_password);
        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPwd = editTextcurrentPwd.getText().toString();
                newPwd = editTextnewPwd.getText().toString();
                //Log.d("mobile ", currentPwd);
                if(TextUtils.isEmpty(currentPwd)) {
                    DialogAndToast.showDialog("Please Enter Your Current Password ", ChangePasswordActivity.this);
                    return;
                }

                if(TextUtils.isEmpty(newPwd)){
                    DialogAndToast.showDialog("Please Enter Your New Password ", ChangePasswordActivity.this);
                    return;
                }else {
                    if(newPwd.length()<5){
                        DialogAndToast.showDialog("Password must be minimum 5 character", ChangePasswordActivity.this);
                        return;
                    }else {
                        changePassword(currentPwd, newPwd);
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

    private void changePassword(String oldPwd, String newPwd){
        Map<String,String> params=new HashMap<>();
        String url=getResources().getString(R.string.url)+"/profile/change_password";
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
            }
        }catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ChangePasswordActivity.this);
        }
    }

}
