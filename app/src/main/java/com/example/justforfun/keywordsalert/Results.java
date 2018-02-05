package com.example.justforfun.keywordsalert;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class Results extends AppCompatActivity {
    private HashMap<String, String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        receiveData();
        setupResultsUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void receiveData(){
        Bundle bundle = getIntent().getExtras();
        SerializableMap serializableMap = (SerializableMap) bundle.get("map");
        results = serializableMap.getMap();
    }
    private void setupResultsUI(){
        for(Map.Entry<String,String> result: results.entrySet()) {
            View view = getLayoutInflater().inflate(R.layout.result_item, null);
            ((TextView)view.findViewById(R.id.results_title)).setText(result.getKey());

            TextView link= (TextView) view.findViewById(R.id.results_website);
            link.setText(result.getValue());
            Linkify.addLinks(link, Linkify.WEB_URLS);
           // ((TextView)view.findViewById(R.id.results_website)).setText(result.getValue());
            ((LinearLayout) findViewById(R.id.results_container)).addView(view);
        }
    }
}
