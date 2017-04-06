package com.example.luisle.equiz.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.luisle.equiz.Adapter.QuestionPagerAdapter;
import com.example.luisle.equiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.example.luisle.equiz.MyFramework.MyEssential.EXAM_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;

public class MainAct extends AppCompatActivity {

    private String examID;
    private Integer numOfQuestion;


    private ViewPager viewPgActMain_Question;
    private QuestionPagerAdapter questionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        examID = getIntent().getStringExtra("ID");

        mappingLayout();
        getNumOfQuestion(eQuizRef, examID);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                questionPagerAdapter = new QuestionPagerAdapter(getSupportFragmentManager(), MainAct.this, examID, numOfQuestion);
                viewPgActMain_Question.setAdapter(questionPagerAdapter);
            }
        }, 2000);

    }

    private void mappingLayout() {
        viewPgActMain_Question = (ViewPager) findViewById(R.id.viewPgActMain_Question);
    }

    public void getNumOfQuestion(DatabaseReference dataRef, String examID) {
        dataRef.child(EXAM_CHILD).child(examID).child("numberOfQuestion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setNumOfQuestion(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setNumOfQuestion(DataSnapshot dataSnapshot) {
        numOfQuestion = dataSnapshot.getValue(Integer.class);
    }

}
