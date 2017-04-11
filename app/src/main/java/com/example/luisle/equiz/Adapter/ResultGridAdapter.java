package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luisle.equiz.Model.Result;
import com.example.luisle.equiz.R;

import java.util.List;

import static com.example.luisle.equiz.R.id.imgGridResult_Mood;

/**
 * Created by LuisLe on 4/9/2017.
 */

public class ResultGridAdapter extends RecyclerView.Adapter<ResultGridAdapter.ResultGridViewHolder> {


    private Context myContext;
    private List<Result> resultList;
    private Integer spanCount;
    private LayoutInflater layoutInflater;

    public ResultGridAdapter(Context myContext, List<Result> resultList, StaggeredGridLayoutManager manager) {
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
    public void onBindViewHolder(ResultGridViewHolder holder, int position) {
        Result result = resultList.get(position);
        txtQuestion.append(" " + String.valueOf(position + 1));
        if (result.getBoolResult()) {
            imgMood.setImageResource(R.mipmap.ic_smile);
            txtResult.setText(myContext.getResources().getString(R.string.text_correct));
        } else {
            imgMood.setImageResource(R.mipmap.ic_sad);
            txtResult.setText(myContext.getResources().getString(R.string.text_wrong));
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
        return resultList.size();
    }
    private ImageView imgMood;
    private TextView txtQuestion, txtResult;
    public class ResultGridViewHolder extends RecyclerView.ViewHolder {

        public ResultGridViewHolder(View itemView) {
            super(itemView);
            imgMood = (ImageView) itemView.findViewById(imgGridResult_Mood);
            txtQuestion = (TextView) itemView.findViewById(R.id.txtGridResult_Question);
            txtResult = (TextView) itemView.findViewById(R.id.txtGridResult_Result);
        }
    }
}
