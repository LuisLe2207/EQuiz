package com.example.luisle.equiz.Fragment;

import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getDoneExam;
import static com.example.luisle.equiz.MyFramework.MyEssential.RESULT_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.inHomeFrag;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;

/**
 * Created by LuisLe on 4/13/2017.
 */

public class UserStatisticsFrag extends Fragment {

    // Fragment Palette Layout
    private TextView txtNoDoneExam;
    private RecyclerView rcvExam;
    private ProgressBar pgBarLoading;

    // Fragment Variables
    private ExamListAdapter examListAdapter;
    private ArrayList<Exam> examList;
    private ArrayList<String> examIDList;

    /**
     * Create new instance of Fragment
     * @return Fragment
     */
    public static UserStatisticsFrag newInstance() {
        UserStatisticsFrag fragment = new UserStatisticsFrag();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_user_statistics, container, false);
        inHomeFrag = false;

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
        rcvExam = (RecyclerView) view.findViewById(R.id.rcvFragUserStatistics_Exam);
        pgBarLoading = (ProgressBar) view.findViewById(R.id.pgBarFragUserStatistics_Loading);
        txtNoDoneExam = (TextView) view.findViewById(R.id.txtFragUserStatistics_NoExam);
    }

    /**
     * Init variables
     */
    private void initVariables() {
        examIDList = new ArrayList<>();
        examList = new ArrayList<>();
        examListAdapter = new ExamListAdapter(getContext(), examList);
    }

    /**
     * Init Fragment Data
     */
    private void initData() {
        pgBarLoading.setVisibility(View.VISIBLE);
        rcvExam.setVisibility(View.INVISIBLE);
        txtNoDoneExam.setVisibility(View.INVISIBLE);

        getDoneExamsID();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (examIDList.isEmpty()) {
                    pgBarLoading.setVisibility(View.INVISIBLE);
                    txtNoDoneExam.setVisibility(View.VISIBLE);
                    txtNoDoneExam.setText(getContext().getResources().getString(R.string.text_no_statistic));
                } else {
                    for (String examID : examIDList) {
                        getDoneExam(eQuizRef, examID, rcvExam, pgBarLoading, examList, examListAdapter);
                    }
                }
            }
        }, 1000);
        rcvExam.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    /**
     * Get DoneExamID from backend server with userID
     */
    private void getDoneExamsID() {
        eQuizRef.child(RESULT_CHILD).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setExamIDList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set exam id list from dataSnapshot after get it from backend server
     * @param dataSnapshot contain exam data
     */
    private void setExamIDList(DataSnapshot dataSnapshot) {
        for (DataSnapshot doneExamIDSnapshot: dataSnapshot.getChildren()) {
            examIDList.add(doneExamIDSnapshot.getKey());
        }
    }

    public void hideLayout() {
        rcvExam.setVisibility(View.INVISIBLE);
        ((HomeAct) getActivity()).getBottomNavigationView().setVisibility(View.INVISIBLE);
    }

    public void showLayout() {
        rcvExam.setVisibility(View.VISIBLE);
    }

}
