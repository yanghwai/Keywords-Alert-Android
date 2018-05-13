package com.example.justforfun.keywordsalert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.justforfun.keywordsalert.models.Result;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class NotificationUtils {
    private Context context;
    private static final String NOTIFICATION_CHANNEL_ID="0309";

    public NotificationUtils(Context context){this.context = context;}

    public void sendNotification(ArrayList<Result> results){
        Intent resultIntent = new Intent(context, ResultsActivity.class);
        resultIntent.putParcelableArrayListExtra(ResultsActivity.KEY_RESULT,results);
        PendingIntent pIntent = PendingIntent.getActivity(context,0, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        mBuilder.setContentTitle("New updates").
                setContentText(results.size()+" new article(s) ")
                .setTicker("find new articles")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)         // Notification必须设置SmallIcon,  ContentTitle和ContentText.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_round))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setChannelId(NOTIFICATION_CHANNEL_ID);
        Notification not = mBuilder.build();




        try{
            sleep(5000);
        }catch (Exception e){

        }
        notificationManager.notify(9439, not);
    }
}
