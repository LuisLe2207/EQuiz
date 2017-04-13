package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.luisle.equiz.Model.ExamResult;
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
    private List<ExamResult> examResultList;
    private LayoutInflater layoutInflater;

    public ExamResultListAdapter(Context myContext, List<ExamResult> examResultList) {
        this.myContext = myContext;
        this.examResultList = examResultList;
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

    public class ExamResultListViewHolder extends RecyclerView.ViewHolder {
        private EditText edtCorrectAnswer, edtCompleteTime, edtDoneDate;
        public ExamResultListViewHolder(View itemView) {
            super(itemView);
            edtCorrectAnswer = (EditText) itemView.findViewById(R.id.edtRowExamResult_CorrectAnswer);
            edtCompleteTime = (EditText) itemView.findViewById(R.id.edtRowExamResult_CompleteTime);
            edtDoneDate = (EditText) itemView.findViewById(R.id.edtRowExamResult_DoneDate);
        }
    }
}
