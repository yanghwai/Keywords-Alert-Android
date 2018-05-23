package com.justforfun.keywordsalert.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.justforfun.keywordsalert.activity.MainActivity;
import com.justforfun.keywordsalert.util.NotificationUtil;

public class JobSchedulerService extends JobService {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;
    private WebsiteSearch wbs = new WebsiteSearch();
    private NotificationUtil notificationUtil = new NotificationUtil(JobSchedulerService.this);

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Job started");
        checkUpdates(params);
        return true;  // if true, needs to manually tell system that job is finished
    }

    private void checkUpdates(final JobParameters parameters){
        Thread t = new Thread(() -> {
            if(jobCancelled)
                return;
            Log.d(TAG,"checking updates");
            wbs.updateResult();
            Log.d(TAG, "Diff Result size: "+ MainActivity.getDiffResult().size());
            notificationUtil.sendNotification();
            jobFinished(parameters, false);
        });

        t.start();
    }

    private void doBackgroundWork(final JobParameters params) {
        Thread t = new Thread(() -> {
            for (int i=0; i<10; i++){
                if(jobCancelled)
                    return;

                Log.d(TAG, "run: "+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG,"Job finished");
            jobFinished(params, false);
        });

        t.start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
//        mJobHandler.removeMessages(1);
        Log.d(TAG,"Job cancelled before completion");
        jobCancelled = true;
        return false;
    }
}
