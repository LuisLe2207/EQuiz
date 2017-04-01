package com.example.luisle.equiz.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.luisle.equiz.Activity.AdminHomeAct;
import com.example.luisle.equiz.Adapter.QuestionChoiceAdapter;
import com.example.luisle.equiz.R;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/26/2017.
 */

public class ExamFrag extends DialogFragment {
    // Layout
    EditText edtQuestionTitle, edtQuestionChoice;
    Button btnAddChoice;
    RecyclerView rcvQuestion;

    // Fragment Variables
    ArrayList<String> choiceList;
    ArrayList<String> answerList;
    QuestionChoiceAdapter questionChoiceAdapter;
    String id;

    public static ExamFrag newInstance(String id) {
        ExamFrag fragment = new ExamFrag();
        Bundle args = new Bundle();
        args.putString("ID", id);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_question, container, false);

        mappingLayout(view);
        createActionBar(view);

        if (getArguments() != null) {
            id = getArguments().getString("ID");
        }
        showToast(getContext(), id);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        MenuItem createActionItem = menu.add(1,333,1, getResources().getString(R.string.text_create));
        createActionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 333:
                showToast(getContext(), "Hello");
                break;
            case android.R.id.home:
                dismiss();
                FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AdminExamFrag adminExamFrag1 = (AdminExamFrag) fragmentManager.findFragmentByTag("ExamListFrag");
//                transaction.attach(adminExamFrag1).commit();
                adminExamFrag1.showLayout();
                ((AdminHomeAct) getActivity()).getBottomNavigationView().setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mappingLayout(View view) {
        edtQuestionTitle = (EditText) view.findViewById(R.id.edtDialog_AddQuestion_Title);
        edtQuestionChoice = (EditText) view.findViewById(R.id.edtDialog_AddQuestion_Choice);
        btnAddChoice = (Button) view.findViewById(R.id.btnDialog_AddQuestion_AddChoice);
        rcvQuestion = (RecyclerView) view.findViewById(R.id.rcvDialogAddQuestion);
    }

    private void createActionBar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarDialogAddQuestion);
        toolbar.setTitle(getContext().getResources().getString(R.string.toolbar_add_question));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        setHasOptionsMenu(true);
    }

}
