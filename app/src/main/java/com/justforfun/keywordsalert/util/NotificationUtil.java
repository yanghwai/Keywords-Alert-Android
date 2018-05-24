package com.justforfun.keywordsalert.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.justforfun.keywordsalert.R;
import com.justforfun.keywordsalert.activity.MainActivity;
import com.justforfun.keywordsalert.activity.ResultsActivity;
import com.justforfun.keywordsalert.entity.Result;
import com.justforfun.keywordsalert.entity.Setting;

import java.util.ArrayList;
import java.util.Set;


public class NotificationUtil {
    private static final String TAG= NotificationUtil.class.getSimpleName();

    private Context context;
    private int NOTIFICATION_ID=1;
    private static final String NOTIFICATION_CHANNEL_ID="114514";


    private boolean emailOption;
    private boolean pushOption;
    private String email;
    private Set<Result> diffResults;

    // Synchronize settings and result set with MainActivity
    private void syncWithMain(){
        Setting setting = MainActivity.getSetting();
        emailOption = setting.isEmailOption();
        pushOption = setting.isPushOption();
        email = setting.getEmail();

        diffResults = MainActivity.getDiffResult();
    }

    public NotificationUtil(Context context){
        this.context=context;
    }

    public void sendNotification() {
        syncWithMain();

        Log.d(TAG,">>>NotificationUtil: sending notifications");
        Log.d(TAG, toString() );

        if(diffResults.size()==0)
            return;

        if(emailOption && email!=null)
            sendEmailNotification(email);
        if(pushOption)
            sendPushNotification("New updates", String.valueOf(diffResults.size()) + " new article(s) found!");
    }

    private void sendEmailNotification(String email){

        EmailUtil emailUtil = new EmailUtil("keywords.alert.new@gmail.com","alert6666");

        StringBuilder msgStr=new StringBuilder();
        Integer index=1;

        for( Result result: diffResults) {
            msgStr.append(index).append(". ").append(result.getTitle()).append("\n").append(result.getWebLink()).append("\n\n");
            index++;
        }

        try {
            emailUtil.sendMail("Keywords updates: "+diffResults.size()+" new articles found",
                    msgStr.toString(),
                    "keywords.alert.new@gmail.com",
                    email);
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
                        .setDefaults(android.app.Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true);

        NotificationManager mNotifyMgr= (NotificationManager) context.getSystemService(MainActivity.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("My NotificationUtil channel 1");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);


            if (mNotifyMgr != null) {
                mNotifyMgr.createNotificationChannel(notificationChannel);
            }
        }

        Intent resultIntent = new Intent(context, ResultsActivity.class);
        resultIntent.putParcelableArrayListExtra(ResultsActivity.KEY_RESULT,new ArrayList<>(diffResults));

        //Intent resultIntent= new Intent(context, MainActivity.class);
        //resultIntent.setAction(Intent.ACTION_MAIN);
        //resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent= PendingIntent.getActivity(context,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        if (mNotifyMgr != null) {
            mNotifyMgr.notify(NOTIFICATION_ID++, mBuilder.build());
        }
    }

    @Override
    public String toString() {
        return "NotificationUtil{" +
                "context=" + context +
                ", NOTIFICATION_ID=" + NOTIFICATION_ID +
                ", emailOption=" + emailOption +
                ", pushOption=" + pushOption +
                ", email='" + email + '\'' +
                ", diffResults=" + diffResults.size() +
                '}';
    }
}
