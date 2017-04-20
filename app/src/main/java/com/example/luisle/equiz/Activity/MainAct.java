package com.example.luisle.equiz.Activity;

import android.app.Dialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.luisle.equiz.Adapter.QuestionPagerAdapter;
import com.example.luisle.equiz.Fragment.QuestionPagerFrag;
import com.example.luisle.equiz.Model.Comment;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.Model.ExamResult;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.Model.QuestionResult;
import com.example.luisle.equiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getMaintainStatus;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.saveResult;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.submitComment;
import static com.example.luisle.equiz.MyFramework.MyEssential.EXAM_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.QUESTION_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.createAlertDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.createDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;


public class MainAct extends AppCompatActivity {

    // region VARIABLES

    // Act Palette Layout
    private ViewPager viewPgActMain_Question;
    private FloatingActionButton fabMainQuestionList;
    private QuestionPagerAdapter questionPagerAdapter;
    private CircleProgressView cpgViewDuration;

    // Act Variables
    private String examID;
    private Exam exam;
    private int examDuration;
    private long completeTime = 0;
    private boolean isTimeOut = false;
    private ArrayList<Question> questionList;
    private ArrayList<QuestionResult> questionResultList;
    private ArrayList<Integer> unChooseList;
    private CountDownTimer examDutaionCountDown;

    // Act Dialog Layout
    private ProgressDialog progressDialogRetrievingData;
    private Dialog rateCommentDialog;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mappingPaletteLayout();
        initVariables();
        initData();
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

    /**
     *  Mapping Palette Layout
     */
    private void mappingPaletteLayout() {
        viewPgActMain_Question = (ViewPager) findViewById(R.id.viewPgActMain_Question);
        fabMainQuestionList = (FloatingActionButton) findViewById(R.id.fabMainQuestionList);
        cpgViewDuration = (CircleProgressView) findViewById(R.id.cpgViewActMain_Duration);
    }

