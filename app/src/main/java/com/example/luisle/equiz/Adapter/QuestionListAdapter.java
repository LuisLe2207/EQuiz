package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Fragment.AdminQuestionFrag;
import com.example.luisle.equiz.Fragment.QuestionFrag;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.R;

import java.util.List;

import static com.example.luisle.equiz.MyFramework.MyEssential.isAdmin;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.QuestionListViewHolder> {

    private List<Question> questionsList;
    private List<String> examQuestionList;
    private Context myContext;
    private boolean inAdminQuestion;
    private LayoutInflater layoutInflater;

    public QuestionListAdapter(Context myContext, List<Question> questionsList, Boolean inAdminQuestion) {
        this.myContext = myContext;
        this.questionsList = questionsList;
        this.inAdminQuestion = inAdminQuestion;
        layoutInflater = LayoutInflater.from(myContext);
    }

    public QuestionListAdapter(Context myContext, List<Question> questionsList, List<String> examQuestionList, Boolean inAdminQuestion) {
        this.myContext = myContext;
        this.questionsList = questionsList;
        this.examQuestionList = examQuestionList;
        this.inAdminQuestion = inAdminQuestion;
        layoutInflater = LayoutInflater.from(myContext);
    }

    @Override
    public QuestionListAdapter.QuestionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (inAdminQuestion) {
            itemView = layoutInflater.inflate(R.layout.row_question, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.row_question_checkbox, parent, false);
        }

        return new QuestionListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuestionListAdapter.QuestionListViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        // Get exam in exam list via position
        final Question question = questionsList.get(position);

        //bind data to viewholder
        holder.edtRowQuestion_Title.setText(question.getTitle());
        holder.edtRowQuestion_Type.setText(question.getQuestionType());
        holder.edtRowQuestion_Choice.setText(String.valueOf(question.getChoiceList().size()));
        holder.edtRowQuestion_Answer.setText(String.valueOf(question.getAnswerList().size()));

        if (!inAdminQuestion) {
            if (examQuestionList.contains(question.getID())) {
                holder.ckbRowQuestion_Question.setChecked(true);
            }

            // Set checked change listener for each checkbox
            holder.ckbRowQuestion_Question.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        examQuestionList.add(question.getID());

                    } else {
                        if (examQuestionList.contains(question.getID())) {
                            examQuestionList.remove(examQuestionList.indexOf(question.getID()));
                        }
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    class QuestionListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private EditText edtRowQuestion_Title, edtRowQuestion_Type, edtRowQuestion_Choice, edtRowQuestion_Answer;
        private CheckBox ckbRowQuestion_Question;
        public QuestionListViewHolder(View itemView) {
            super(itemView);
            edtRowQuestion_Title = (EditText) itemView.findViewById(R.id.edtRowQuestion_Title);
            edtRowQuestion_Type = (EditText) itemView.findViewById(R.id.edtRowQuestion_Type);
            edtRowQuestion_Choice = (EditText) itemView.findViewById(R.id.edtRowQuestion_Choice);
            edtRowQuestion_Answer = (EditText) itemView.findViewById(R.id.edtRowQuestion_Answer);

            if (!inAdminQuestion) {
                ckbRowQuestion_Question = (CheckBox) itemView.findViewById(R.id.ckbRowQuestion_Question);
            }

            // Add click listener for each item view
            if (inAdminQuestion) {
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Question question = questionsList.get(position);
            if (isAdmin) {
                FragmentManager fragmentManager = ((AdminHomeAct) myContext).getSupportFragmentManager();
                QuestionFrag questionFrag = QuestionFrag.newInstance(question.getID());
                AdminQuestionFrag questionExamFrag = (AdminQuestionFrag) fragmentManager.findFragmentByTag("QuestionListFrag");
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                questionExamFrag.hideLayout();
                ((AdminHomeAct) myContext).getBottomNavigationView().setVisibility(View.INVISIBLE);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(android.R.id.content, questionFrag).addToBackStack(null).commit();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            showToast(myContext, "Hello");
            return true;
        }
    }

}
