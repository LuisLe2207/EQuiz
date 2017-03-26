package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luisle.equiz.Activity.MainAct;
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
    private Context myContext;
    private LayoutInflater layoutInflater;

    public QuestionListAdapter(Context myContext, List<Question> questionsList) {
        this.myContext = myContext;
        this.questionsList = questionsList;
        layoutInflater = LayoutInflater.from(myContext);
    }

    @Override
    public QuestionListAdapter.QuestionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_question, parent, false);
        return new QuestionListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuestionListAdapter.QuestionListViewHolder holder, int position) {
        // Get exam in exam list via position
        Question question = questionsList.get(position);

        //bind data to viewholder
        holder.txtRowQuestion_Title.setText(question.getTitle());
    }


    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    class QuestionListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView txtRowQuestion_Title;
        public QuestionListViewHolder(View itemView) {
            super(itemView);
            txtRowQuestion_Title = (TextView) itemView.findViewById(R.id.txtRowQuestion_Title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Question question = questionsList.get(position);
            if (isAdmin) {
                showToast(myContext, question.getTitle());
            } else {
                Intent mainIntent = new Intent(myContext, MainAct.class);
                mainIntent.putExtra("EXAM_ID", question.getID());
                myContext.startActivity(mainIntent);
            }

        }
    }

}
