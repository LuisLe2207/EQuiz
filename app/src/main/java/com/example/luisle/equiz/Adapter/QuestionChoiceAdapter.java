package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.luisle.equiz.R;

import java.util.List;

import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class QuestionChoiceAdapter extends RecyclerView.Adapter<QuestionChoiceAdapter.QuestionChoiceViewHolder> {

    private List<String> choiceList;
    private List<String> answerList;
    private Context myContext;
    private LayoutInflater layoutInflater;

    public QuestionChoiceAdapter(Context myContext, List<String> choiceList, List<String> answerList) {
        this.myContext = myContext;
        this.choiceList = choiceList;
        this.answerList = answerList;
        layoutInflater = LayoutInflater.from(myContext);
    }

    @Override
    public QuestionChoiceAdapter.QuestionChoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_choice, parent, false);
        return new QuestionChoiceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuestionChoiceAdapter.QuestionChoiceViewHolder holder, int position) {
        // Get exam in exam list via position
        String choice = choiceList.get(position);
        if (answerList.contains(choice)) {
            holder.ckbRowExam_Answer.setChecked(true);
        }
        //bind data to viewholder
        holder.txtRowChoice_Title.setText(choice);

    }

    @Override
    public int getItemCount() {
        return choiceList.size();
    }

    class QuestionChoiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView txtRowChoice_Title;
        private CheckBox ckbRowExam_Answer;
        public QuestionChoiceViewHolder(View itemView) {
            super(itemView);
            txtRowChoice_Title = (TextView) itemView.findViewById(R.id.txtRowChoice_Title);
            ckbRowExam_Answer = (CheckBox) itemView.findViewById(R.id.ckbRowChoice_Answer);
            ckbRowExam_Answer.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            String choice = choiceList.get(position);
            showToast(myContext, choice);
        }
    }
}