    /**
     * Init variables
     */
    private void initVariables() {
        // Get FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Get DatabaseRef
        eQuizRef = eQuizDatabase.getReference();
        // Get exam ID from Intent put extra
        examID = getIntent().getStringExtra("ID");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMaintainStatus(MainAct.this);
            }
        }, 2000);
    }

    /**
     *  Init Act Data
     */
    private void initData() {
        // Init Array List
        questionList = new ArrayList<>();
        questionResultList = new ArrayList<>();
        unChooseList = new ArrayList<>();
        // Check exam id not null and not empty
        if (examID != null && !examID.isEmpty()) {
            // Get Exam
            getExam(examID);
            // Get Question List
            getQuestions();
            // Declare comment dialog
            rateCommentDialog = createDialog(MainAct.this, R.layout.dialog_rate_comment, "Rate & Comment");
            rateCommentDialog.setCanceledOnTouchOutside(false);
            // Declare progress dialog
            progressDialogRetrievingData  = createProgressDialog(MainAct.this, getResources().getString(R.string.text_progress_retrieving));
            if (exam == null) {
                progressDialogRetrievingData.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (exam != null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    createActionBar();
                                    // Get exam duration
                                    examDuration = exam.getDuration() * 60000;
                                    // Set question viewpager adapter
                                    questionPagerAdapter = new QuestionPagerAdapter(getSupportFragmentManager(), MainAct.this, examID, exam.getNumberOfQuestion());
                                    viewPgActMain_Question.setAdapter(questionPagerAdapter);
                                    // Set exam duration
                                    setDuration();
                                    // Start countdown
                                    examDutaionCountDown.start();
                                }
                            }, 500);
                        }
                        progressDialogRetrievingData.dismiss();
                    }
                }, 1500);
            }
        }
    }

    /**
     *  Create action bar layout
     */
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

    // Submit exam
    private void submitExam() {
        // Declare alert dialog
        AlertDialog.Builder submitAlertDialog = createAlertDialog(MainAct.this, getResources().getString(R.string.text_submit_exam));
        submitAlertDialog.setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Stop count down
                examDutaionCountDown.cancel();
                // Set result list
                if (setResultList()) {
                    // Save to firebase
                    ExamResult examResult = new ExamResult("", exam.getTitle(), completeTime, 0, questionResultList);
                    saveResult(MainAct.this, eQuizRef, userID, examID, examResult);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rateAndComment(rateCommentDialog);
                            rateCommentDialog.show();
                        }
                    }, 3000);
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

    /**
     *  Quit doing exam
     */
    private void quitExam() {
        // Declare alert dialog
        final AlertDialog.Builder quitAlertDialog = createAlertDialog(MainAct.this, getResources().getString(R.string.text_quit_exam));
        quitAlertDialog.setPositiveButton(getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Stop count down
                examDutaionCountDown.cancel();
                final ProgressDialog quitProgressDialog = createProgressDialog(MainAct.this, getResources().getString(R.string.text_progress_quit));
                quitProgressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
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

    /**
     *  Set exam duration
     */
    private void setDuration() {
        // Set exam duration value for circle progress bar
        cpgViewDuration.setMaxValue(examDuration);
        cpgViewDuration.setValue(examDuration);
        // Declare a count down
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
                completeTime = examDuration - l;
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
                            ExamResult examResult = new ExamResult("", exam.getTitle(), completeTime, 0, questionResultList);
                            saveResult(MainAct.this, eQuizRef, userID, examID, examResult);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    rateAndComment(rateCommentDialog);
                                    rateCommentDialog.show();
                                }
                            }, 3000);
                        }
                    }
                }, 2000);
            }
        };
    }

    /**
     *  Add user choice to result list
     * @return boolean Allow to submit exam
     */
    private boolean setResultList() {
        // Declare flag variables to check
        boolean allowSubmit = false;
        boolean hasChoose = false;
        // Declare fragment to store current viewpager fragment
        QuestionPagerFrag questionPagerFrag = null;
        // Init array list
        ArrayList<Integer> answerList = new ArrayList<>();
        ArrayList<Integer> userAnswerList = new ArrayList<>();

        // Start a loop through question viewpager adapter
        for (int i = 0; i < questionPagerAdapter.getCount(); i ++) {
            // Get fragment in viewpager by tag
            questionPagerFrag = (QuestionPagerFrag) getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:"
                            + R.id.viewPgActMain_Question + ":"
                            + questionPagerAdapter.getItemId(i));
            // Check if fragment is null ~ have not init yet
            if (questionPagerFrag == null) {
                allowSubmit = false;
                /**
                  * Check this question is in unchooseList or not
                  * If not, add it
                 */
                if (!unChooseList.contains(i)) {
                    unChooseList.add(i);
                }
            } else {
                // Check user has answer this question or not
                if (questionPagerFrag.getUserAnswerChoice().isEmpty()) {
                    allowSubmit = false;
                    hasChoose = false;
                    /**
                     * Check this question is in unchooseList or not
                     * If not, add it
                     */
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
                            QuestionResult resultQuestionSingle = null;
                            if (answerList.contains(userAnswerSingle)) {
                                resultQuestionSingle = new QuestionResult(questionList.get(i).getID(), questionPagerFrag.getUserAnswerChoice(), true);
                            } else {
                                resultQuestionSingle = new QuestionResult(questionList.get(i).getID(), questionPagerFrag.getUserAnswerChoice(), false);
                            }
                            questionResultList.add(resultQuestionSingle);

                            break;
                        case "Multiple":
                            boolean check = false;
                            // Check userAnswerList size and examAnswerList size
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
                            QuestionResult resultQuestionMultiple = null;
                            if (check) {
                                resultQuestionMultiple = new QuestionResult(questionList.get(i).getID(), questionPagerFrag.getUserAnswerChoice(), true);
                            } else {
                                resultQuestionMultiple = new QuestionResult(questionList.get(i).getID(), questionPagerFrag.getUserAnswerChoice(), false);
                            }
                            questionResultList.add(resultQuestionMultiple);
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
            // If still have time, not allow users to submit exam
            if (!isTimeOut) {
                showToast(getApplicationContext(), getResources().getString(R.string.warning_not_choose_answer));
            } else {
                // If time out
                // Add unchoose question to questionResultList
                for (Integer i : unChooseList) {
                    questionResultList.add(new QuestionResult(questionList.get(i).getID(), null, false));
                }
                allowSubmit = true;
            }

        }
        return allowSubmit;
    }

    /**
     * Create rate and comment dialog
     * @param dialog Comment dialog
     */
    private void rateAndComment(Dialog dialog) {
        // mapping Dialog layout
        final RatingBar rateBarStart = (RatingBar) dialog.findViewById(R.id.rateBarDialogRateComment_Stars);
        final EditText edtComment = (EditText) dialog.findViewById(R.id.edtDialogRateComment_Comment);
        Button btnNext = (Button) dialog.findViewById(R.id.btnDialogRateComment_Next);
        Button btnSkip = (Button) dialog.findViewById(R.id.btnDialogRateComment_Skip);
        // Declare exam rate start
        final Float[] rateStar = new Float[1];
        // Set exam rate start value
        rateBarStart.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rateStar[0] = v;
                Log.d("Rating", String.valueOf(rateStar[0]));
            }
        });
        // Set click listener for next button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get rating bar status
                if (rateStar[0] != null) {
                    showToast(getApplicationContext(), String.valueOf(5));
                }

                // Get comment
                String commentContent = edtComment.getText().toString();

                // Validate Input
                if (rateStar[0] == 0.0 || TextUtils.isEmpty(commentContent)) {
                    if (TextUtils.isEmpty(commentContent)) {
                        edtComment.setError(getResources().getString(R.string.error_empty_comment));
                        showToast(getApplicationContext(), getResources().getString(R.string.error_empty_comment));
                    }
                    if (rateStar[0] == 0.0) {
                        showToast(getApplicationContext(), getResources().getString(R.string.error_empty_star));
                    }
                } else {
                    Comment comment = new Comment("", userID, "", commentContent, rateStar[0]);
                    submitComment(MainAct.this, eQuizRef, examID, comment);
                }
            }
        });
        // Set click listener for skip button
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog skipProgressDialog = createProgressDialog(MainAct.this, getResources().getString(R.string.text_progress_skip));
                skipProgressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        skipProgressDialog.dismiss();
                        Intent resultIntent = new Intent(MainAct.this, ResultAct.class);
                        resultIntent.putExtra("ID", examID);
                        startActivity(resultIntent);
                    }
                }, 2000);
            }
        });

    }

    /**
     * Get exam from backend server with examID
     * @param examID string
     */
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

    /**
     * Set exam variables from dataSnapshot after get it from backend server
     * @param dataSnapshot contain exam data
     */
    private void setExam(DataSnapshot dataSnapshot) {
        exam = dataSnapshot.getValue(Exam.class);
    }

    /**
     * Get question list from backend server
     */
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

    /**
     * Set question list values from dataSnapshot after get it from backend server
     * @param dataSnapshot contain questions data
     */
    private void setQuestionList(DataSnapshot dataSnapshot) {
        for (DataSnapshot questionSnapshot: dataSnapshot.getChildren()) {
            Question question = questionSnapshot.getValue(Question.class);
            if (exam.getQuestionList().contains(question.getID())) {
                questionList.add(question);
            }
        }
    }

}
