package com.example.justforfun.keywordsalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.justforfun.keywordsalert.models.Result;

import java.util.List;

public class ResultsListAdapter extends BaseAdapter {
    private List<Result> data;
    Context context;

    public ResultsListAdapter(List<Result> data, Context context){
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_item,parent,false);
        ((TextView)view.findViewById(R.id.results_title)).setText(data.get(position).title);
        ((TextView)view.findViewById(R.id.results_website)).setText(data.get(position).webLink);
        return view;
    }
}
