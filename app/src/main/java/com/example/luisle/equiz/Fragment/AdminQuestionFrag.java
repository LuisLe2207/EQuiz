package com.example.luisle.equiz.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Adapter.QuestionListAdapter;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.MyFramework.EndlessScrollListener;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class AdminQuestionFrag extends Fragment {

    private RecyclerView rvcQuestion;
    private FloatingActionButton fabQuestionAdd;
    private QuestionListAdapter questionListAdapter;
    private ArrayList<Question> questionList;
    private EndlessScrollListener scrollListener;

    private getFloatingButton getAddButton;


    // TODO: Rename and change types and number of parameters
    public static AdminQuestionFrag newInstance() {
        AdminQuestionFrag fragment = new AdminQuestionFrag();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_question_admin, container, false);
        mappingLayout(view);
        getAddButton.getAddButton(fabQuestionAdd);
        fabQuestionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AddQuestionFrag addQuestionFrag = new AddQuestionFrag();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fabQuestionAdd.hide();
                ((AdminHomeAct) getActivity()).getBottomNavigationView().setVisibility(View.INVISIBLE);
                transaction.add(android.R.id.content, addQuestionFrag).addToBackStack(null).commit();
//                transaction.add(R.id.linearLayoutActHomeAdmin, addQuestionFrag).addToBackStack(null).commit();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getAddButton = (getFloatingButton) context;
    }

    public void customLoadMoreDataFromApi(int offset) {
    }

    private void mappingLayout(View view) {
        rvcQuestion = (RecyclerView) view.findViewById(R.id.rcvQuestionAdmin);
        fabQuestionAdd = (FloatingActionButton) view.findViewById(R.id.fabFragQuestionAdd);
    }

    public interface getFloatingButton {
        public FloatingActionButton getAddButton(FloatingActionButton fab);
    }

}
