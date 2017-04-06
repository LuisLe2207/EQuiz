package com.example.luisle.equiz.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LuisLe on 4/5/2017.
 */

public class QuestionPageAdapter extends RecyclerView.Adapter<QuestionPageAdapter.QuestionPageViewHolder> {


    @Override
    public QuestionPageAdapter.QuestionPageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(QuestionPageAdapter.QuestionPageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class QuestionPageViewHolder extends RecyclerView.ViewHolder {

        public QuestionPageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
