package com.example.luisle.equiz.Fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.luisle.equiz.Model.Choice;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.MyEssential.EXAM_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.MAX_ANSWER;
import static com.example.luisle.equiz.MyFramework.MyEssential.QUESTION_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 4/6/2017.
 */

public class QuestionPagerFrag extends Fragment {


    private TextView txtPageQuestion_QuestionTh, txtPageQuestion_Title;
    private LinearLayout linearLayoutPageQuestion_Choice;

    private ArrayList<Question> questionList;
    private ArrayList<Integer> userAnswerChoice;


    private String examID;
    private Integer position;

    public static QuestionPagerFrag newInstance(String examID, Integer position) {
        QuestionPagerFrag fragment = new QuestionPagerFrag();
        Bundle args = new Bundle();
        args.putString("ID", examID);
        args.putInt("Position", position);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_question, container, false);

        if (getArguments() != null) {
            examID = getArguments().getString("ID");
            position = getArguments().getInt("Position");
        }
        mappingLayout(view);
        init();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userAnswerChoice = new ArrayList<>();
    }

    private void mappingLayout(View view) {
        txtPageQuestion_QuestionTh = (TextView) view.findViewById(R.id.txtPageQuestion_QuestionTh);
        txtPageQuestion_Title = (TextView) view.findViewById(R.id.txtPageQuestion_Title);
        linearLayoutPageQuestion_Choice = (LinearLayout) view.findViewById(R.id.linearLayoutPageQuestion_Choice);
    }


    private void init() {
        questionList = new ArrayList<>();
        getQuestions();
    }

    private void getQuestions() {
        final ArrayList<String> questionIDList = new ArrayList<>();
        eQuizRef.child(EXAM_CHILD).child(examID).child("questionList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot questionIDSnapshot: dataSnapshot.getChildren()) {
                    questionIDList.add(questionIDSnapshot.getValue().toString());
                }
                eQuizRef.child(QUESTION_CHILD).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot questionSnapshot: dataSnapshot.getChildren()) {
                            Question question = questionSnapshot.getValue(Question.class);
                            if (questionIDList.contains(question.getID())) {
                                questionList.add(question);
                            }
                        }
                        createChoiceLayout(questionList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private  void createChoiceLayout(ArrayList<Question> questionList) {
        txtPageQuestion_QuestionTh.append(" " + (position + 1)) ;
        txtPageQuestion_Title.setText(questionList.get(position).getTitle());
        String questionType = questionList.get(position).getQuestionType();
        final ArrayList<Choice> choiceList = new ArrayList<>();
        choiceList.addAll(questionList.get(position).getChoiceList());
        switch (questionType) {
            case "Single":
                RadioGroup rdgChoice = new RadioGroup(getContext());
                for (int i = 0; i < choiceList.size(); i++) {
                    RadioButton rbChoice = new RadioButton(getContext());
                    rbChoice.setText(choiceList.get(i).getContent());
                    rbChoice.setId(choiceList.get(i).getID());
                    if (! userAnswerChoice.isEmpty() && userAnswerChoice.contains(rbChoice.getId())) {
                        rbChoice.setChecked(true);
                    }
                    rdgChoice.addView(rbChoice);
                }
                linearLayoutPageQuestion_Choice.addView(rdgChoice);
                rdgChoice.setOnCheckedChangeListener(chooseAnswer(rdgChoice));
                break;
            case "Multiple":
                for (int i = 0 ; i < choiceList.size(); i ++) {
                    CheckBox ckbChoice = new CheckBox(getContext());
                    ckbChoice.setId(choiceList.get(i).getID());
                    ckbChoice.setText(choiceList.get(i).getContent());
                    if (!userAnswerChoice.isEmpty() && userAnswerChoice.contains(ckbChoice.getId())) {
                        ckbChoice.setChecked(true);
                    }
                    linearLayoutPageQuestion_Choice.addView(ckbChoice);
                    ckbChoice.setOnCheckedChangeListener(chooseAnswer(ckbChoice));
                }
                break;
        }
    }

    CompoundButton.OnCheckedChangeListener chooseAnswer(final CheckBox checkBox) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (userAnswerChoice.size() < MAX_ANSWER) {
                        if (!userAnswerChoice.contains(checkBox.getId())) {
                            userAnswerChoice.add(checkBox.getId());
                        }
                    } else {
                        showToast(getContext(), getContext().getResources().getString(R.string.error_max_answer_length));
                        checkBox.setChecked(false);
                    }
                    //showToast(getContext(), String.valueOf(userAnswerChoice.size()));
                }
                else {
                    if (userAnswerChoice.size() == 1) {
                        showToast(getContext(), getContext().getResources().getString(R.string.error_min_answer_length));
                        checkBox.setChecked(true);
                    } else {
                        if (userAnswerChoice.contains(checkBox.getId())) {
                            userAnswerChoice.remove(userAnswerChoice.indexOf(checkBox.getId()));
                        }
                    }

                    //showToast(getContext(), String.valueOf(userAnswerChoice.size()));
                }
            }
        };
    }

    RadioGroup.OnCheckedChangeListener chooseAnswer(final RadioGroup radioGroup) {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                int rbChoiceID = radioGroup.getCheckedRadioButtonId();
                userAnswerChoice.clear();
                userAnswerChoice.add(rbChoiceID);
                //showToast(getContext(), String.valueOf(userAnswerChoice.size()));
            }
        };
    }

    public ArrayList<Integer> getUserAnswerChoice() {
        return userAnswerChoice;
    }

}
