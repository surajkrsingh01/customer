package com.shoppurs.activities;


import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.shoppurs.R;
import com.shoppurs.services.NotificationService;
import com.shoppurs.utilities.AppController;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ForgotPasswordActivity extends NetworkBaseActivity {

    private EditText editMobile;
    private String myNumber="";
    //These are the objects needed
    //It is the verification id that will be sent to the user
    private String mVerificationId;

    //The edittext to input the code
    private EditText editTextCode;
    private String OTP ="";
    private boolean isOtpGenerated;
    private Button submitButton;

    //firebase auth object
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editMobile = findViewById(R.id.edit_mobile_no);
        editTextCode = findViewById(R.id.editTextCode);
        /*if(TextUtils.isEmpty(sharedPreferences.getString(Constants.OTP,"")))
            editTextCode.setVisibility(View.GONE);
        else editTextCode.setVisibility(View.VISIBLE);*/
        submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(myNumber.isEmpty()){
                    myNumber = editMobile.getText().toString();
                    OTP = editTextCode.getText().toString();
                    Log.d("mobile ", myNumber);
                    if (TextUtils.isEmpty(myNumber)) {
                        Log.d("mobile ", myNumber);
                        DialogAndToast.showDialog("Please Enter Your Mobile Number", ForgotPasswordActivity.this);
                    } else {
                        if (myNumber.length() != 10) {
                            DialogAndToast.showDialog("Please Enter Valid Mobile Number", ForgotPasswordActivity.this);
                        } else {
                            validate_OTP(myNumber, OTP);
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

    private void initFirebaseOtp(String mobile){
        mAuth = FirebaseAuth.getInstance();
        sendVerificationCode(mobile);
    }

    private void sendVerificationCode(String mobile) {
        Log.d(TAG, "getGoogleApiForMethod called "+mobile);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            OTP = phoneAuthCredential.getSmsCode();
            Log.d(TAG, "onVerificationCompleted called "+OTP);

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (OTP != null) {
                editTextCode.setText(OTP);
                //verifying the code
                verifyVerificationCode(OTP);
            }else{
                //showMyDialog("Unable to retrieve otp, please try again");
                //Instant verification: in some cases the phone number can be instantly verified without needing
                //to send or enter a verification code.

                Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                intent.putExtra("flag", "ForgotPassword");
                intent.putExtra("mobile", myNumber);
                startActivity(intent);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
            Log.d(TAG, "onCodeSent called "+mVerificationId);
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            editor.putString(Constants.OTP, OTP);
                            editor.commit();
                            save_OTP(myNumber, OTP);
                            //DialogAndToast.showDialog("verification successful", ForgotPasswordActivity.this);

                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "OTP varification failed, please try again.";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "OTP invalid please try again...";
                            }
                            DialogAndToast.showDialog(message, ForgotPasswordActivity.this);
                        }
                    }
                });
    }

    private void validate_OTP(String mobile, String otp){
        Map<String,String> params=new HashMap<>();
        params.put("mobile", mobile);
        if(!isOtpGenerated)
        params.put("otp", "-1");
        else params.put("otp", otp);
        String url=getResources().getString(R.string.url_web)+"/useradmin/validate_otp";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"validate_otp");
    }

    private void save_OTP(String mobile, String otp){
        Map<String,String> params=new HashMap<>();
        params.put("mobile", mobile);
        if(TextUtils.isEmpty(otp))
            params.put("otp", "-1");
        else params.put("otp", otp);
        String url=getResources().getString(R.string.url_web)+"/useradmin/save_otp";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"save_otp");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
            showProgress(false);
        try {
            Log.d("response", response.toString());
            if (apiName.equals("validate_otp")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                   if(response.getInt("result")==1) {
                       Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                       intent.putExtra("flag", "ForgotPassword");
                       intent.putExtra("mobile", myNumber);
                       startActivity(intent);
                   }else {
                       isOtpGenerated = true;
                       editTextCode.setVisibility(View.VISIBLE);
                       submitButton.setText("Submit");
                       editTextCode.setText(response.getString("result"));
                       DialogAndToast.showDialog("Otp has been sent to your mobile, please check your notification", ForgotPasswordActivity.this);
                       NotificationService.displayNotification(ForgotPasswordActivity.this, this, response.getInt("result")+" is your verification code");
                   }
                }else {
                    if(response.getInt("result")==0) {
                        editTextCode.setVisibility(View.VISIBLE);
                        initFirebaseOtp(myNumber);
                    }else if(response.getInt("result")==1) {
                        DialogAndToast.showDialog(response.getString("message"), ForgotPasswordActivity.this);
                    }else if(response.getInt("result")==2) {
                        DialogAndToast.showDialog(response.getString("message"), ForgotPasswordActivity.this);
                    }
                }
            }else if(apiName.equals("save_otp")){
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);
                    intent.putExtra("flag", "ForgotPassword");
                    intent.putExtra("mobile", myNumber);
                    startActivity(intent);
                } else {
                    DialogAndToast.showDialog(response.getString("message"), ForgotPasswordActivity.this);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ForgotPasswordActivity.this);
        }
    }

    public void onDialogPositiveClicked(){
        initFirebaseOtp(myNumber);
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
