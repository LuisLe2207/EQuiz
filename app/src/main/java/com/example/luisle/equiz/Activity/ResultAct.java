package com.example.luisle.equiz.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.luisle.equiz.Adapter.QuestionResultGridAdapter;
import com.example.luisle.equiz.Model.ExamResult;
import com.example.luisle.equiz.Model.QuestionResult;
import com.example.luisle.equiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getMaintainStatus;
import static com.example.luisle.equiz.MyFramework.MyEssential.RESULT_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;

public class ResultAct extends AppCompatActivity {

    // region VARIABLES

    // Act Palette Layout
    private RecyclerView rcvResult;
    private EditText edtExamTitle, edtCompleteTime, edtCorrectAnswer;

    // Act Variables
    private String examID;
    private String examResultID = "";
    private ExamResult examResult;
    private boolean doubleBackToExitPressedOnce = false;
    private ArrayList<QuestionResult> questionResultList;
    private QuestionResultGridAdapter questionResultGridAdapter;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_result);

        initVariables();
        mappingLayout();
        createActionBar();
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backToHome();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        edtExamTitle = (EditText) findViewById(R.id.edtActResult_ExamTitle);
        edtCompleteTime = (EditText) findViewById(R.id.edtActResult_CompleteTime);
        edtCorrectAnswer = (EditText) findViewById(R.id.edtActResult_CorrectAnswer);
        rcvResult = (RecyclerView) findViewById(R.id.rcvActResult_Result);

    }

    private void initVariables() {
        // Get FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Get DatabaseRef
        eQuizRef = eQuizDatabase.getReference();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMaintainStatus(ResultAct.this);
            }
        }, 2000);
    }

    /**
     *  Create action bar layout
     */
    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarActResult);
        toolbar.setTitle(getResources().getString(R.string.toolbar_result));

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_nav_home);
        }
    }

    // Init act first data
    private void initData() {
        // Init array list
        questionResultList = new ArrayList<>();
        // Get data from intent extra
        Bundle resultBundle = getIntent().getBundleExtra("ID");
        examID = resultBundle.getString("examID");
        examResultID = resultBundle.getString("examResultID");

        // Check examID is valid
        if (examID != null && !examID.isEmpty()) {
            // Set data
            getResults();
            final ProgressDialog collectingDataProgressDialog = createProgressDialog(ResultAct.this,
                    getResources().getString(R.string.text_progress_collecting_result));
            collectingDataProgressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setExamResult();
                    collectingDataProgressDialog.dismiss();
                }
            }, 3000);
        }
    }

    /**
     * Set exam result data
     */
    private void setExamResult() {
        edtExamTitle.setText(examResult.getExamTitle());
        edtCompleteTime.setText("" + String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(examResult.getCompleteTime()),
                TimeUnit.MILLISECONDS.toSeconds(examResult.getCompleteTime()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(examResult.getCompleteTime()))));
        edtCorrectAnswer.setText(String.valueOf(examResult.getCorrectAnswer()));
        questionResultList.addAll(examResult.getQuestionResults());
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        questionResultGridAdapter = new QuestionResultGridAdapter(ResultAct.this, questionResultList, manager);
        rcvResult.setLayoutManager(manager);
        rcvResult.setAdapter(questionResultGridAdapter);
    }


    private void backToHome() {
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

    private void getResults() {
        Query query = null;
        if (examResultID.isEmpty()) {
            // Get last value
            query = eQuizRef.child(RESULT_CHILD).child(userID).child(examID)
                    .limitToLast(1);
        } else {
            query = eQuizRef.child(RESULT_CHILD).child(userID).child(examID).child(examResultID);
        }
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setResult(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setResult(DataSnapshot dataSnapshot) {
        if (examResultID.isEmpty()) {
            for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {
                examResult = resultSnapshot.getValue(ExamResult.class);
                Log.d("Question", examResult.getExamTitle());
            }
        } else {
            examResult = dataSnapshot.getValue(ExamResult.class);
        }
    }

}
