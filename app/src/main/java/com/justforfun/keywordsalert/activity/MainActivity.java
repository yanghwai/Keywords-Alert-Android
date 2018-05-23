package com.justforfun.keywordsalert.activity;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justforfun.keywordsalert.R;
import com.justforfun.keywordsalert.entity.Result;
import com.justforfun.keywordsalert.entity.Setting;
import com.justforfun.keywordsalert.service.JobSchedulerService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static Setting setting;

    private List<TextView> keywordsView;
    private List<TextView> websitesView;
    private TextView emailTextView;

    private static Set<Result> oldResult;
    private static Set<Result> newResult;
    private static Set<Result> diffResult;


    // setters and getters

    public static Set<Result> getOldResult() {
        return oldResult;
    }

    public static void setOldResult(Set<Result> oldResult) {
        MainActivity.oldResult = oldResult;
    }

    public static Set<Result> getNewResult() {
        return newResult;
    }

    public static void setNewResult(Set<Result> newResult) {
        MainActivity.newResult = newResult;
    }

    public static Set<Result> getDiffResult() {
        return diffResult;
    }

    public static void setDiffResult(Set<Result> diffResult) {
        MainActivity.diffResult = diffResult;
    }

    public static Setting getSetting() {
        return setting;
    }

    public static void setSetting(Setting setting) {
        MainActivity.setting = setting;
    }


    // override Activity methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupKeywords();
        setupWebsites();
        setupAlert();

        oldResult = new HashSet<>();
        newResult = new HashSet<>();
        diffResult = new HashSet<>();
        setting = new Setting();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    private void setupKeywords(){
        ImageButton addKeyword = findViewById(R.id.add_keyword);
        TextView firstKeyword = findViewById(R.id.input_keyword);
        final LinearLayout keywordContainer = findViewById(R.id.keywords_container);

        keywordsView = new ArrayList<>();
        keywordsView.add(firstKeyword);
        addKeyword.setOnClickListener(view -> {
            final View v = getLayoutInflater().inflate(R.layout.keyword_item,null);
            keywordContainer.addView(v);

            final EditText keyword = v.findViewById(R.id.keyword_edit);
            ImageButton removeKeyWord = v.findViewById(R.id.remove_keyword);

            keywordsView.add(keyword);
            removeKeyWord.setOnClickListener(view1 -> {
                keywordContainer.removeView(v);
                keywordsView.remove(keyword);
            });
        });
    }

    private void setupWebsites(){
        TextView firstWebsite = findViewById(R.id.input_website);
        ImageButton addWebsite = findViewById(R.id.add_website);
        final LinearLayout websiteContainer = findViewById(R.id.websites_container);

        websitesView = new ArrayList<>();
        websitesView.add(firstWebsite);
        addWebsite.setOnClickListener(view -> {
            final View v = getLayoutInflater().inflate(R.layout.website_item,null);
            websiteContainer.addView(v);

            final EditText website = v.findViewById(R.id.website_edit);
            ImageButton removeWebsite = v.findViewById(R.id.remove_website);

            websitesView.add(website);
            removeWebsite.setOnClickListener(view1 -> {
                websitesView.remove(website);
                websiteContainer.removeView(v);
            });
        });
    }

    private void setupAlert(){
        final CheckBox checkEmail = findViewById(R.id.check_Email);
        final CheckBox checkNotification = findViewById(R.id.check_Notification);
        final LinearLayout emailContainer = findViewById(R.id.email_container);

        checkEmail.setOnCheckedChangeListener((compoundButton, b) -> {
            if(checkEmail.isChecked()){
                View view = getLayoutInflater().inflate(R.layout.email_item,null);
                emailContainer.addView(view);
                emailTextView = view.findViewById(R.id.email);
                setting.setEmailOption(true);
            }else{
                emailContainer.removeAllViews();
                emailTextView = null;
                setting.setEmailOption(false);
            }
        });
        checkNotification.setOnCheckedChangeListener((compoundButton, b) -> {
            if(checkNotification.isChecked()){
                setting.setPushOption(true);
            }else{
                setting.setPushOption(false);
            }
        });
    }


    // save user settings, true if settings are valid
    private boolean saveSetting(){
        boolean flag = false;
        Set<String> keywords = new HashSet<>();
        Set<String> websites = new HashSet<>();

        // set Timer
        boolean setTimerResult = setTimer();

        // set keywords
        for(TextView keyword:keywordsView){
            if(!keyword.getText().toString().equals(""))
                keywords.add(keyword.getText().toString());
        }
        setting.setKeywords(keywords);

        // set websites
        for(TextView website:websitesView){
            if(!website.getText().toString().equals(""))
                websites.add(website.getText().toString());
        }
        setting.setWebsites(websites);


        // set alert method
        if(emailTextView != null && !emailTextView.getText().toString().equals("")){
            setting.setEmail(emailTextView.getText().toString());
        }else{
            setting.setEmail(null);
        }

        if(keywords.size() == 0){
            new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.alert_keyword))
                    .setPositiveButton("Ok", (dialog, which) -> {
                    }).show();
        }else if(websites.size() == 0) {
            new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.alert_website))
                    .setPositiveButton("Ok", (dialog, which) -> {
                    }).show();
        }else if(emailTextView !=null && !isValidEmail(setting.getEmail())){
            new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.alert_email))
                    .setPositiveButton("Ok", (dialog, which) -> {
                    }).show();
        }
        else if(!setTimerResult){
            new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage(getString(R.string.alert_interval))
                    .setPositiveButton("Ok", (dialog, which) -> {
                    }).show();
        }
        else{
            flag=true;
        }

        return flag;
    }


    // "Set Alert" Button
    public void onClickStartButton(View view){
        if(!saveSetting())
            return;  // exit if settings are invalid
        Log.d(TAG,">>>Settings are: "+ setting);
        cancelJob();
        Log.d(TAG, ">>Main: Setting up scheduler");
        scheduleJob();
        Toast.makeText(MainActivity.this, "Alert service is on", Toast.LENGTH_LONG).show();
    }

    // "Result" Button
    public void onClickResultButton(View view){
        ArrayList<Result> resultList;
        resultList = new ArrayList<>(diffResult);

        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        intent.putParcelableArrayListExtra(ResultsActivity.KEY_RESULT, resultList);

        startActivity(intent);
    }

    // helper methods

    private void scheduleJob(){
        ComponentName componentName = new ComponentName(this, JobSchedulerService.class);
        JobInfo info = new JobInfo.Builder(100, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(setting.getTimerMillisecond())
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS)
            Log.d(TAG, "Job scheduled");
        else
            Log.d(TAG, "Job scheduling failed");
    }

    private void cancelJob(){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            scheduler.cancel(123);
            Log.d(TAG, "Job cancelled");
        }
    }

    // validate email address using regex
    private boolean isValidEmail(String email) {
        final String emailPattern="^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    // validate timer settings and set settings.timerMilliseconds
    private boolean setTimer(){
        boolean setResult;
        String timerStr = ((EditText)findViewById(R.id.checking_interval)).getText().toString();
        if( timerStr.length()==0 || Integer.parseInt(timerStr)==0 || Integer.parseInt(timerStr)<15){
            setResult = false;
        }

        else{
            setting.setTimerMillisecond(Integer.parseInt(timerStr) * 60 * 1000);
            setResult = true;
        }

        return setResult;
    }

}
