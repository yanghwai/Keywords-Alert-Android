package com.example.justforfun.keywordsalert;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {
    boolean sendSucceed = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupUI();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setupUI(){
        Button submit = findViewById(R.id.submit_feedback);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(runnable).start();
                if(sendSucceed){
                    Toast.makeText(FeedbackActivity.this,getString(R.string.feedback_sent),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(FeedbackActivity.this,getString(R.string.feedback_fail),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            EditText content = findViewById(R.id.content);
            if(!content.getText().toString().equals("")){
                EmailUtils emailUtils = new EmailUtils("keywords.alert.new@gmail.com","alert6666");
                try {
                    emailUtils.sendMail("Keywords alert feedback",content.getText().toString(),"keywords.alert.new@gmail.com","jinghao.qiao@mail.mcgill.ca");
                }catch (Exception e){
                    e.printStackTrace();
                    sendSucceed = false;
                }
            }

        }
    };
}
