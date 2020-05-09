package com.example.signalify.models;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.signalify.R;
import com.example.signalify.activities.AddAccidentActivity;

import java.util.Objects;

public class Notifications extends Application {
    public static final String CHANNEL_ID="channel_high";
    private static NotificationManager notificationManager;
    int id = 0;


    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel =new NotificationChannel(CHANNEL_ID,"channel",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Mon canal de notifications");
            notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void sendNotificationChannelNormal(String title, String message, String channelId, int priority) {
        NotificationCompat.Builder notification=new NotificationCompat.Builder(getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority);
        NotificationManagerCompat.from(this).notify(++id,notification.build());
    }

}
