package com.example.justforfun.keywordsalert;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Space;

public class ResultItemDecoration extends RecyclerView.ItemDecoration{
    private int space;

    public ResultItemDecoration(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;

        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = 0;

        }
    }
}
