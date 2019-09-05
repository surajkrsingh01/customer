package com.shoppurs.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.shoppurs.R;
import com.shoppurs.activities.ForgotPasswordActivity;
import com.shoppurs.activities.SplashActivity;


public class NotificationService {

// LogCat tag

    private static final String TAG = NotificationService.class.getSimpleName();
    private static String CHANNEL_ID = "notChannelShoppurs";

    // private constructor
    private NotificationService() {
        super();
    }


    @SuppressWarnings({ "deprecation" })
    public static void displayNotification(Context context, AppCompatActivity startActivity, String message) {
        Intent intent;
        // Create an explicit intent for an Activity in your app
       /* if(startActivity instanceof ForgotPasswordActivity)
            intent = new Intent(context, ForgotPasswordActivity.class);
        else*/
            intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels(context);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
       // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void setupChannels(Context context) {
        CharSequence channelName = context.getString(R.string.notifications_channel_name);
        String channelDescription = context.getString(R.string.notifications_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(adminChannel);
    }

}
