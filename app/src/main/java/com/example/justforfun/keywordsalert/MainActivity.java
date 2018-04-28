package com.example.justforfun.keywordsalert;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private static final int NOTIFICATION_ID=1;
    private static final String NOTIFICATION_CHANNEL_ID= "my_notify_channel";
    private List<String> keywords;
    private List<String> websites;
    private String email;

    private Map<String,String> result= new ConcurrentHashMap<>();
    public Map<String,String> oldDict= new ConcurrentHashMap<>();
    public Map<String,String> newDict= new ConcurrentHashMap<>();
    public Map<String,String> updatesDict= new ConcurrentHashMap<>();

    private MyService timerService;
    private static int checkInterval;  // notification time interval

    public static int getInterval(){
        return checkInterval;
    }

    private boolean checkEmailBox(){
       CheckBox emailBox = findViewById(R.id.check_Email);
       return emailBox.isChecked();
    }

    private boolean checkPush(){
        CheckBox pushBox = findViewById(R.id.check_Notification);
        return pushBox.isChecked();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            timerService = ((MyService.TimerBinder) iBinder).getService();
            timerService.setOnTimerServiceListener(onTimerServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
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
        setupPopupUI();
        setupButtonsUI();
    }

    private void setupTimer(){
        checkInterval = Integer.parseInt(((EditText)findViewById(R.id.checking_interval)).getText().toString());
    }

    private void showResult() {
        if (result.size() <= 50) {
            for (Map.Entry<String, String> entry : updatesDict.entrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }
        } else
            result = updatesDict;
    }

    private Runnable runnable= new Runnable() {
        @Override
        public void run() {
            WebsiteSearch wbs= new WebsiteSearch();
            boolean emailOption = checkEmailBox();
            boolean popupOption = checkPush();
            Log.i("email option check",String.valueOf(emailOption));
            Log.i("pull option",String.valueOf(popupOption));
            List<Map<String,String>> newAndUpdates= wbs.updateAlert(oldDict, keywords, websites,emailOption, email);
            newDict=newAndUpdates.get(0);
            updatesDict= newAndUpdates.get(1);
            oldDict=newDict;

            if(popupOption)
                sendNotification("New updates", String.valueOf(updatesDict.size())+" new article(s) found!");

        }
    };

    private void sendNotification(String title, String content)
    {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher_round))
                        .setContentTitle(title)
                        .setContentText(content)
                        .setWhen(System.currentTimeMillis())
                        .setTicker("I am a Notification")
                        .setDefaults(Notification.DEFAULT_ALL);

        NotificationManager mNotifyMgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("My Notification channel 1");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            if (mNotifyMgr != null) {
                mNotifyMgr.createNotificationChannel(notificationChannel);
            }
        }

        Intent resultIntent= new Intent(mContext, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent= PendingIntent.getActivity(mContext,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (mNotifyMgr != null) {
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private void setupKeywordUI(){
        final EditText editKeyword = findViewById(R.id.keyword);
        ImageButton addKeyword = findViewById(R.id.add_keyword);
        final LinearLayout keywordContainer = findViewById(R.id.keywords_container);

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
        final EditText editWebsite =  findViewById(R.id.website);
        ImageButton addWebsite =  findViewById(R.id.add_website);
        final LinearLayout websiteContainer = findViewById(R.id.website_container);


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

    // Email UI
    private void setupEmailUI() {
        final CheckBox checkEmail = findViewById(R.id.check_Email);
        final LinearLayout emailContainer =  findViewById(R.id.email_container);

        checkEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkEmail.isChecked()){
                    View view = getLayoutInflater().inflate(R.layout.email_item,null);
                    emailContainer.addView(view);
                    final EditText emailEdit =  findViewById(R.id.email);
                    emailEdit.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                        @Override
                        public void afterTextChanged(Editable editable) {
                            email = emailEdit.getText().toString();
                        }
                    });
                }
                else{
                    emailContainer.removeAllViews();
                    email = null;
                }
            }
        });
    }

    private void setupPopupUI() {
        final CheckBox checkNotification = findViewById(R.id.check_Notification);
    }


    private void setupButtonsUI(){
        Button setAlert = findViewById(R.id.setAlert);
        Button results = findViewById(R.id.results);
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
