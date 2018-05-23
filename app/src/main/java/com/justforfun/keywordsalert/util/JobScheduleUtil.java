package com.justforfun.keywordsalert.util;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;

import com.justforfun.keywordsalert.service.JobSchedulerService;

public class JobScheduleUtil {
    public static void scheduleJob(Context context){
        ComponentName componentName = new ComponentName(context, JobSchedulerService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, componentName);
    }
}
