package com.justforfun.keywordsalert.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.justforfun.keywordsalert.R;
import com.justforfun.keywordsalert.entity.Result;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    public static final String KEY_RESULT = "results";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setupUI(getIntent().getParcelableArrayListExtra(KEY_RESULT));
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