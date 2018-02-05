package com.example.justforfun.keywordsalert;

/**
 * Created by Jinghao Qiao on 2018/1/29.
 */

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.support.v4.app.NotificationCompat;


public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private static final int NOTIFICATION_ID=1;
    private static final String NOTIFICATION_CHANNEL_ID= "my_notify_channel";
    private ArrayList<String> keywords;
    private ArrayList<String> websites;
    private String email;
    private HashMap<String,String> result= new HashMap<>();
    public HashMap<String,String> oldDict= new HashMap<>();
    public HashMap<String,String> newDict= new HashMap<>();
    public HashMap<String,String> updatesDict= new HashMap<>();
    private MyService timerService;
    private static int checkInterval;

    public static int getInterval(){
        return checkInterval;
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            timerService = ((MyService.TimerBinder) iBinder).getService();
            timerService.setOnTimerServiceListener(onTimerServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

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
        mContext= MainActivity.this;

        keywords = new ArrayList<>();
        websites = new ArrayList<>();

        setupKeywordUI();
        setupWebsitesUI();
        setupEmailUI();
        setupButtonsUI();

    }
    private void setupTimer(){
        checkInterval = Integer.parseInt(((EditText)findViewById(R.id.checking_interval)).getText().toString());
    }

    private void showResult()
    {
        if(result.size()<=50)
        {
            for(Map.Entry<String, String> entry: updatesDict.entrySet())
            {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        else
            result = updatesDict;
    }

    Runnable runnable= new Runnable() {
        @Override
        public void run() {
            WebsiteSearch wbs= new WebsiteSearch();
            ArrayList<HashMap<String,String>> newAndUpdates= wbs.updateAlert(oldDict, keywords, websites,email!=null, true, email);
            newDict=newAndUpdates.get(0);
            updatesDict= newAndUpdates.get(1);
            Log.i("get results", String.valueOf(updatesDict.size()));
            //TODO notification

            oldDict=newDict;

            handler.sendEmptyMessage(0);

        }
    };

    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            {
                showResult();
                CheckBox checkNotification= (CheckBox)findViewById(R.id.check_Notification);
                if( checkNotification.isChecked() &&  updatesDict.size()>0)
                {
                    sendNotification("New updates", String.valueOf(updatesDict.size())+" new article(s) found!");//"New article updates", String.valueOf(updatesDict.size())+" article(s) found.");
                }
            }

        }
    };

    private void sendNotification(String title, String content)
    {
        NotificationManager mNotifyMgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("My Notification channel 1");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            mNotifyMgr.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round))
                        .setContentTitle(title)
                        .setContentText(content)
                        .setWhen(System.currentTimeMillis())
                        .setTicker("I am a Notification")
                        .setDefaults(Notification.DEFAULT_ALL);


        Intent resultIntent= new Intent(mContext, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent= PendingIntent.getActivity(mContext,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void setupKeywordUI(){
        final EditText editKeyword = (EditText) findViewById(R.id.keyword);
        ImageButton addKeyword = (ImageButton) findViewById(R.id.add_keyword);
        final LinearLayout keywordContainer = (LinearLayout) findViewById(R.id.keywords_container);

        keywords.add(editKeyword.getText().toString());


        editKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keywords.remove(editKeyword.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                keywords.add(0,editKeyword.getText().toString());
            }
        });

        addKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create a view
                final View v = getLayoutInflater().inflate(R.layout.keyword_item, null);
                keywordContainer.addView(v);
                final EditText keyword = v.findViewById(R.id.keyword_edit);

                keywords.add(keyword.getText().toString());
                keyword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        keywords.remove(keyword.getText().toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        keywords.add(keyword.getText().toString());
                    }
                });

                v.findViewById(R.id.remove_keyword).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        keywordContainer.removeView(v);
                        EditText keyword = v.findViewById(R.id.keyword_edit);

                        keywords.remove(keyword.getText().toString());
                    }
                });
            }
        });
    }

    private void setupWebsitesUI(){
        final EditText editWebsite = (EditText) findViewById(R.id.website);
        ImageButton addWebsite = (ImageButton) findViewById(R.id.add_website);
        final LinearLayout websiteContainer = (LinearLayout) findViewById(R.id.website_container);


        websites.add(editWebsite.getText().toString());


        editWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                websites.remove(editWebsite.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                websites.add(0, editWebsite.getText().toString());
            }
        });

        addWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = getLayoutInflater().inflate(R.layout.website_item,null);
                websiteContainer.addView(v);
                final EditText website = v.findViewById(R.id.website_edit);
                websites.add(website.getText().toString());

                website.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        websites.remove(website.getText().toString());
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        websites.add(website.getText().toString());
                    }
                });

                v.findViewById(R.id.remove_website).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        websiteContainer.removeView(v);
                        EditText website = v.findViewById(R.id.website_edit);

                        websites.remove(website.getText().toString());
                    }
                });
            }
        });
    }

    private void setupEmailUI(){
        final CheckBox checkEmail = (CheckBox)findViewById(R.id.check_Email);
        CheckBox checkNotification = (CheckBox)findViewById(R.id.check_Notification);
        final LinearLayout emailContainer = (LinearLayout) findViewById(R.id.email_container);

        checkEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkEmail.isChecked()){
                    View view = getLayoutInflater().inflate(R.layout.email_item,null);
                    emailContainer.addView(view);
                    final EditText emailEdit = (EditText) findViewById(R.id.email);
                    emailEdit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            email = emailEdit.getText().toString();
                        }
                    });
                }else if(!checkEmail.isChecked()){
                    emailContainer.removeAllViews();
                    email = null;
                }
            }
        });
    }

    private void setupButtonsUI(){
        Button setAlert = (Button) findViewById(R.id.setAlert);
        Button results = (Button) findViewById(R.id.results);
        //TODO Set Alert call
        setAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String timeStr = ((EditText)findViewById(R.id.checking_interval)).getText().toString();

                if( timeStr.length()==0 || Integer.parseInt(timeStr)==0 ){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("warning")
                            .setMessage("Please set a proper time")
                            .setPositiveButton("ok",null)
                            .show();
                }
                else {
                    setupTimer();
                    // TODO start new thread
                    Log.i("size of websites", String.valueOf(websites.size()));
                    Toast.makeText(MainActivity.this, "Alert service is on", Toast.LENGTH_LONG).show();
                    bindService(new Intent(MainActivity.this, MyService.class), serviceConnection, Context.BIND_AUTO_CREATE);
                }
            }
        });

        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Results.class);

                final SerializableMap Mymap = new SerializableMap();
                Mymap.setMap(result);
                Bundle bundle = new Bundle();
                bundle.putSerializable("map",Mymap);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        timerService = null;
    }
}
