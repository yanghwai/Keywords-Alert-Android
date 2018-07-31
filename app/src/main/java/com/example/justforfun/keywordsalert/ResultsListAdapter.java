package com.example.justforfun.keywordsalert;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.justforfun.keywordsalert.models.Result;

import java.util.List;

public class ResultsListAdapter extends RecyclerView.Adapter {
    List<Result> data;
    LayoutInflater inflater;

    public ResultsListAdapter(List<Result> data, Context context){
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.result_item,parent,false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Result result = data.get(position);

        ResultViewHolder viewHolder = (ResultViewHolder) holder;
        viewHolder.title.setText(result.title);
        viewHolder.website.setText(result.webLink);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
