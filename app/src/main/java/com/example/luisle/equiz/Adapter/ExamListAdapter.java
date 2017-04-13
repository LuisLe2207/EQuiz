package com.example.luisle.equiz.Adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Activity.HomeAct;
import com.example.luisle.equiz.Fragment.AdminExamFrag;
import com.example.luisle.equiz.Fragment.DetailExamFrag;
import com.example.luisle.equiz.Fragment.DetailUserStatisticsFrag;
import com.example.luisle.equiz.Fragment.ExamFrag;
import com.example.luisle.equiz.Fragment.HomeFrag;
import com.example.luisle.equiz.Fragment.UserStatisticsFrag;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.R;

import java.util.List;

import static com.example.luisle.equiz.MyFramework.MyEssential.inHomeFrag;
import static com.example.luisle.equiz.MyFramework.MyEssential.isAdmin;

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
        // Get exam in exam list via position
        Exam exam = examList.get(position);

        //bind data to viewholder
        holder.edtRowExam_Title.setText(exam.getTitle());
        holder.edtRowExam_Duration.setText(String.valueOf(exam.getDuration()) + "'");
        holder.edtRowExam_Questions.setText(String.valueOf(exam.getNumberOfQuestion()));
    }


    @Override
    public int getItemCount() {
        return examList.size();
    }

    class ExamListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private EditText edtRowExam_Title, edtRowExam_Duration, edtRowExam_Questions;
        public ExamListViewHolder(View itemView) {
            super(itemView);
            edtRowExam_Title = (EditText) itemView.findViewById(R.id.edtRowExam_Title);
            edtRowExam_Duration = (EditText) itemView.findViewById(R.id.edtRowExam_Duration);
            edtRowExam_Questions = (EditText) itemView.findViewById(R.id.edtRowExam_Questions);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Exam exam = examList.get(position);
            if (isAdmin) {
                FragmentManager fragmentManager = ((AdminHomeAct) myContext).getSupportFragmentManager();
                ExamFrag examFrag = ExamFrag.newInstance(exam.getID());
                AdminExamFrag adminExamFrag = (AdminExamFrag) fragmentManager.findFragmentByTag("ExamListFrag");
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                adminExamFrag.hideLayout();
                ((AdminHomeAct) myContext).getBottomNavigationView().setVisibility(View.INVISIBLE);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(android.R.id.content, examFrag).addToBackStack(null).commit();


            } else {
                FragmentManager fragmentManager = ((HomeAct) myContext).getSupportFragmentManager();
                FragmentTransaction transaction;
                if (inHomeFrag) {
                    DetailExamFrag detailExamFrag = DetailExamFrag.newInstance(exam.getID());
                    HomeFrag homeFrag = (HomeFrag) fragmentManager.findFragmentByTag("HomeFrag");
                    transaction = fragmentManager.beginTransaction();
                    homeFrag.hideLayout();
                    ((HomeAct) myContext).getBottomNavigationView().setVisibility(View.INVISIBLE);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(android.R.id.content, detailExamFrag, "DetailExamFrag").addToBackStack(null).commit();
                } else {
                    DetailUserStatisticsFrag detailUserStatisticsFrag = DetailUserStatisticsFrag.newInstance(exam.getID());
                    UserStatisticsFrag UserStatisticsFrag = (UserStatisticsFrag) fragmentManager.findFragmentByTag("UserStatisticsFrag");
                    transaction = fragmentManager.beginTransaction();
                    UserStatisticsFrag.hideLayout();
                    ((HomeAct) myContext).getBottomNavigationView().setVisibility(View.INVISIBLE);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(android.R.id.content, detailUserStatisticsFrag, "DetailUserStatisticFrag").addToBackStack(null).commit();
                }
            }

        }
    }
}
