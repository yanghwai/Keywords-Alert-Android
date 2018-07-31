package com.example.justforfun.keywordsalert;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justforfun.keywordsalert.models.Result;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private ArrayList<TextView> keywordsView;
    private ArrayList<TextView> websitesView;
    public HashSet<String> keywords;
    public HashSet<String> websites;
    public String email;
    public static int checkInterval;
    private EditText emailText;
    public boolean notByEmail, notByNot;
    ArrayList<Result> res;
    public int newResNum;
    private MyService myService;
    private ServiceConnection serviceConnection;
    private OnTimerServiceListener onTimerServiceListener = new OnTimerServiceListener() {
        @Override
        public void getData() {
            new Thread(runnable).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupKeywords();
        setupWebsites();
        setupAlert();
        setupButtons();
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
            //View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.website_item,null);
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
                emailText = view.findViewById(R.id.email);
                notByEmail = true;
            }else{
                emailContainer.removeAllViews();
                emailText = null;
                notByEmail = false;
            }
        });
        checkNotification.setOnCheckedChangeListener((compoundButton, b) -> {
            if(checkNotification.isChecked()){
                notByNot = true;
            }else{
                notByNot = false;
            }
        });
    }

    private void setupButtons(){
        Button setAlert = findViewById(R.id.setAlert);
        final Button results = findViewById(R.id.results);
        FloatingActionButton fab = findViewById(R.id.feedback);

        setAlert.setOnClickListener(view -> {
            keywords = new HashSet<>();
            websites = new HashSet<>();
            checkInterval = 0;
            String checkInput = ((EditText)findViewById(R.id.checking_interval)).getText().toString();


            if(!checkInput.equals("")){// set checking inteval
                checkInterval = Integer.parseInt(checkInput);
            }

            // set keywords
            for(TextView keyword:keywordsView){
                if(!keyword.getText().toString().equals(""))
                    keywords.add(keyword.getText().toString());
            }

            // set websites
            for(TextView website:websitesView){
                if(!website.getText().toString().equals(""))
                    websites.add(website.getText().toString());
            }

            // set alert method
            if(emailText != null){
                email = emailText.getText().toString();
            }else{
                email = null;
            }

            if(keywords.size() == 0){
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage(getString(R.string.alert_keyword))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }else if(websites.size() == 0) {
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage(getString(R.string.alert_website))
                        .setPositiveButton("Ok", (dialog, which) -> {
                        }).show();
            }else if(emailText !=null && !isValidEmail(email)){
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage(getString(R.string.alert_email))
                        .setPositiveButton("Ok", (dialog, which) -> {
                        }).show();
            }
            else if(checkInterval == 0){
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                        .setMessage(getString(R.string.alert_interval))
                        .setPositiveButton("Ok", (dialog, which) -> {
                        }).show();
            }
            else {
                Toast.makeText(MainActivity.this,getString(R.string.start_search),Toast.LENGTH_LONG).show();
                res = new ArrayList<>();
                //res.add(new Result("Optical Fiber Communication (OFC)","https://www.osapublishing.org/conference.cfm?meetingid=5"));
                //parseAndGetData();
                serviceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        myService = ((MyService.TimerBinder) service).getService();
                        myService.setOnTimerServiceListener(onTimerServiceListener);
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }
                };

                bindService(new Intent(MainActivity.this,MyService.class),serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });

        results.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
            if(res == null){
                intent.putParcelableArrayListExtra(ResultsActivity.KEY_RESULT, new ArrayList<Result>());
            }else {
                intent.putParcelableArrayListExtra(ResultsActivity.KEY_RESULT, res);
            }
            startActivity(intent);
        });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });
    }

    public static int getCheckInterval(){
        return checkInterval;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            WebsiteSearchUtil wsu = new WebsiteSearchUtil();
            newResNum = wsu.updateResults(res,keywords,websites);
            if(newResNum != 0){
                if(notByNot){
                    NotificationUtils notif = new NotificationUtils(MainActivity.this);
                    notif.sendNotification(res,newResNum);
                }
                if(notByEmail){
                    try{
                    EmailUtils emu = new EmailUtils("keywords.alert.new@gmail.com","alert6666");
                    StringBuilder body=new StringBuilder();
                    for(int i=0; i<res.size(); i++){
                        body.append(res.get(i).title+"\n");
                        body.append(res.get(i).webLink+"\n\n");
                    }
                    body.append("Best regards,\n");
                    body.append("Keywords Alert");
                    emu.sendMail("keywords alert: "+newResNum+" new articles are found!",body.toString(),"keywords.alert.new@gmail.com",email);
                    }catch(Exception e){
                        Log.e("SendMail",e.getMessage(),e);
                    }
                }
            }
        }
    };

    private boolean isValidEmail(String emailAddr) {
        final String emailPattern="^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(emailPattern).matcher(emailAddr).matches();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(serviceConnection!=null)
            unbindService(serviceConnection);
        myService = null;
    }
}