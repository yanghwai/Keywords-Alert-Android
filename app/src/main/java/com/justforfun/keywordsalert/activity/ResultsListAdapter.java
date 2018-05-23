package com.justforfun.keywordsalert.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.justforfun.keywordsalert.R;
import com.justforfun.keywordsalert.entity.Result;

import java.util.List;

public class ResultsListAdapter extends BaseAdapter {
    private List<Result> data;
    private Context context;

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
        ((TextView)view.findViewById(R.id.results_title)).setText(data.get(position).getTitle());
        ((TextView)view.findViewById(R.id.results_website)).setText(data.get(position).getWebLink());
        return view;
    }
}
