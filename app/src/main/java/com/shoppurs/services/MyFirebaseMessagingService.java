package com.shoppurs.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shoppurs.R;

/**
 * Created by ARIEON-7 on 30-06-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    private static final String TAG = "MyFirebaseMsgService";
    private String CHANNEL_ID = "shoppursChannel";

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

    @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Notification data payload: " + message);
            sendNotification(remoteMessage.getNotification().getBody());

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
