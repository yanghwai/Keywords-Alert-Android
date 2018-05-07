package com.example.justforfun.keywordsalert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.justforfun.keywordsalert.models.Result;

import java.util.ArrayList;

public class NotificationUtils {
    private Context context;

    public NotificationUtils(Context context){this.context = context;}

    public void sendNotification(ArrayList<Result> results){
        Intent resultIntent = new Intent(context, ResultsActivity.class);
        resultIntent.putParcelableArrayListExtra(ResultsActivity.KEY_RESULT,results);
        PendingIntent pIntent = PendingIntent.getActivity(context,0, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setContentTitle("New updates").
                setContentText(results.size()+" new article(s) ")
                .setTicker("find new articles")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String NOTIFICATION_CHANNEL_ID = "114514";
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("My NotificationUtil channel 1");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);

        }*/

        notificationManager.notify(9439, mBuilder.build());
    }
}
