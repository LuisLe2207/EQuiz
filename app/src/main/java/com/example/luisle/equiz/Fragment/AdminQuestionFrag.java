package com.example.luisle.equiz.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Adapter.QuestionListAdapter;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getQuestions;
import static com.example.luisle.equiz.MyFramework.MyEssential.allowModify;
import static com.example.luisle.equiz.MyFramework.MyEssential.dialogOnScreen;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class AdminQuestionFrag extends Fragment {

    private RecyclerView rcvQuestion;
    private ProgressBar pgBarLoading;
    private FloatingActionButton fabQuestionAdd;
    private QuestionListAdapter questionListAdapter;
    private ArrayList<Question> questionList;


    // TODO: Rename and change types and number of parameters
    public static AdminQuestionFrag newInstance() {
        AdminQuestionFrag fragment = new AdminQuestionFrag();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_question_admin, container, false);
        mappingLayout(view);
        init();
        openAddQuestionDialog();
        return view;
    }


    private void init() {
        rcvQuestion.setVisibility(View.INVISIBLE);
        pgBarLoading.setVisibility(View.VISIBLE);
        questionList = new ArrayList<>();
        questionListAdapter = new QuestionListAdapter(getContext(), questionList, true);
        getQuestions(eQuizRef, rcvQuestion, pgBarLoading, questionList, questionListAdapter);
        rcvQuestion.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }


    private void mappingLayout(View view) {
        rcvQuestion = (RecyclerView) view.findViewById(R.id.rcvQuestionAdmin);
        pgBarLoading = (ProgressBar) view.findViewById(R.id.pgBarFragQuestionAdmin_Loading);
        fabQuestionAdd = (FloatingActionButton) view.findViewById(R.id.fabFragQuestionAdd);
    }

    private void openAddQuestionDialog() {
        fabQuestionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allowModify) {
                    dialogOnScreen = true;
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    QuestionFrag questionFrag = QuestionFrag.newInstance("");
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    hideLayout();
                    transaction.add(android.R.id.content, questionFrag).addToBackStack(null).commit();
                } else {
                    showToast(getContext(),
                                      getContext().getResources().getString(R.string.alert_you_must_create_push_notification)
                                    + " "
                                    + getContext().getResources().getString(R.string.text_create));
                }

            }
        });
    }


    public void hideLayout() {
        fabQuestionAdd.hide();
        rcvQuestion.setVisibility(View.INVISIBLE);
        ((AdminHomeAct) getActivity()).getBottomNavigationView().setVisibility(View.INVISIBLE);
    }

    public void showLayout() {
        fabQuestionAdd.show();
        rcvQuestion.setVisibility(View.VISIBLE);
    }
}
