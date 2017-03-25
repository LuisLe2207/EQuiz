package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.R;

import java.util.List;

/**
 * Created by LuisLe on 3/4/2017.
 */

public class ExamListAdapter extends RecyclerView.Adapter<ExamListAdapter.ExamListViewHolder> {

    private List<Exam> examList;
    private Context myContext;
    private LayoutInflater layoutInflater;

    public ExamListAdapter(Context myContext, List<Exam> examList) {
        this.myContext = myContext;
        this.examList = examList;
        layoutInflater = LayoutInflater.from(myContext);
    }

    @Override
    public ExamListAdapter.ExamListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.row_exam, parent, false);
        return new ExamListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ExamListAdapter.ExamListViewHolder holder, int position) {
        // Get examin exam list via position
        Exam exam = examList.get(position);

        //bind data to viewholder
        holder.txtRowExam_Title.setText(exam.getTitle());
        holder.txtRowExam_Duration.setText(String.valueOf(exam.getDuration()));
    }


    @Override
    public int getItemCount() {
        return examList.size();
    }

    class ExamListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView txtRowExam_Title;
        private TextView txtRowExam_Duration;
        public ExamListViewHolder(View itemView) {
            super(itemView);
            txtRowExam_Title = (TextView) itemView.findViewById(R.id.txtRowExam_Title);
            txtRowExam_Duration = (TextView) itemView.findViewById(R.id.txtRowExam_Duration);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Exam exam = examList.get(position);
            Toast.makeText(myContext, txtRowExam_Title.getText(), Toast.LENGTH_LONG).show();
        }
    }
}
