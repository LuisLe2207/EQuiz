package com.example.luisle.equiz.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.luisle.equiz.Activity.HomeAct;
import com.example.luisle.equiz.Adapter.ExamListAdapter;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getExams;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.inHomeFrag;

/**
 * Created by LuisLe on 4/5/2017.
 */

public class HomeFrag extends Fragment {

    // Fragment Palette Layout
    private RecyclerView rcvFragHomeExam;
    private ProgressBar pgBarLoading;
    private TextView txtNoExam;

    // Fragment Variables
    private ExamListAdapter examListAdapter;
    private ArrayList<Exam> examList;

    /**
     * Create new instance of Fragment
     * @return Fragment
     */
    public static HomeFrag newInstance() {
        HomeFrag homeFrag = new HomeFrag();
        return homeFrag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
        inHomeFrag = true;
        mappingPaletteLayout(view);
        initVariables();
        initData();
        return view;
    }

    /**
     * Mapping Fragment Palette Layout
     * @param view layout
     */
    private void mappingPaletteLayout(View view) {
        rcvFragHomeExam = (RecyclerView) view.findViewById(R.id.rcvFragHomeExam);
        pgBarLoading = (ProgressBar) view.findViewById(R.id.pgBarFragHome_Loading);
        txtNoExam = (TextView) view.findViewById(R.id.txtFragHome_NoExams);
    }

    /**
     * Init variables
     */
    private void initVariables() {
        examList = new ArrayList<>();
        examListAdapter = new ExamListAdapter(getContext(), examList);
    }

    /**
     * Init Fragment Data
     */
    private void initData() {
        pgBarLoading.setVisibility(View.VISIBLE);
        rcvFragHomeExam.setVisibility(View.INVISIBLE);
        txtNoExam.setVisibility(View.INVISIBLE);
        getExams(getContext(), eQuizRef, rcvFragHomeExam, pgBarLoading, txtNoExam, examList, examListAdapter);
        rcvFragHomeExam.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void hideLayout() {
        rcvFragHomeExam.setVisibility(View.INVISIBLE);
        ((HomeAct) getActivity()).getBottomNavigationView().setVisibility(View.INVISIBLE);
    }

    public void showLayout() {
        rcvFragHomeExam.setVisibility(View.VISIBLE);
    }
}
