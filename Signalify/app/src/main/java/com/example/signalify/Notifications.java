package com.example.signalify;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class Notifications extends Application {
    public static final String CHANNEL_1_ID="channel_low";
    public static final String CHANNEL_3_ID="channel_high";
    private static NotificationManager notificationManager;


    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel3=new NotificationChannel(CHANNEL_3_ID,"channel3",NotificationManager.IMPORTANCE_HIGH);
            channel3.setDescription("Notifications avec images");
            NotificationChannel channel1=new NotificationChannel(CHANNEL_1_ID,"channel1",NotificationManager.IMPORTANCE_LOW);
            channel1.setDescription("Notificatios simples");
            notificationManager =getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel1);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel3);

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
}
