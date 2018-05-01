package com.example.justforfun.keywordsalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

public class ResultsActivity extends AppCompatActivity {
//    public static Map<String, String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        receiveData();
        setupResultsUI();
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void receiveData(){
//        Bundle bundle = getIntent().getExtras();
//        SerializableMap serializableMap = (SerializableMap) bundle.get("map");
//        results = serializableMap.getMap();
//    }
    private void setupResultsUI(){
        for(Map.Entry<String,String> result: MainActivity.updatesDict.entrySet()) {
            View view = getLayoutInflater().inflate(R.layout.result_item, null);
            ((TextView)view.findViewById(R.id.results_title)).setText(result.getKey());

            TextView link= view.findViewById(R.id.results_website);
            link.setText(result.getValue());
            Linkify.addLinks(link, Linkify.WEB_URLS);
           // ((TextView)view.findViewById(R.id.results_website)).setText(result.getValue());
            ((LinearLayout) findViewById(R.id.results_container)).addView(view);
        }
    }

    private void refresh(){
        finish();
        Intent intent = new Intent(ResultsActivity.this, ResultsActivity.class);
        startActivity(intent);
    }
}
