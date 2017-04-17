package com.example.luisle.equiz.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Adapter.QuestionListAdapter;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getExam;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.getQuestions;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.saveExam;
import static com.example.luisle.equiz.MyFramework.MyEssential.MAX_QUESTION;
import static com.example.luisle.equiz.MyFramework.MyEssential.MIN_QUESTTION;
import static com.example.luisle.equiz.MyFramework.MyEssential.allowMaintain;
import static com.example.luisle.equiz.MyFramework.MyEssential.dialogOnScreen;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.inAddExamDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class ExamFrag extends DialogFragment {
    // Layout
    private EditText edtExamTitle;
    private RadioGroup rdGrpDuration;
    private RecyclerView rcvQuestion;
    private ProgressBar pgBarLoading;


    // Fragment Variables
    private ArrayList<Question> questionList;
    private ArrayList<String> examQuestionList;
    private QuestionListAdapter questionListAdapter;
    private String examID;
    private Integer examDuration = 3;

    public static ExamFrag newInstance(String examID) {
        ExamFrag fragment = new ExamFrag();
        Bundle args = new Bundle();
        args.putString("ID", examID);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_exam, container, false);

        mappingLayout(view);
        createActionBar(view);
        setExamDuration();
        if (getArguments() != null) {
            examID = getArguments().getString("ID");
        }
        init();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (TextUtils.isEmpty(examID)) {
            MenuItem createActionItem = menu.add(1,333,1, getResources().getString(R.string.text_create));
            createActionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            MenuItem createActionItem = menu.add(1,333,1, getResources().getString(R.string.text_modify));
            createActionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 333:
                if (TextUtils.isEmpty(examID)) {
                    submitAction();
                } else {
                    if (allowMaintain) {
                        submitAction();
                    } else {
                        String action = getContext().getResources().getString(R.string.text_modify);
                        showToast(getContext(),
                                getContext().getResources().getString(R.string.alert_you_must_create_push_notification)
                                        + " "
                                        + action);
                    }
                }
                break;
            case android.R.id.home:
                dismiss();
                FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
                AdminExamFrag adminExamFrag1 = (AdminExamFrag) fragmentManager.findFragmentByTag("ExamListFrag");
                adminExamFrag1.showLayout();
                ((AdminHomeAct) getActivity()).getBottomNavigationView().setVisibility(View.VISIBLE);
                dialogOnScreen = false;
                inAddExamDialog = false;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mappingLayout(View view) {
        edtExamTitle = (EditText) view.findViewById(R.id.edtDialog_AddExam_Title);
        rdGrpDuration = (RadioGroup) view.findViewById(R.id.rdGrpDialog_AddExam_Duration);
        rcvQuestion = (RecyclerView) view.findViewById(R.id.rcvDialogAddExam_Question);
        pgBarLoading = (ProgressBar) view.findViewById(R.id.pgBarDialogAddExam_LoadingQuestion);
    }

    private void init() {
        pgBarLoading.setVisibility(View.VISIBLE);
        rcvQuestion.setVisibility(View.INVISIBLE);
        questionList = new ArrayList<>();
        examQuestionList = new ArrayList<>();
        questionListAdapter = new QuestionListAdapter(getContext(), questionList, examQuestionList, false);
        getQuestions(eQuizRef, rcvQuestion, pgBarLoading, questionList, questionListAdapter);
        if (!TextUtils.isEmpty(examID)) {
            getExam(eQuizRef, examID, examQuestionList, edtExamTitle, questionListAdapter);
        }
        rcvQuestion.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void createActionBar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarDialogAddExam);
        if (TextUtils.isEmpty(examID)) {
            toolbar.setTitle(getContext().getResources().getString(R.string.toolbar_add_exam));
        } else {
            toolbar.setTitle(getContext().getResources().getString(R.string.toolbar_modify_exam));
        }


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        setHasOptionsMenu(true);
    }

    private void submitAction() {
        String examTitle = edtExamTitle.getText().toString().trim();
        if (validateBeforeSave(examTitle, examQuestionList)) {
            Integer numberQuestion = examQuestionList.size();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            final String createDate = simpleDateFormat.format(calendar.getTime());
            Exam newExam = new Exam("", examTitle, examQuestionList, numberQuestion, examDuration, createDate, "");
            saveExam(getContext(), eQuizRef, newExam, examID);
        }
    }

    private void setExamDuration() {
        rdGrpDuration.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.rdBtnDialog_AddExam_Duration3:
                        examDuration = 3;
                        break;
                    case R.id.rdBtnDialog_AddExam_Duration7:
                        examDuration = 7;
                        break;
                    case R.id.rdBtnDialog_AddExam_Duration15:
                        examDuration = 15;
                        break;
                    case R.id.rdBtnDialog_AddExam_Duration20:
                        examDuration = 20;
                        break;
                }
            }
        });
    }

    private boolean validateBeforeSave(String title, ArrayList<String> examQuestionList) {
        if (TextUtils.isEmpty(title)) {
            edtExamTitle.setError(getResources().getString(R.string.error_title_not_fill));
            return false;
        }

        if (examQuestionList.size() < MIN_QUESTTION || examQuestionList.size() > MAX_QUESTION) {
            if (examQuestionList.size() < MIN_QUESTTION) {
                showToast(getContext(), getResources().getString(R.string.error_min_question_length));
                return false;
            }

            if (examQuestionList.size() > MAX_QUESTION) {
                showToast(getContext(), getResources().getString(R.string.error_max_question_length));
                return false;
            }
        }
        return true;
    }

}
