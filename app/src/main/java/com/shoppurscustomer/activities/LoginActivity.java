package com.shoppurscustomer.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;

import com.shoppurscustomer.R;
import com.shoppurscustomer.utilities.ConnectionDetector;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends NetworkBaseActivity{

    private EditText editTextMobileNumber,editTextPassword;
    private TextView textForgotPassword;
    private Button btnLogin,btnSignUp;
    private String mobile,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextMobileNumber=(EditText)findViewById(R.id.edit_mobile);
        editTextPassword=(EditText)findViewById(R.id.edit_password);
        textForgotPassword=(TextView)findViewById(R.id.text_forgot_password);
        progressDialog.setMessage(getResources().getString(R.string.logging_user));

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                //startActivity(intent);
            }
        });

        //dbHelper.dropAndCreateAllTable();
        //createDatabase();

        if(sharedPreferences.getBoolean(Constants.IS_LOGGED_IN,false)){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin=(Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        btnSignUp=(Button)findViewById(R.id.btn_register);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        for(Drawable drawable : editTextMobileNumber.getCompoundDrawables()){
            if(drawable != null){
                drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.accent_color_1), PorterDuff.Mode.SRC_IN));
            }
        }

        for(Drawable drawable : editTextPassword.getCompoundDrawables()){
            if(drawable != null){
                drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.accent_color_1), PorterDuff.Mode.SRC_IN));
            }
        }

    }

    public void attemptLogin(){
        mobile = editTextMobileNumber.getText().toString();
        password=editTextPassword.getText().toString();
        View focus=null;
        boolean cancel=false;

        if(TextUtils.isEmpty(password)){
            editTextPassword.setError(getResources().getString(R.string.password_required));
            focus=editTextPassword;
            cancel=true;
        }

        if(TextUtils.isEmpty(mobile)){
            editTextMobileNumber.setError(getResources().getString(R.string.mobile_required));
            focus=editTextMobileNumber;
            cancel=true;
        }else if(mobile.length()!=10) {
            editTextMobileNumber.setError(getResources().getString(R.string.valid_mobile));
            focus=editTextMobileNumber;
            cancel=true;
        }

        if(cancel){
            focus.requestFocus();
            return;
        }else {
            if(ConnectionDetector.isNetworkAvailable(this)) {
                progressDialog.setMessage(getResources().getString(R.string.logging_user));
                showProgress(true);
                volleyRequest();
            }else {
                DialogAndToast.showDialog(getResources().getString(R.string.no_internet),this);
            }

        }
    }

    private void volleyRequest(){
        Map<String,String> params=new HashMap<>();
        params.put("mobile",mobile);
        params.put("password",password);
        String url=getResources().getString(R.string.url)+"/loginCustomer";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"loginCustomer");
    }

    private void getFavoriteStore(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.url)+"/shop/favourite_shops";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"getfavoriteshop");
    }


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        if(apiName.equals("getfavoriteshop"))
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("loginCustomer")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONObject dataObject=response.getJSONObject("result");

                    editor.putString(Constants.FULL_NAME,dataObject.getString("username"));
                    editor.putString(Constants.USER_ID,dataObject.getString("userid"));
                    editor.putString(Constants.EMAIL,dataObject.getString("user_email"));
                    editor.putString(Constants.MOBILE_NO,dataObject.getString("mobile"));
                    editor.putString(Constants.DB_NAME,dataObject.getString("dbname"));
                    editor.putString(Constants.DB_USER_NAME,dataObject.getString("dbusername"));
                    editor.putString(Constants.DB_PASSWORD,dataObject.getString("dbpassword"));
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.commit();
                    //DialogAndToast.showToast("Logged In",LoginActivity.this);
                    getFavoriteStore();
                }else {
                    DialogAndToast.showDialog(response.getString("message"),LoginActivity.this);
                }
            }else if(apiName.equals("getfavoriteshop")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    Log.d(TAG, response.toString());
                    List<String> mFavoriteList = new ArrayList();
                    JSONArray jsonArray = response.getJSONArray("result");
                    for (int i= 0;i< jsonArray.length();i++){
                    mFavoriteList.add(jsonArray.getJSONObject(i).getString(""+i));
                    }
                    if(mFavoriteList.size()>0){
                        dbHelper.deleteAllFavorite();
                        dbHelper.add_to_Favorite(mFavoriteList);
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    editor.commit();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),LoginActivity.this);
        }
    }

}
