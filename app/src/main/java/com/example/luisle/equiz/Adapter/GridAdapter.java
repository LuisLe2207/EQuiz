package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.Model.QuestionResult;
import com.example.luisle.equiz.R;

import java.util.List;

import static com.example.luisle.equiz.R.id.imgGridResult_Mood;

/**
 * Created by LuisLe on 4/9/2017.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ResultGridViewHolder> {


    private Context myContext;
    private List<QuestionResult> resultList;
    private List<Integer> answerList;
    private Integer questions;
    private Integer spanCount;
    private LayoutInflater layoutInflater;
    private String type;
    private getQuestionPosition getQuestionPosition;

    public GridAdapter(Context myContext, List<QuestionResult> resultList, List<Integer> answerList, List<Question> questionList, StaggeredGridLayoutManager manager, String type) {
        this.myContext = myContext;
        this.resultList = resultList;
        this.answerList = answerList;
        if (questionList != null) {
            questions = questionList.size();
        }
        this.spanCount = manager.getSpanCount();
        this.type = type;
        layoutInflater = LayoutInflater.from(myContext);
        if (TextUtils.equals(type, "Question")) {
            getQuestionPosition = (getQuestionPosition) myContext;
        }
    }

    @Override
    public ResultGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (type) {
            case "Result":
                itemView = layoutInflater.inflate(R.layout.grid_result, parent, false);
                break;
            case "Question":
                itemView = layoutInflater.inflate(R.layout.grid_question, parent, false);
                break;
        }
        return new ResultGridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GridAdapter.ResultGridViewHolder holder, int position) {
        switch (type) {
            case "Result":
                QuestionResult result = resultList.get(position);
                holder.txtQuestion.append(" " + String.valueOf(position + 1));
                if (result.getBoolResult()) {
                    holder.imgMood.setImageResource(R.mipmap.ic_smile);
                    holder.txtResult.setText(myContext.getResources().getString(R.string.text_correct));
                } else {
                    holder.imgMood.setImageResource(R.mipmap.ic_sad);
                    holder.txtResult.setText(myContext.getResources().getString(R.string.text_wrong));
                }

                if (result.getBoolResult()) {
                    holder.cvGridResult.setCardBackgroundColor(ContextCompat.getColor(myContext, R.color.correctAnswer));
                } else {
                    holder.cvGridResult.setCardBackgroundColor(ContextCompat.getColor(myContext, R.color.wrongAnswer));
                }
                break;
            case "Question":
                if (answerList.contains(position)) {
                    holder.cvGridQuestion.setCardBackgroundColor(ContextCompat.getColor(myContext, R.color.correctAnswer));
                } else {
                    holder.cvGridQuestion.setCardBackgroundColor(ContextCompat.getColor(myContext, R.color.wrongAnswer));
                }
                holder.txtQuestion.append(" " + (position + 1));
                break;
        }

        final ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams staggLayoutParams = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            if (getItemCount() % spanCount != 0) {
                int lastPos = getItemCount() - 1;
                if (position == lastPos) {
                    staggLayoutParams.setFullSpan(true);
                    holder.itemView.setLayoutParams(staggLayoutParams);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (TextUtils.equals(type, "Result")) {
            return resultList.size();
        } else {
            return questions;
        }
    }


    public class ResultGridViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgMood;
        private TextView txtQuestion, txtResult;
        private CardView cvGridResult, cvGridQuestion;
        public ResultGridViewHolder(View itemView) {
            super(itemView);
            switch (type) {
                case "Result":
                    cvGridResult = (CardView) itemView.findViewById(R.id.cvGridResult);
                    imgMood = (ImageView) itemView.findViewById(imgGridResult_Mood);
                    txtQuestion = (TextView) itemView.findViewById(R.id.txtGridResult_Question);
                    txtResult = (TextView) itemView.findViewById(R.id.txtGridResult_Result);
                    break;
                case "Question":
                    cvGridQuestion = (CardView) itemView.findViewById(R.id.cvGridQuestion);
                    txtQuestion = (TextView) itemView.findViewById(R.id.txtGridQuestion_QuestionTH);
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getQuestionPosition.getQuestionPosition(getLayoutPosition());
                        }
                    });
                    break;
            }
        }
    }

    public interface getQuestionPosition {
        void getQuestionPosition(Integer position);
    }
}
