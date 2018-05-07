package com.example.justforfun.keywordsalert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.justforfun.keywordsalert.models.Result;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    public static final String KEY_RESULT = "results";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setupUI(getIntent().<Result>getParcelableArrayListExtra(KEY_RESULT));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupUI(List<Result> data) {
        ListView listView = findViewById(R.id.results_list_view);
        listView.setAdapter(new ResultsListAdapter(data, this));
    }
}
