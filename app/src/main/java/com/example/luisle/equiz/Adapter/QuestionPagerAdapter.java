package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.luisle.equiz.Fragment.QuestionPagerFrag;

/**
 * Created by LuisLe on 4/5/2017.
 */

public class QuestionPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private String examID;
    private Integer numOfQuestion;

    public QuestionPagerAdapter(FragmentManager fm, Context context, String examID, Integer numOfQuestion) {
        super(fm);
        this.context = context;
        this.examID = examID;
        this.numOfQuestion = numOfQuestion;
    }

    @Override
    public int getCount() {
        return numOfQuestion;
    }

    @Override
    public Fragment getItem(int position) {

        return QuestionPagerFrag.newInstance(examID, position);
    }
}
