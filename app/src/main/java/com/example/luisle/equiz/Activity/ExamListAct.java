package com.example.luisle.equiz.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.luisle.equiz.Adapter.ExamListAdapter;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.MyFramework.EndlessScrollListener;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

public class ExamListAct extends AppCompatActivity {

    private RecyclerView rvcExam;
    private ExamListAdapter examListAdapter;
    private ArrayList<Exam> examList;
    private EndlessScrollListener scrollListener;

    private String GET_EXTRA_KEY = "CATEGORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_exam_list);

        String category = getIntent().getExtras().getString(GET_EXTRA_KEY);
        Toast.makeText(getApplicationContext(), category, Toast.LENGTH_SHORT).show();

        mappingLayout();
        examList = new ArrayList<>();
        examList.add(new Exam("1", "A", 50));
        examList.add(new Exam("1", "AB", 50));
        examList.add(new Exam("1", "ABC", 50));
        examList.add(new Exam("1", "ABCD", 50));
        examList.add(new Exam("1", "AD", 50));
        examList.add(new Exam("1", "AC", 50));
        examList.add(new Exam("1", "ACD", 50));
        examList.add(new Exam("1", "ABD", 50));
        examList.add(new Exam("1", "BCD", 50));
        examList.add(new Exam("1", "CBD", 50));
        examListAdapter = new ExamListAdapter(ExamListAct.this, examList);
        rvcExam.setAdapter(examListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvcExam.setLayoutManager(linearLayoutManager);
        rvcExam.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                customLoadMoreDataFromApi(current_page);
            }
        });
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

    private void mappingLayout() {
        rvcExam = (RecyclerView) findViewById(R.id.rcvExam);
    }
}
