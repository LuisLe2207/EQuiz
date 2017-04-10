package com.example.luisle.equiz.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.luisle.equiz.Adapter.QuestionPagerAdapter;
import com.example.luisle.equiz.Fragment.QuestionPagerFrag;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.Model.Result;
import com.example.luisle.equiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.saveResult;
import static com.example.luisle.equiz.MyFramework.MyEssential.EXAM_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.QUESTION_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.createAlertDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

public class MainAct extends AppCompatActivity {

    private String examID;
    private String userID;
    private Exam exam;
    private int examDuration;
    private boolean isTimeOut = false;
    private ArrayList<Question> questionList;
    private ArrayList<Result> resultList;
    private ArrayList<Integer> unChooseList;
    private CountDownTimer examDutaionCountDown;


    private ViewPager viewPgActMain_Question;
    private FloatingActionButton fabMainQuestionList;
    private QuestionPagerAdapter questionPagerAdapter;
    private CircleProgressView cpgViewDuration;

    private ProgressDialog progressDialogRetrievingData;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        // Init FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Init DatabaseRef
        eQuizRef = eQuizDatabase.getReference();
        // Get current user

        examID = getIntent().getStringExtra("ID");

        mappingLayout();
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem createActionItem = menu.add(1,333,1, getResources().getString(R.string.text_submit));
        createActionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 333:
                submitExam();
                break;
            case android.R.id.home:
                quitExam();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        quitExam();
    }

    private void mappingLayout() {
        viewPgActMain_Question = (ViewPager) findViewById(R.id.viewPgActMain_Question);
        fabMainQuestionList = (FloatingActionButton) findViewById(R.id.fabMainQuestionList);
        cpgViewDuration = (CircleProgressView) findViewById(R.id.cpgViewActMain_Duration);
    }

    private void init() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        questionList = new ArrayList<>();
        resultList = new ArrayList<>();
        unChooseList = new ArrayList<>();
        getExam(examID);
        getQuestions();
        progressDialogRetrievingData  = createProgressDialog(MainAct.this, getResources().getString(R.string.text_progress_retrieving));
        progressDialogRetrievingData.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createActionBar();
                examDuration = exam.getDuration() * 60000;
                userID = firebaseUser.getUid();
                questionPagerAdapter = new QuestionPagerAdapter(getSupportFragmentManager(), MainAct.this, examID, exam.getNumberOfQuestion());
                viewPgActMain_Question.setAdapter(questionPagerAdapter);
                progressDialogRetrievingData.dismiss();
                setDuration();
                examDutaionCountDown.start();
            }
        }, 2000);
    }

    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle(exam.getTitle());

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
    }

    private void submitExam() {
        AlertDialog.Builder submitAlertDialog = createAlertDialog(MainAct.this, getResources().getString(R.string.text_submit_exam));
        submitAlertDialog.setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Set result list
                if (setResultList()) {
                    // Save to firebase
                    saveResult(MainAct.this, eQuizRef, userID, examID, resultList);
                }
            }
        });
        submitAlertDialog.setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        submitAlertDialog.show();
    }

    private void quitExam() {
        final AlertDialog.Builder quitAlertDialog = createAlertDialog(MainAct.this, getResources().getString(R.string.text_quit_exam));
        quitAlertDialog.setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final ProgressDialog quitProgressDialog = createProgressDialog(MainAct.this, getResources().getString(R.string.text_progress_quit));
                quitProgressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        examDutaionCountDown.cancel();
                        quitProgressDialog.dismiss();
                        startActivity(new Intent(MainAct.this, HomeAct.class));
                    }
                }, 2000);

            }
        });
        quitAlertDialog.setNegativeButton(getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        quitAlertDialog.show();
    }

    private void setDuration() {
        cpgViewDuration.setMaxValue(examDuration);
        cpgViewDuration.setValue(examDuration);
        examDutaionCountDown = new CountDownTimer(examDuration, 1000) {
            @Override
            public void onTick(long l) {
                cpgViewDuration.setTextMode(TextMode.TEXT);
                cpgViewDuration.setText("" + String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l))));
                cpgViewDuration.setValue(l);
                if (l < 60000 && l > 59000) {
                    showToast(getApplicationContext()
                            , getResources().getString(R.string.warning_1_minute_left));
                }
                if (l < 30000 && l > 29000) {
                    showToast(getApplicationContext()
                            , getResources().getString(R.string.warning_30_seconds_left));
                }
                if (l < 15000 && l > 14000) {
                    showToast(getApplicationContext()
                            , getResources().getString(R.string.warning_15_seconds_left));
                }
            }

            @Override
            public void onFinish() {
                isTimeOut = true;
                showToast(getApplicationContext(), getResources().getString(R.string.alert_exam_will_be_submit));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (setResultList()) {
                            // Save to firebase
                            saveResult(MainAct.this, eQuizRef, userID, examID, resultList);
                        }
                    }
                }, 2000);
            }
        };
    }

    private boolean setResultList() {
        boolean allowSubmit = false;
        boolean hasChoose = false;
        QuestionPagerFrag questionPagerFrag = null;
        ArrayList<Integer> answerList = new ArrayList<>();
        ArrayList<Integer> userAnswerList = new ArrayList<>();
        for (int i = 0; i < questionPagerAdapter.getCount(); i ++) {
            // Get fragment in viewpager by tag
            questionPagerFrag = (QuestionPagerFrag) getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:"
                            + R.id.viewPgActMain_Question + ":"
                            + questionPagerAdapter.getItemId(i));
            if (questionPagerFrag == null) {
                allowSubmit = false;
                if (!unChooseList.contains(i)) {
                    unChooseList.add(i);
                }
            } else {
                if (questionPagerFrag.getUserAnswerChoice().isEmpty()) {
                    allowSubmit = false;
                    hasChoose = false;
                    if (!unChooseList.contains(i)) {
                        unChooseList.add(i);
                    }
                } else {
                    // Get question answer list
                    answerList.clear();
                    answerList.addAll(questionList.get(i).getAnswerList());
                    // Get user answer for each question
                    userAnswerList.clear();
                    userAnswerList.addAll(questionPagerFrag.getUserAnswerChoice());
                    // Check user answer is correct or not
                    switch (questionList.get(i).getQuestionType()) {
                        case "Single":
                            /**
                             * Get user answer from user answer list at index = 0
                             * In this case in single answer question
                             */
                            Integer userAnswerSingle = userAnswerList.get(0);
                            Result resultSingle = null;
                            if (answerList.contains(userAnswerSingle)) {
                                resultSingle = new Result(questionList.get(i).getID(), questionPagerFrag.getUserAnswerChoice(), true);
                            } else {
                                resultSingle = new Result(questionList.get(i).getID(), questionPagerFrag.getUserAnswerChoice(), false);
                            }
                            resultList.add(resultSingle);

                            break;
                        case "Multiple":
                            boolean check = false;
                            if (userAnswerList.size() < answerList.size()) {
                                check = false;
                            } else if (userAnswerList.size() > answerList.size()) {
                                check = false;
                            } else {
                                for (Integer userAnswerMultiple : userAnswerList) {
                                    if (answerList.contains(userAnswerMultiple)) {
                                        check = true;
                                    } else {
                                        check = false;
                                        break;
                                    }
                                }
                            }
                            Result resultMultiple = null;
                            if (check) {
                                resultMultiple = new Result(questionList.get(i).getID(), questionPagerFrag.getUserAnswerChoice(), true);
                            } else {
                                resultMultiple = new Result(questionList.get(i).getID(), questionPagerFrag.getUserAnswerChoice(), false);
                            }
                            resultList.add(resultMultiple);
                            break;
                    }
                    allowSubmit = true;
                    hasChoose = true;
                    if (unChooseList.contains(i)) {
                        unChooseList.remove(unChooseList.indexOf(i));
                    }
                }
            }
        }
        if (!hasChoose) {
            if (!isTimeOut) {
                showToast(getApplicationContext(), getResources().getString(R.string.warning_not_choose_answer));
            } else {
                for (Integer i : unChooseList) {
                    resultList.add(new Result(questionList.get(i).getID(), null, false));
                }
                allowSubmit = true;
            }

        }
        return allowSubmit;
    }


    public void getExam(String examID) {
        eQuizRef.child(EXAM_CHILD).child(examID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setExam(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setExam(DataSnapshot dataSnapshot) {
        exam = dataSnapshot.getValue(Exam.class);
    }

    private void getQuestions() {
        eQuizRef.child(QUESTION_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setQuestionList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setQuestionList(DataSnapshot dataSnapshot) {
        for (DataSnapshot questionSnapshot: dataSnapshot.getChildren()) {
            Question question = questionSnapshot.getValue(Question.class);
            if (exam.getQuestionList().contains(question.getID())) {
                questionList.add(question);
            }
        }
    }
}
