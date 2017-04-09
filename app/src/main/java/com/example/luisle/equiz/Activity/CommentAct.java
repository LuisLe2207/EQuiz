package com.example.luisle.equiz.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.luisle.equiz.Model.Comment;
import com.example.luisle.equiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.submitComment;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.R.id.btnActComment_Skip;

public class CommentAct extends AppCompatActivity {

    private RatingBar rateBarStart;
    private EditText edtComment;
    private Button btnSkip, btnNext;

    private String userID, examID;
    private Float rateStar;
    private String commentContent;
    private boolean doubleBackToExitPressedOnce = false;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_comment);

        mappingLayout();
        init();
        pressNextButton();
        pressSkipButton();
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
        rateBarStart = (RatingBar) findViewById(R.id.rateBarActComment_Stars);
        edtComment = (EditText) findViewById(R.id.edtAccComment_Comment);
        btnSkip = (Button) findViewById(btnActComment_Skip);
        btnNext = (Button) findViewById(R.id.btnActComment_Next);
    }

    private void init() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((firebaseUser != null) && !TextUtils.isEmpty(getIntent().getStringExtra("ID"))) {
                    examID = getIntent().getStringExtra("ID");
                    userID = firebaseUser.getUid();
                }
            }
        }, 1000);

        rateBarStart.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rateStar = v;
            }
        });
    }

    private void pressNextButton() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentContent = edtComment.getText().toString();
                if (validateInput(rateStar, commentContent)) {
                    Comment comment = new Comment("", userID, "", commentContent, rateStar);
                    submitComment(CommentAct.this, eQuizRef, examID, comment);
                }
            }
        });
    }

    private void pressSkipButton() {
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog skipProgressDialog = createProgressDialog(CommentAct.this, getResources().getString(R.string.text_progress_skip));
                skipProgressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        skipProgressDialog.dismiss();
                        startActivity(new Intent(CommentAct.this, HomeAct.class));
                    }
                }, 2000);
            }
        });
    }

    private boolean validateInput(Float rateStar, String comment) {
        if (TextUtils.isEmpty(comment)) {
            edtComment.setError(getResources().getString(R.string.error_empty_comment));
            showToast(getApplicationContext(), getResources().getString(R.string.error_empty_comment));
            return false;
        }
        if (rateStar == 0.0) {
            showToast(getApplicationContext(), getResources().getString(R.string.error_empty_star));
            return false;
        }
        return true;
    }
}
