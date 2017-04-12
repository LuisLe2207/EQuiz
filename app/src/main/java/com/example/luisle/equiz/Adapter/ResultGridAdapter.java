package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luisle.equiz.Model.QuestionResult;
import com.example.luisle.equiz.R;

import java.util.List;

import static com.example.luisle.equiz.R.id.imgGridResult_Mood;

/**
 * Created by LuisLe on 4/9/2017.
 */

public class ResultGridAdapter extends RecyclerView.Adapter<ResultGridAdapter.ResultGridViewHolder> {


    private Context myContext;
    private List<QuestionResult> resultList;
    private Integer spanCount;
    private LayoutInflater layoutInflater;

    public ResultGridAdapter(Context myContext, List<QuestionResult> resultList, StaggeredGridLayoutManager manager) {
        this.myContext = myContext;
        this.resultList = resultList;
        this.spanCount = manager.getSpanCount();
        layoutInflater = LayoutInflater.from(myContext);
    }

    @Override
    public ResultGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.grid_result, parent, false);
        return new ResultGridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResultGridAdapter.ResultGridViewHolder holder, int position) {
        QuestionResult result = resultList.get(position);
        holder.txtQuestion.append(" " + String.valueOf(position + 1));
        if (result.getBoolResult()) {
            holder.imgMood.setImageResource(R.mipmap.ic_smile);
            holder.txtResult.setText(myContext.getResources().getString(R.string.text_correct));
        } else {
            holder.imgMood.setImageResource(R.mipmap.ic_sad);
            holder.txtResult.setText(myContext.getResources().getString(R.string.text_wrong));
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

        if (result.getBoolResult()) {
            holder.cvGridResult.setCardBackgroundColor(ContextCompat.getColor(myContext, R.color.correctAnswer));
        } else {
            holder.cvGridResult.setCardBackgroundColor(ContextCompat.getColor(myContext, R.color.wrongAnswer));
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }
    public class ResultGridViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgMood;
        private TextView txtQuestion, txtResult;
        private CardView cvGridResult;
        public ResultGridViewHolder(View itemView) {
            super(itemView);
            cvGridResult = (CardView) itemView.findViewById(R.id.cvGridResult);
            imgMood = (ImageView) itemView.findViewById(imgGridResult_Mood);
            txtQuestion = (TextView) itemView.findViewById(R.id.txtGridResult_Question);
            txtResult = (TextView) itemView.findViewById(R.id.txtGridResult_Result);
        }
    }
}
