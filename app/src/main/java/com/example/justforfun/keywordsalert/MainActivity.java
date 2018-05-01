package com.example.justforfun.keywordsalert;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private ArrayList<TextView> keywordsView;
    private ArrayList<TextView> websitesView;
    private ArrayList<String> keywords;
    private ArrayList<String> websites;
    private String email;
    private int checkInteval;
    private EditText emailText;

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
        addKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = getLayoutInflater().inflate(R.layout.keyword_item,null);
                keywordContainer.addView(v);

                final EditText keyword = v.findViewById(R.id.keyword_edit);
                ImageButton removeKeyWord = v.findViewById(R.id.remove_keyword);

                keywordsView.add(keyword);
                removeKeyWord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        keywordContainer.removeView(v);
                        keywordsView.remove(keyword);
                    }
                });
            }
        });
    }

    private void setupWebsites(){
        TextView firstWebsite = findViewById(R.id.input_website);
        ImageButton addWebsite = findViewById(R.id.add_website);
        final LinearLayout websiteContainer = findViewById(R.id.websites_container);

        websitesView = new ArrayList<>();
        websitesView.add(firstWebsite);
        addWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = getLayoutInflater().inflate(R.layout.website_item,null);
                websiteContainer.addView(v);

                final EditText website = v.findViewById(R.id.website_edit);
                ImageButton removeWebsite = v.findViewById(R.id.remove_website);

                websitesView.add(website);
                removeWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        websitesView.remove(website);
                        websiteContainer.removeView(v);
                    }
                });
            }
        });
    }

    private void setupAlert(){
        final CheckBox checkEmail = findViewById(R.id.check_Email);
        final CheckBox checkNotification = findViewById(R.id.check_Notification);
        final LinearLayout emailContainer = findViewById(R.id.email_container);

        checkEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkEmail.isChecked()){
                    View view = getLayoutInflater().inflate(R.layout.email_item,null);
                    emailContainer.addView(view);
                    emailText = view.findViewById(R.id.email);
                }else{
                    emailContainer.removeAllViews();
                    emailText = null;
                }
            }
        });
        checkNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkNotification.isChecked()){

                }
            }
        });
    }

    private void setupButtons(){
        Button setAlert = findViewById(R.id.setAlert);
        Button results = findViewById(R.id.results);

        setAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keywords = new ArrayList<>();
                websites = new ArrayList<>();
                checkInteval = 0;
                String checkInput = ((EditText)findViewById(R.id.checking_interval)).getText().toString();


                if(!checkInput.equals("")){// set checking inteval
                    checkInteval = Integer.parseInt(checkInput);
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
                if(emailText != null && !emailText.getText().toString().equals("")){
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
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }else if(emailText !=null && email==null){
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                            .setMessage(getString(R.string.alert_email))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
                else if(checkInteval == 0){
                    new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                            .setMessage(getString(R.string.alert_interval))
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
                else {
                    for (String keyword : keywords) {
                        Toast.makeText(MainActivity.this, keyword, Toast.LENGTH_SHORT).show();
                    }
                    for (String website : websites) {
                        Toast.makeText(MainActivity.this, website, Toast.LENGTH_SHORT).show();
                    }
                    if(email!=null)
                        Toast.makeText(MainActivity.this,email,Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, checkInteval + "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
