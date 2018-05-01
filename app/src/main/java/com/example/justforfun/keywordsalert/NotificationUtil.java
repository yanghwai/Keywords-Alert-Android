package com.example.justforfun.keywordsalert;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.Map;


public class NotificationUtil {
    private Context context;
    private static Map<String, String> content;
    private static int NOTIFICATION_ID=1;
    private static final String NOTIFICATION_CHANNEL_ID="114514";

    NotificationUtil(Context context){
        this.context=context;
    }

    void sendNotification(boolean emailOption, boolean pushOption, String email, Map<String, String> content) {
        NotificationUtil.content = content;
        if(content.size()==0)
            return;

        if(emailOption && email!=null)
            sendEmailNotification(email);
        if(pushOption)
            sendPushNotification("New updates", String.valueOf(content.size()) + " new article(s) found!");
    }

    private void sendEmailNotification(String email){
        GMailSender sender= new GMailSender("lithiumHack2018", "hack6666");
        StringBuilder msgStr=new StringBuilder();
        Integer index=1;

        for( Map.Entry<String, String> entry: content.entrySet()) {
            msgStr.append(index).append(". ").append(entry.getKey()).append("\n").append(entry.getValue()).append("\n\n");
            index++;
        }

        try {
            sender.sendMail("Topic updates", msgStr.toString(), "lithiumHack2018@gmail.com", email);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendPushNotification(String title, String body){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_round))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setWhen(System.currentTimeMillis())
                        .setTicker("I am a NotificationUtil")
                        .setDefaults(android.app.Notification.DEFAULT_ALL);

        NotificationManager mNotifyMgr= (NotificationManager) context.getSystemService(MainActivity.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("My NotificationUtil channel 1");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            if (mNotifyMgr != null) {
                mNotifyMgr.createNotificationChannel(notificationChannel);
            }
        }

        Intent resultIntent= new Intent(context, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent= PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        if (mNotifyMgr != null) {
            mNotifyMgr.notify(NOTIFICATION_ID++, mBuilder.build());
        }
    }

}
