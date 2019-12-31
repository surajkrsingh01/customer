package com.shoppurs.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.ReturnProductsActivity;
import com.shoppurs.activities.Settings.ToDoListActivity;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends NetworkBaseActivity {

    private SharedPreferences sharedPreferences;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setIsSplash(true);
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.splash_layout);
        sharedPreferences=getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        if(sharedPreferences.getBoolean(Constants.IS_LOGGED_IN,false)){
            checkVersion();
        }else {
            intent=new Intent(SplashActivity.this,LoginActivity.class);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                    // overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
            }, 2000);
        }
    }

    private void checkVersion(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.url_customer)+"/api/db_version/get";
        Log.d(TAG, params.toString());
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"getVersions");
    }

    private void updateDb(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        params.put("code",sharedPreferences.getString(Constants.USER_ID, ""));
        params.put("userName",sharedPreferences.getString(Constants.USERNAME, ""));
        params.put("dbVersion",sharedPreferences.getString(Constants.DB_VERSION, ""));
        String url=getResources().getString(R.string.url_customer)+"/api/db_version/handle_change_version";
        Log.d(TAG, params.toString());
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"updateDbVersion");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("getVersions")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    JSONObject dataObject = response.getJSONObject("result");
                    if (!dataObject.equals("null")) {
                        String playStoreVersion = dataObject.getString("custAppVersion");
                        String dbVersion = dataObject.getString("dbVersion");
                        String previousDbVersion = sharedPreferences.getString(Constants.DB_VERSION, "");
                        PackageInfo pInfo = null;
                        String appCurrentversion = null;
                        try {
                            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            appCurrentversion = pInfo.versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        if(!TextUtils.isEmpty(appCurrentversion) && !TextUtils.isEmpty(playStoreVersion)) {
                            Log.d("appCurrentversion "+appCurrentversion, "playStoreVersion "+playStoreVersion);
                            if (!appCurrentversion.equals(playStoreVersion)) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.shoppurs"));
                                startActivity(intent);
                            }else if(!TextUtils.isEmpty(previousDbVersion) && !TextUtils.isEmpty(dbVersion)) {
                                Log.d("previousDbVersion "+previousDbVersion, "dbVersion "+dbVersion);
                                if (!previousDbVersion.equals(dbVersion)) {
                                    updateDb();
                                } else {
                                    intent = new Intent(SplashActivity.this, StoresListActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }else if(!TextUtils.isEmpty(previousDbVersion) && !TextUtils.isEmpty(dbVersion)) {
                            if(!previousDbVersion.equals(dbVersion)){
                                updateDb();
                            }else {
                                intent=new Intent(SplashActivity.this,StoresListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }


                    } else {
                        DialogAndToast.showDialog(response.getString("message"), SplashActivity.this);
                    }
                }
            }else if(apiName.equals("updateDbVersion")){
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    if (!response.getString("result").equals("null")) {
                        editor.putString(Constants.DB_VERSION, response.getString("result"));
                        editor.commit();
                        intent = new Intent(SplashActivity.this, StoresListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        DialogAndToast.showDialog(response.getString("message"), SplashActivity.this);
                    }
                }else{
                    intent = new Intent(SplashActivity.this, StoresListActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            //DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),SplashActivity.this);
        }
    }
}
