package com.example.luisle.equiz.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.MyFramework.MyEssential;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

public class MainAct extends AppCompatActivity {

    private RecyclerView rcvMain;
    private ArrayList<Question> questionList;
    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        String myIntent = getIntent().getStringExtra("ID");
        MyEssential.showToast(getApplicationContext(), myIntent);

    }
}
