package com.example.luisle.equiz.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.luisle.equiz.Activity.HomeAct;
import com.example.luisle.equiz.Activity.MainAct;
import com.example.luisle.equiz.R;

import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 4/5/2017.
 */

public class DetailExamFrag extends DialogFragment {


    String examID;

    public static DetailExamFrag newInstance(String examID) {
        DetailExamFrag fragment = new DetailExamFrag();
        Bundle args = new Bundle();
        args.putString("ID", examID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_detail_exam, container, false);
        createActionBar(view);
        if (getArguments() != null) {
            examID = getArguments().getString("ID");
        }
        showToast(getContext(), examID);
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
        MenuItem createActionItem = menu.add(1,333,1, getResources().getString(R.string.text_start));
        createActionItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 333:
                doExam();
                break;
            case android.R.id.home:
                dismiss();
                FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
                HomeFrag homeFrag = (HomeFrag) fragmentManager.findFragmentByTag("HomeFrag");
                homeFrag.showLayout();
                ((HomeAct) getActivity()).getBottomNavigationView().setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createActionBar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarDialogDetailExam);
        toolbar.setTitle(getContext().getResources().getString(R.string.toolbar_detail));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        setHasOptionsMenu(true);
    }

    private void doExam() {
        final AlertDialog.Builder doExamAlertDialog = new AlertDialog.Builder(getActivity());
        doExamAlertDialog.setMessage(getContext().getResources().getString(R.string.text_do_exam));
        doExamAlertDialog.setPositiveButton(getContext().getResources().getString(R.string.text_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mainAct = new Intent(getContext(), MainAct.class);
                mainAct.putExtra("ID", examID);
                startActivity(mainAct);
            }
        });
        doExamAlertDialog.setNegativeButton(getContext().getResources().getString(R.string.text_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        doExamAlertDialog.create();
        doExamAlertDialog.setCancelable(false);
        doExamAlertDialog.show();
    }
}
