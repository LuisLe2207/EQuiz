package com.example.luisle.equiz.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.luisle.equiz.Adapter.ResultGridAdapter;
import com.example.luisle.equiz.Model.Result;
import com.example.luisle.equiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.MyEssential.RESULT_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

public class ResultAct extends AppCompatActivity {

    private RecyclerView rcvResult;
    private Button btnBackToHome;
    private ArrayList<Result> resultList;
    private ResultGridAdapter resultGridAdapter;

    private String examID;
    private String userID;
    private boolean doubleBackToExitPressedOnce = false;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_result);

        // Init FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Init DatabaseRef
        eQuizRef = eQuizDatabase.getReference();
        // Get current user

        mappingLayout();
        init();
        backToHome();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }

        this.doubleBackToExitPressedOnce = true;
        showToast(getApplicationContext(), getResources().getString(R.string.press_twice_to_minimize));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void mappingLayout() {
        rcvResult = (RecyclerView) findViewById(R.id.rcvActResult_Result);
        btnBackToHome = (Button) findViewById(R.id.btnActResult_BackHome);
    }

    private void init() {
        resultList = new ArrayList<>();
        examID = getIntent().getStringExtra("ID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

        final ProgressDialog collectingDataProgressDialog = createProgressDialog(ResultAct.this,
                getResources().getString(R.string.text_progress_collecting_result));
        collectingDataProgressDialog.show();
        getResults();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                resultGridAdapter = new ResultGridAdapter(ResultAct.this, resultList, manager);
                rcvResult.setLayoutManager(manager);
                rcvResult.setAdapter(resultGridAdapter);
                collectingDataProgressDialog.dismiss();
            }
        }, 3000);
    }

    private void backToHome() {
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog backHomeProgressDialog = createProgressDialog(ResultAct.this,
                        getResources().getString(R.string.text_progress_back_home));
                backHomeProgressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backHomeProgressDialog.dismiss();
                        startActivity(new Intent(ResultAct.this, HomeAct.class));
                    }
                }, 2000);
            }
        });
    }

    private void getResults() {
        eQuizRef.child(RESULT_CHILD).child(userID).child(examID)
                .limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setResultList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setResultList(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            for (DataSnapshot resultSnapshot : snapshot.getChildren()) {
                Result result = resultSnapshot.getValue(Result.class);
                resultList.add(result);
                Log.d("Question", result.getQuestionID());
            }
        }
    }
}
