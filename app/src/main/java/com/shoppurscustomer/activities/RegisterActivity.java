package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.utilities.ConnectionDetector;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends NetworkBaseActivity{

    private EditText editFullName,editEmail,editMobile,editPassword,editConfPassword;
    private CheckBox checkBoxTerms;
    private String fullName,email,mobile,password,confPassword;
    private RelativeLayout relative_footer_action;
    private TextView tv_action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        editFullName=(EditText)findViewById(R.id.edit_full_name);
        editEmail=(EditText)findViewById(R.id.edit_email);
        editMobile=(EditText)findViewById(R.id.edit_mobile);
        editPassword=(EditText)findViewById(R.id.edit_password);
        editConfPassword=(EditText)findViewById(R.id.edit_conf_password);
        checkBoxTerms=(CheckBox)findViewById(R.id.checkbox_terms_condition);

        if(!sharedPreferences.getBoolean(Constants.IS_DATABASE_CREATED,false)){
           // createDatabase();
        }

        if(sharedPreferences.getBoolean(Constants.IS_LOGGED_IN,false)){
            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        tv_action = findViewById(R.id.text_action);
        relative_footer_action = findViewById(R.id.relative_footer_action);
        tv_action.setText("Register Now");
        relative_footer_action.setBackgroundColor(getResources().getColor(R.color.green700));

        relative_footer_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    public void attemptRegister(){
        fullName=editFullName.getText().toString();
        email=editEmail.getText().toString();
        mobile=editMobile.getText().toString();
      //  location=editLocation.getText().toString();
       // password=editPassword.getText().toString();
       // confPassword=editConfPassword.getText().toString();
        password=editPassword.getText().toString();
        confPassword=editConfPassword.getText().toString();
        boolean isChecked=checkBoxTerms.isChecked();

        View focus=null;
        boolean cancel=false;

        if(TextUtils.isEmpty(password)){
            focus=editPassword;
            cancel=true;
            editPassword.setError(getResources().getString(R.string.password_required));
        }else if(!password.equals(confPassword)){
            focus=editPassword;
            cancel=true;
            editPassword.setError(getResources().getString(R.string.password_not_match));
        }

        if(TextUtils.isEmpty(mobile)){
            focus=editMobile;
            cancel=true;
            editMobile.setError(getResources().getString(R.string.mobile_required));
        }

        if(TextUtils.isEmpty(email)){
            focus=editEmail;
            cancel=true;
            editEmail.setError(getResources().getString(R.string.email_required));
        }

        if(TextUtils.isEmpty(fullName)){
            focus=editFullName;
            cancel=true;
            editFullName.setError(getResources().getString(R.string.full_name_required));
        }

        if(cancel){
            focus.requestFocus();
            return;
        }else {
            if(isChecked){
                if(ConnectionDetector.isNetworkAvailable(this)) {
                    progressDialog.setMessage(getResources().getString(R.string.creating_account));
                    showProgress(true);
                    volleyRequest();
                   /* new Thread(new Runnable() {
                        @Override
                        public void run() {
                            okHttpRequest();
                          //  multiPartRequest();
                        }
                    }).start();*/

                }else {
                    DialogAndToast.showDialog(getResources().getString(R.string.no_internet),this);
                }

            }else {
                DialogAndToast.showDialog(getResources().getString(R.string.accept_terms),this);
            }

        }
    }

    // {"user_email":"vivek@gmail.com","user_type":"Customer","mpassword":"1234","updated_by":"vivek","photo":"photo.jpg","action":"1","created_by":"vivek","username":"vivek"}
    public void volleyRequest(){
        Map<String,String> params=new HashMap<>();
        params.put("username",fullName);
        params.put("mobile",mobile);
       // params.put("username",email.split("@")[0]);
        params.put("user_email",email);
        params.put("mpassword",password);
        params.put("photo","photo.jpg");
        params.put("user_type","Customer");
       // params.put("address", "");
        params.put("created_by",fullName);
        params.put("updated_by",fullName);
        params.put("action","1");
        String url=getResources().getString(R.string.url)+"/registerCustomer";
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"registerCustomer");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            if(apiName.equals("registerCustomer")){
                Log.d("response ", response.toString());
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
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.commit();
                    DialogAndToast.showToast("Account created",RegisterActivity.this);
                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.commit();
                    DialogAndToast.showToast("Account created",RegisterActivity.this);
                }else {
                    DialogAndToast.showDialog(response.getString("message"),RegisterActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),RegisterActivity.this);
        }
    }

}
