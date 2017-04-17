package com.example.luisle.equiz.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.luisle.equiz.Activity.ResultAct;
import com.example.luisle.equiz.Model.ExamResult;
import com.example.luisle.equiz.MyFramework.MyEssential;
import com.example.luisle.equiz.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LuisLe on 4/13/2017.
 */

public class ExamResultListAdapter extends RecyclerView.Adapter<ExamResultListAdapter.ExamResultListViewHolder> {

    private Context myContext;
    private String examID;
    private List<ExamResult> examResultList;
    private LayoutInflater layoutInflater;

    public ExamResultListAdapter(Context myContext, List<ExamResult> examResultList, String examID) {
        this.myContext = myContext;
        this.examResultList = examResultList;
        this.examID = examID;
        layoutInflater = LayoutInflater.from(myContext);
    }


    @Override
    public ExamResultListAdapter.ExamResultListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_exam_result, parent, false);
        return new ExamResultListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExamResultListAdapter.ExamResultListViewHolder holder, int position) {
        ExamResult examResult = examResultList.get(position);
        holder.edtCorrectAnswer.append(String.valueOf(examResult.getCorrectAnswer()) + "/" + String.valueOf(examResult.getQuestionResults().size()));
        holder.edtCompleteTime.append(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(examResult.getCompleteTime()),
                TimeUnit.MILLISECONDS.toSeconds(examResult.getCompleteTime()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(examResult.getCompleteTime()))));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(examResult.getID()));
        holder.edtDoneDate.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public int getItemCount() {
        return examResultList.size();
    }

    public class ExamResultListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private EditText edtCorrectAnswer, edtCompleteTime, edtDoneDate;
        public ExamResultListViewHolder(View itemView) {
            super(itemView);
            edtCorrectAnswer = (EditText) itemView.findViewById(R.id.edtRowExamResult_CorrectAnswer);
            edtCompleteTime = (EditText) itemView.findViewById(R.id.edtRowExamResult_CompleteTime);
            edtDoneDate = (EditText) itemView.findViewById(R.id.edtRowExamResult_DoneDate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final ProgressDialog openResultActProgressDialog = MyEssential.createProgressDialog(myContext,
                    myContext.getResources().getString(R.string.text_progress_moving));
            String examResultID = examResultList.get(getLayoutPosition()).getID();
            Bundle iDBundle = new Bundle();
            iDBundle.putString("examID", examID);
            iDBundle.putString("examResultID", examResultID);
            final Intent resultIntent = new Intent(myContext, ResultAct.class);
            resultIntent.putExtra("ID", iDBundle);
            openResultActProgressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openResultActProgressDialog.dismiss();
                    myContext.startActivity(resultIntent);
                }
            }, 2000);

        }
    }

}
