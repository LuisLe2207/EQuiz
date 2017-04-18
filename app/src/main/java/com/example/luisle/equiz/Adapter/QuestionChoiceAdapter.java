package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.luisle.equiz.Model.Choice;
import com.example.luisle.equiz.R;

import java.util.List;

import static com.example.luisle.equiz.MyFramework.MyEssential.MAX_ANSWER;
import static com.example.luisle.equiz.MyFramework.MyEssential.choiceID;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class QuestionChoiceAdapter extends RecyclerView.Adapter<QuestionChoiceAdapter.QuestionChoiceViewHolder> {

    private List<Choice> choiceList;
    private List<Integer> answerList;
    private Context myContext;
    private LayoutInflater layoutInflater;

    public QuestionChoiceAdapter(Context myContext, List<Choice> choiceList, List<Integer> answerList) {
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
    public void onBindViewHolder(QuestionChoiceAdapter.QuestionChoiceViewHolder holder, final int position) {
        // Get exam in exam list via position
        Choice choice = choiceList.get(position);
        if (answerList.contains(choice.getID())) {
            holder.ckbRowExam_Answer.setChecked(true);
        }
        holder.ckbRowExam_Answer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Choice choice = choiceList.get(position);
                if (b) {
                    if (answerList.size() < MAX_ANSWER) {
                        answerList.add(choice.getID());
                        Log.d("Action", "Add");
                    } else {
                        showToast(myContext, myContext.getResources().getString(R.string.error_max_answer_length));
                        compoundButton.setChecked(false);
                    }
                }
                if (!b){
                    if (answerList.contains(choice.getID())) {
                        answerList.remove(answerList.indexOf(choice.getID()));
                        Log.d("Action", "Remove");
                    }
                }
            }
        });
        //bind data to viewholder
        holder.txtRowChoice_Title.setText(choice.getContent());
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
            setIsRecyclable(false);
            txtRowChoice_Title = (TextView) itemView.findViewById(R.id.txtRowChoice_Title);
            ckbRowExam_Answer = (CheckBox) itemView.findViewById(R.id.ckbRowChoice_Answer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (ckbRowExam_Answer.isChecked()) {
                showToast(myContext, "Please uncheck choice as answer before delete");
            } else {
                int position = getLayoutPosition();
                choiceList.remove(position);
                choiceID--;
                showToast(myContext, String.valueOf(choiceID));
                notifyDataSetChanged();
            }

        }
    }

}
