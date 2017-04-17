package com.example.luisle.equiz.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.Button;
import android.widget.EditText;

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Adapter.QuestionChoiceAdapter;
import com.example.luisle.equiz.Model.Choice;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getQuestion;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.saveQuestion;
import static com.example.luisle.equiz.MyFramework.MyEssential.MAX_CHOICE;
import static com.example.luisle.equiz.MyFramework.MyEssential.MIN_CHOICE;
import static com.example.luisle.equiz.MyFramework.MyEssential.allowModify;
import static com.example.luisle.equiz.MyFramework.MyEssential.choiceID;
import static com.example.luisle.equiz.MyFramework.MyEssential.dialogOnScreen;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.isDelete;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;


/**
 * Created by LuisLe on 3/26/2017.
 */

public class QuestionFrag extends DialogFragment {

    // Layout
    EditText edtQuestionTitle, edtQuestionChoice;
    Button btnAddChoice;
    RecyclerView rcvQuestion;

    // Fragment Variables
    private ArrayList<Choice> choiceList;
    private ArrayList<Integer> answerList;
    private QuestionChoiceAdapter questionChoiceAdapter;
    private String questionID;

    public static QuestionFrag newInstance(String questionID) {
        QuestionFrag fragment = new QuestionFrag();
        Bundle args = new Bundle();
        args.putString("ID", questionID);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_question, container, false);

        mappingLayout(view);
        createActionBar(view);

        if (getArguments() != null) {
            questionID = getArguments().getString("ID");
        }

        init();
        addChoice();



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
        if (TextUtils.isEmpty(questionID)) {
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
                if (allowModify) {
                    String title = edtQuestionTitle.getText().toString().trim();
                    String questionType = "";
                    if (validateBeforeSave(choiceList, answerList, title)) {
                        if (answerList.size() == 1) {
                            questionType = "Single";
                        } else {
                            questionType = "Multiple";
                        }
                        Question newQuestion = new Question("", title, questionType, choiceList, answerList);
                        if (TextUtils.isEmpty(questionID)) {
                            saveQuestion(getContext(), eQuizRef, newQuestion, "");
                        } else {
                            saveQuestion(getContext(), eQuizRef, newQuestion, questionID);
                        }

                        choiceID = 1;
                    }
                } else {
                    String action = "";
                    if (TextUtils.isEmpty(questionID)) {
                        action = getContext().getResources().getString(R.string.toolbar_add_question);
                    } else {
                        action = getContext().getResources().getString(R.string.toolbar_modify_question);
                    }
                    showToast(getContext(),
                                      getContext().getResources().getString(R.string.alert_you_must_create_push_notification)
                                    + " "
                                    + action);
                }

                break;
            case android.R.id.home:
                dismiss();
                choiceID = 1;
                AdminQuestionFrag adminQuestionFrag = (AdminQuestionFrag) getActivity()
                                                    .getSupportFragmentManager()
                                                    .findFragmentByTag("QuestionListFrag");
                adminQuestionFrag.showLayout();
                ((AdminHomeAct) getActivity()).getBottomNavigationView().setVisibility(View.VISIBLE);
                dialogOnScreen = false;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mappingLayout(View view) {
        edtQuestionTitle = (EditText) view.findViewById(R.id.edtDialog_AddQuestion_Title);
        edtQuestionChoice = (EditText) view.findViewById(R.id.edtDialog_AddQuestion_Choice);
        btnAddChoice = (Button) view.findViewById(R.id.btnDialog_AddQuestion_AddChoice);
        rcvQuestion = (RecyclerView) view.findViewById(R.id.rcvDialogAddQuestion);
    }

    private void createActionBar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarDialogAddQuestion);
        if (TextUtils.isEmpty(questionID)) {
            toolbar.setTitle(getContext().getResources().getString(R.string.toolbar_add_question));
        } else {
            toolbar.setTitle(getContext().getResources().getString(R.string.toolbar_modify_question));
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

    private void init() {
        choiceList = new ArrayList<>();
        answerList = new ArrayList<>();
        questionChoiceAdapter = new QuestionChoiceAdapter(getContext(), choiceList, answerList);
        if (!TextUtils.isEmpty(questionID)) {
            getQuestion(eQuizRef, questionID, choiceList, answerList, edtQuestionTitle, questionChoiceAdapter);
        }
        rcvQuestion.setAdapter(questionChoiceAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvQuestion.setLayoutManager(linearLayoutManager);
    }

    private void addChoice() {
        btnAddChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String choiceContent = edtQuestionChoice.getText().toString().trim();
                if (TextUtils.isEmpty(choiceContent)) {
                    if (TextUtils.isEmpty(choiceContent)) {
                        edtQuestionChoice.setError(getResources().getString(R.string.error_choice_not_fill));
                    }

                } else {
                    if (choiceList.size() < MAX_CHOICE ) {
                        Choice newChoice = new Choice(choiceID, choiceContent);
                        isDelete = false;
                        choiceList.add(newChoice);
                        questionChoiceAdapter.notifyDataSetChanged();
                        rcvQuestion.invalidate();
                        choiceID++;
                    } else {
                        showToast(getContext(),
                                getResources().getString(R.string.error_max_choice_length));
                    }
                }
            }
        });
    }

    private boolean validateBeforeSave(ArrayList<Choice> choiceList, ArrayList<Integer> answerList, String title) {
        if (choiceList.isEmpty() || choiceList.size() < MIN_CHOICE) {
            showToast(getContext(), getResources().getString(R.string.error_min_choice_length));
            return false;
        }
        if (answerList.isEmpty()) {
            showToast(getContext(), getResources().getString(R.string.error_min_answer_length));
            return false;
        }
        if (TextUtils.isEmpty(title)) {
            edtQuestionTitle.setError(getResources().getString(R.string.error_title_not_fill));
            return false;
        }
        return true;
    }
}
