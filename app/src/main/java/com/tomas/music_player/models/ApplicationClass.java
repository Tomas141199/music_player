package com.tomas.music_player.models;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {
    private static final String CHANNEL_ID_1 = "channel_1";
    public static final String CHANNEL_ID_2 = "channel_1";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_NEXT =   "actionnext";
    public static final String ACTION_PLAY = "actionplay";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel Channel1 = new NotificationChannel(CHANNEL_ID_1,
                    "channel (1)", NotificationManager.IMPORTANCE_HIGH);
            Channel1.setDescription("channel 1 Description");

            NotificationChannel Channel2 = new NotificationChannel(CHANNEL_ID_2,
                    "channel (2)", NotificationManager.IMPORTANCE_HIGH);
            Channel1.setDescription("channel 2 Description");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(Channel1);
            notificationManager.createNotificationChannel(Channel2);
        }
    }
}
