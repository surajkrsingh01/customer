package com.shoppurs.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.shoppurs.R;
import com.shoppurs.models.DeliveryAddress;
import com.shoppurs.utilities.AppController;
import com.shoppurs.utilities.ConnectionDetector;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends NetworkBaseActivity{

    private EditText editTextMobileNumber,editTextPassword;
    private TextView textForgotPassword, text_mobile_icon, text_password_icon, text_sign_up_icon, text_forgot_password_icon;
    private Button btnLogin;
    private TextView text_sign_up, forgot_password;
    private String mobile,password, email;
    private ImageView  image_twitter, image_facebook, image_google;
    private com.google.android.gms.common.SignInButton loginGoogleButton;
    private int RC_SIGN_IN = 111;

    private CallbackManager callbackManager;
    private LoginButton loginFacebookButton;
    private AccessToken accessToken;
    private ProfileTracker profileTracker;
    private String facebookUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initGoogleLogin();

        editTextMobileNumber=(EditText)findViewById(R.id.edit_mobile);
        editTextPassword=(EditText)findViewById(R.id.edit_password);
        textForgotPassword=(TextView)findViewById(R.id.text_forgot_password);
        text_mobile_icon = findViewById(R.id.text_mobile_icon);
        text_mobile_icon.setTypeface(Utility.getSimpleLineIconsFont(this));
        text_password_icon = findViewById(R.id.text_password_icon);
        text_password_icon.setTypeface(Utility.getSimpleLineIconsFont(this));
        text_sign_up_icon =findViewById(R.id.text_sign_up_icon);
        text_sign_up_icon.setTypeface(Utility.getSimpleLineIconsFont(this));
        text_forgot_password_icon =findViewById(R.id.text_forgot_password_icon);
        text_forgot_password_icon.setTypeface(Utility.getSimpleLineIconsFont(this));
        loginFacebookButton =  findViewById(R.id.login_button);
        image_twitter =  findViewById(R.id.image_twitter);
        image_google =  findViewById(R.id.image_google);
        image_facebook = findViewById(R.id.image_facebook);
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

        text_sign_up= findViewById(R.id.text_sign_up);
        text_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        forgot_password = findViewById(R.id.text_forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
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


        loginFacebookButton.setReadPermissions(Arrays.asList("email","public_profile"));
        callbackManager = CallbackManager.Factory.create();
        loginFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken= loginResult.getAccessToken();
                facebookUserId =accessToken.getUserId();
                Log.i("Login","Permissions: "+accessToken.getPermissions().toString());
                if(Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.com_facebook_profilepictureview_preset_size_large);
                            Uri profilePictureUri= profile2.getProfilePictureUri(dimensionPixelSize , dimensionPixelSize);
                            editor.putString(Constants.USER_ID,profile2.getId());
                            editor.putString(Constants.FIRST_NAME,profile2.getFirstName());
                            editor.putString(Constants.LAST_NAME,profile2.getLastName());
                            editor.putString(Constants.USERNAME,profile2.getFirstName()+" "+profile2.getLastName());
                            editor.putString(Constants.PROFILE_PIC,profilePictureUri.toString());
                            editor.putBoolean(Constants.IS_LOGGED_IN,true);
                            editor.commit();
                            Log.d("Profile", profile2.getFirstName());
                            makeGraphRequest();
                            profileTracker.stopTracking();
                        }
                    };
                    // no need to call startTracking() on mProfileTracker
                    // because it is called by its constructor, internally.
                }
                else {
                    Profile profile = Profile.getCurrentProfile();
                    int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.com_facebook_profilepictureview_preset_size_large);
                    Uri profilePictureUri= profile.getProfilePictureUri(dimensionPixelSize , dimensionPixelSize);
                    editor.putString(Constants.USER_ID,profile.getId());
                    editor.putString(Constants.FIRST_NAME,profile.getFirstName());
                    editor.putString(Constants.LAST_NAME,profile.getLastName());
                    editor.putString(Constants.USERNAME,profile.getFirstName()+" "+profile.getLastName());
                    editor.putString(Constants.PROFILE_PIC,profilePictureUri.toString());
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.commit();
                    Log.d("Profile", profile.getFirstName());
                    makeGraphRequest();
                }

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.i("Login","Error "+error.toString());
            }
        });

        image_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        image_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gogleLogin();
            }
        });

        image_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFacebookButton.performClick();
            }
        });

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
                loginRequest();
            }else {
                DialogAndToast.showDialog(getResources().getString(R.string.no_internet),this);
            }

        }
    }

    private void loginRequest(){
        Map<String,String> params=new HashMap<>();
        params.put("mobile",mobile);
        params.put("password",password);
        //String url=getResources().getString(R.string.url)+"/loginCustomer";
        String url=getResources().getString(R.string.url_web)+"/useradmin/customers/loginCustomer";
        showProgress(true);
        jsonObjectApiRequestForLoginReg(Request.Method.POST,url,new JSONObject(params),"loginCustomer");
    }

    public void registerRequest(String email, String fullName, String photoUrl, String password){
        Map<String,String> params=new HashMap<>();
        params.put("username",fullName);
        params.put("mobile",mobile);
        // params.put("username",email.split("@")[0]);
        params.put("user_email",email);
        params.put("mpassword",password);
        params.put("photo", photoUrl);
        params.put("user_type","Customer");
        // params.put("address", "");
        params.put("created_by",fullName);
        params.put("updated_by",fullName);
        params.put("action","1");
        String url=getResources().getString(R.string.url)+"/registerCustomer";
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"registerCustomer");
    }

    private void getFavoriteStore(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));

        String url=getResources().getString(R.string.url)+"/shop/favourite_shops";
        Log.i(TAG,params.toString());
        //showProgress(true);
        jsonObjectFavShopApiRequest(Request.Method.POST,url,new JSONObject(params),"getfavoriteshop");
    }

    private void getDeliveryAddress(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));

        String url=getResources().getString(R.string.url)+"/get_delivery_address";
        Log.i(TAG,params.toString());
        //showProgress(true);
        jsonObjectFavShopApiRequest(Request.Method.POST,url,new JSONObject(params),"getDeliveryAddress");
    }

    public void jsonObjectFavShopApiRequest(int method,String url, JSONObject jsonObject, final String apiName){

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
        }) {
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


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        if(apiName.equals("getDeliveryAddress"))
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if (apiName.equals("loginCustomer")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    JSONObject dataObject = response.getJSONObject("result");

                    editor.putString(Constants.JWT_TOKEN, dataObject.getString("token"));
                    editor.putString(Constants.GOOGLE_MAP_API_KEY,dataObject.getString("googleMapApiKey"));
                    editor.putString(Constants.FULL_NAME, dataObject.getString("username"));
                    editor.putString(Constants.USER_ID, dataObject.getString("userCode"));
                    editor.putString(Constants.PROFILE_PIC, dataObject.getString("photo"));
                    editor.putString(Constants.CUST_ADDRESS, dataObject.getString("address"));
                    editor.putString(Constants.CUST_LOCALITY, dataObject.getString("locality"));
                    editor.putString(Constants.CUST_PINCODE, dataObject.getString("zip"));
                    editor.putString(Constants.CUST_CITY, dataObject.getString("city"));
                    editor.putString(Constants.CUST_STATE, dataObject.getString("province"));
                    editor.putString(Constants.CUST_COUNTRY, dataObject.getString("country"));
                    editor.putString(Constants.CUST_LAT, dataObject.getString("userLat"));
                    editor.putString(Constants.CUST_LONG, dataObject.getString("userLong"));
                    editor.putString(Constants.EMAIL, dataObject.getString("user_email"));
                    editor.putString(Constants.MOBILE_NO, dataObject.getString("mobile"));
                    editor.putString(Constants.DB_NAME, dataObject.getString("dbname"));
                    editor.putString(Constants.DB_USER_NAME, dataObject.getString("dbusername"));
                    editor.putString(Constants.DB_PASSWORD, dataObject.getString("dbpassword"));
                    //editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    editor.commit();
                    //DialogAndToast.showToast("Logged In",LoginActivity.this);
                    getFavoriteStore();
                } else {
                    DialogAndToast.showDialog(response.getString("message"), LoginActivity.this);
                }
            } else if (apiName.equals("getfavoriteshop")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    Log.d(TAG, response.toString());
                    List<String> mFavoriteList = new ArrayList();
                    dbHelper.deleteAllFavorite();
                    if(!response.getString("result").equals("null")){
                    JSONArray jsonArray = response.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mFavoriteList.add(jsonArray.getJSONObject(i).getString("" + i));
                        dbHelper.add_to_Favorite(jsonArray.getJSONObject(i).getString("" + i));
                    }
                }
                    getDeliveryAddress();
                }
            } else if (apiName.equals("registerCustomer")) {
                Log.d("response ", response.toString());
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    JSONObject dataObject = response.getJSONObject("result");

                    editor.putString(Constants.FULL_NAME, dataObject.getString("username"));
                    editor.putString(Constants.USER_ID, dataObject.getString("userid"));
                    editor.putString(Constants.EMAIL, dataObject.getString("user_email"));
                    editor.putString(Constants.MOBILE_NO, dataObject.getString("mobile"));
                    editor.putString(Constants.DB_NAME, dataObject.getString("dbname"));
                    editor.putString(Constants.DB_USER_NAME, dataObject.getString("dbusername"));
                    editor.putString(Constants.DB_PASSWORD, dataObject.getString("dbpassword"));
                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    editor.commit();
                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    editor.commit();
                    DialogAndToast.showToast("Account created", LoginActivity.this);
                    Intent intent = new Intent(LoginActivity.this, StoresListActivity.class);
                    startActivity(intent);
                    finish();

                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    editor.commit();
                    DialogAndToast.showToast("Account created", LoginActivity.this);
                } else {

                   // DialogAndToast.showDialog(response.getString("message"), LoginActivity.this);
                }

            }else if (apiName.equals("getDeliveryAddress")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    Log.d(TAG, response.toString());
                    List<DeliveryAddress> deliveryAddressList = new ArrayList<>();
                    DeliveryAddress item;
                    dbHelper.deleteAllDeliveryAddress();

                    if(!response.getString("result").equals("null")){
                        JSONArray jsonArray = response.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            item = new DeliveryAddress();
                            item.setId(jsonArray.getJSONObject(i).getString("id"));
                            item.setName(jsonArray.getJSONObject(i).getString("name"));
                            item.setMobile(jsonArray.getJSONObject(i).getString("mobile"));
                            item.setAddress(jsonArray.getJSONObject(i).getString("address"));
                            if(jsonArray.getJSONObject(i).getInt("isDefault")==1)
                            item.setIsDefaultAddress("Yes");
                            else item.setIsDefaultAddress("No");
                            item.setDelivery_lat(jsonArray.getJSONObject(i).getString("userLat"));
                            item.setDelivery_long(jsonArray.getJSONObject(i).getString("userLong"));
                            item.setLandmark(jsonArray.getJSONObject(i).getString("locality"));
                            item.setCity(jsonArray.getJSONObject(i).getString("city"));
                            item.setState(jsonArray.getJSONObject(i).getString("province"));
                            item.setPinCode(jsonArray.getJSONObject(i).getString("zip"));
                            item.setCountry(jsonArray.getJSONObject(i).getString("country"));
                            deliveryAddressList.add(item);
                        }
                        if(deliveryAddressList.size()>0){
                            dbHelper.addAllDeliveryAddress(deliveryAddressList, sharedPreferences.getString(Constants.USER_ID, ""));
                        }
                    }
                    Intent intent = new Intent(LoginActivity.this, StoresListActivity.class);
                    startActivity(intent);
                    finish();
                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                    editor.commit();
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),LoginActivity.this);
        }
    }


    GoogleSignInClient mGoogleSignInClient;
    private void initGoogleLogin(){
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void gogleLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account){
        Log.d("account ", account+"");
        if(account!=null) {
            registerRequest(account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString(), "123456");
            Log.d("account ", account.getDisplayName() + " " + account.getEmail() + " " + account.getPhotoUrl());
            // startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }


    private void makeGraphRequest(){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            email = object.getString("email");
                            password = "123456";
                            String fullname = sharedPreferences.getString(Constants.USERNAME, "");
                            String photoUrl = sharedPreferences.getString(Constants.PROFILE_PIC, "");
                            registerRequest(email, fullname, photoUrl, password);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Registration","Login "+object.toString());

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
