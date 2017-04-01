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

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Adapter.ExamListAdapter;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.MyFramework.EndlessScrollListener;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class AdminExamFrag extends Fragment{

    private RecyclerView rcvExam;
    private FloatingActionButton fabExamAdd;
    private ExamListAdapter examListAdapter;
    private ArrayList<Exam> examList;
    private EndlessScrollListener scrollListener;


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
        examList = new ArrayList<>();
        examList.add(new Exam("1", "A", 50));
        examList.add(new Exam("2", "AB", 50));
        examList.add(new Exam("3", "ABC", 50));
        examList.add(new Exam("4", "ABCD", 50));
        examList.add(new Exam("5", "AD", 50));
        examList.add(new Exam("6", "AC", 50));
        examList.add(new Exam("7", "ACD", 50));
        examList.add(new Exam("8", "ABD", 50));
        examList.add(new Exam("9", "BCD", 50));
        examList.add(new Exam("10", "CBD", 50));
        examListAdapter = new ExamListAdapter(getContext(), examList);
        rcvExam.setAdapter(examListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvExam.setLayoutManager(linearLayoutManager);
        rcvExam.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                customLoadMoreDataFromApi(current_page);
            }
        });
        fabExamAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ExamFrag examFrag = new ExamFrag();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                AdminExamFrag adminExamFrag = (AdminExamFrag) fragmentManager.findFragmentByTag("ExamListFrag");
//                if (adminExamFrag != null && adminExamFrag.isVisible()) {
//                    transaction.detach(adminExamFrag);
//                }
                hideLayout();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(android.R.id.content, examFrag).addToBackStack(null).commit();
            }
        });
        return view;
    }


    public void customLoadMoreDataFromApi(int offset) {
        ArrayList<Exam> arr = new ArrayList<>();
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        arr.add(new Exam("1", "ABC", 70));
        examList.addAll(arr);

        int curSize = examListAdapter.getItemCount();
        examListAdapter.notifyItemRangeInserted(curSize, examList.size() - 1);
    }

    private void mappingLayout(View view) {
        rcvExam = (RecyclerView) view.findViewById(R.id.rcvExamAdmin);
        fabExamAdd = (FloatingActionButton) view.findViewById(R.id.fabFragExamAdd);
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
