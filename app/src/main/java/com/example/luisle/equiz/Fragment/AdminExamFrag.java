package com.example.luisle.equiz.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Adapter.ExamListAdapter;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getExams;
import static com.example.luisle.equiz.MyFramework.MyEssential.allowModify;
import static com.example.luisle.equiz.MyFramework.MyEssential.dialogOnScreen;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.inAddExamDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class AdminExamFrag extends Fragment{

    private RecyclerView rcvExam;
    private ProgressBar pgBarLoading;
    private FloatingActionButton fabExamAdd;
    private ExamListAdapter examListAdapter;
    private ArrayList<Exam> examList;


    // TODO: Rename and change types and number of parameters
    public static AdminExamFrag newInstance() {
        AdminExamFrag fragment = new AdminExamFrag();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_exam_admin, container, false);
        mappingLayout(view);
        fabExamAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allowModify) {
                    dialogOnScreen = true;
                    inAddExamDialog = true;
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    ExamFrag examFrag = new ExamFrag();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    hideLayout();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(android.R.id.content, examFrag).addToBackStack(null).commit();
                } else {
                    showToast(getContext(),
                                      getContext().getResources().getString(R.string.alert_you_must_create_push_notification)
                                    + " "
                                    + getContext().getResources().getString(R.string.text_create));
                }
            }
        });
        init();
        return view;
    }

    private void mappingLayout(View view) {
        rcvExam = (RecyclerView) view.findViewById(R.id.rcvExamAdmin);
        pgBarLoading = (ProgressBar) view.findViewById(R.id.pgBarFragExamAdmin_Loading);
        fabExamAdd = (FloatingActionButton) view.findViewById(R.id.fabFragExamAdd);
    }

    private void init() {
        pgBarLoading.setVisibility(View.VISIBLE);
        rcvExam.setVisibility(View.INVISIBLE);
        examList = new ArrayList<>();
        examListAdapter = new ExamListAdapter(getContext(), examList);
        getExams(eQuizRef, rcvExam, pgBarLoading, examList, examListAdapter);
        rcvExam.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void hideLayout() {
        fabExamAdd.hide();
        rcvExam.setVisibility(View.INVISIBLE);
        ((AdminHomeAct) getActivity()).getBottomNavigationView().setVisibility(View.INVISIBLE);
    }

    public void showLayout() {
        fabExamAdd.show();
        rcvExam.setVisibility(View.VISIBLE);
    }
}
