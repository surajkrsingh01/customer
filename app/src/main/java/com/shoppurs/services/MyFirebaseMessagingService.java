package com.shoppurs.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shoppurs.R;
import com.shoppurs.activities.BaseActivity;
import com.shoppurs.activities.MainActivity;
import com.shoppurs.activities.RegisterActivity;
import com.shoppurs.utilities.AppController;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ARIEON-7 on 30-06-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private String CHANNEL_ID = "shoppursChannel";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sharedPreferences = getSharedPreferences(Constants.MYPREFERENCEKEY, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.IS_TOKEN_SAVED, false);
        editor.putString(Constants.FCM_TOKEN, refreshedToken);
        editor.commit();
        if(sharedPreferences.getBoolean(Constants.IS_LOGGED_IN,false))
        saveToken(refreshedToken);
    }

    public void saveToken(String token){
        Map<String,String> params=new HashMap<>();
        params.put("mobile", sharedPreferences.getString(Constants.MOBILE_NO, ""));
        params.put("userType", "customer");
        params.put("token",token);
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbPassword", sharedPreferences.getString(Constants.DB_PASSWORD,""));
        params.put("dbUserName", sharedPreferences.getString(Constants.DB_USER_NAME,""));
        String url=getResources().getString(R.string.url_web)+"/api/user/save_fcm_token";
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"saveToken");
    }

    public void jsonObjectApiRequest(int method,String url, JSONObject jsonObject, final String apiName){
        Log.i(TAG,jsonObject.toString());

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(method,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,response.toString());
                //showProgress(false);
                onJsonObjectResponse(response,apiName);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,"Json Error "+error.toString());
                //showProgress(false);
                //onServerErrorResponse(error,apiName);
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

    public void onJsonObjectResponse(JSONObject response, String apiName) {
        //showProgress(false);
        try {
            if(apiName.equals("saveToken")){
                Log.d("response ", response.toString());
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    editor.putBoolean(Constants.IS_TOKEN_SAVED, true);
                    editor.commit();
                }else {
                    DialogAndToast.showDialog(response.getString("message"),getBaseContext());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),getBaseContext());
        }
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String message = remoteMessage.getData().toString();

            if(message.contains("=")){
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(message.split("=")[1]);
                NotificationService.displayNotification(this, jsonObject.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                NotificationService.displayNotification(this, message);
            }

            sendNotification(message);
        }
    }

    private void sendNotification(String message) {
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder builder = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            setupChannels(this,mNotificationManager);
            builder = new Notification.Builder(this, CHANNEL_ID);
        }
        else{
            builder = new Notification.Builder(this);
            //  Notification notification = builder.getNotification();
        }

        Uri sound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notification = builder.setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher).setSound(sound)
                .build();

        //  notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void setupChannels(Context context, NotificationManager notificationManager) {
        CharSequence channelName = context.getString(R.string.notifications_channel_name);
        String channelDescription = context.getString(R.string.notifications_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}
