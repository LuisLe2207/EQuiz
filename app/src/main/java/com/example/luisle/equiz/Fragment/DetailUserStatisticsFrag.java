package com.example.luisle.equiz.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.luisle.equiz.Activity.HomeAct;
import com.example.luisle.equiz.Adapter.ExamResultListAdapter;
import com.example.luisle.equiz.Model.ExamResult;
import com.example.luisle.equiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.luisle.equiz.MyFramework.MyEssential.RESULT_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;

/**
 * Created by LuisLe on 4/13/2017.
 */

public class DetailUserStatisticsFrag extends DialogFragment {

    private String examID;
    private String userID;
    private ArrayList<ExamResult> examResultList;
    private ExamResultListAdapter examResultListAdapter;

    private EditText edtExamTitle, edtBestCompleteTime;
    private ProgressBar pgBarLoading;
    private RecyclerView rcvDoneTimes;

    private FirebaseUser firebaseUser;

    public static DetailUserStatisticsFrag newInstance(String examID) {
        DetailUserStatisticsFrag fragment = new DetailUserStatisticsFrag();
        Bundle args = new Bundle();
        args.putString("ID", examID);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_detail_user_statistics, container, false);
        examID = getArguments().getString("ID");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }
        createActionBar(view);
        mappingLayout(view);
        init();
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
            case android.R.id.home:
                dismiss();
                FragmentManager fragmentManager = (getActivity()).getSupportFragmentManager();
                UserStatisticsFrag userStatisticsFrag = (UserStatisticsFrag) fragmentManager.findFragmentByTag("UserStatisticsFrag");
                userStatisticsFrag.showLayout();
                ((HomeAct) getActivity()).getBottomNavigationView().setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mappingLayout(View view) {
        edtExamTitle = (EditText) view.findViewById(R.id.edtDialogDetailUserStatistics_Title);
        edtBestCompleteTime = (EditText) view.findViewById(R.id.edtDialogDetailUserStatistics_BestCompleteTime);
        pgBarLoading = (ProgressBar) view.findViewById(R.id.pgBarFragUserStatisticsDetail_Loading);
        rcvDoneTimes = (RecyclerView) view.findViewById(R.id.rcvDoneTimes);
    }

    private void init() {
        pgBarLoading.setVisibility(View.VISIBLE);
        rcvDoneTimes.setVisibility(View.INVISIBLE);
        examResultList = new ArrayList<>();
        if (examID != null && !examID.isEmpty()) {
            getExamResult(examID);
            final ProgressDialog loadExamResultProgressDialog = createProgressDialog(getContext()
                    , getContext().getResources().getString(R.string.text_progress_retrieving));
            loadExamResultProgressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDoneExamDetail();
                    examResultListAdapter = new ExamResultListAdapter(getContext(), examResultList, examID);
                    rcvDoneTimes.setAdapter(examResultListAdapter);
                    rcvDoneTimes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    loadExamResultProgressDialog.dismiss();
                    pgBarLoading.setVisibility(View.INVISIBLE);
                    rcvDoneTimes.setVisibility(View.VISIBLE);
                }
            }, 2000);

        }
    }

    private void createActionBar(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarDialogDetailUserStatistics);
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

    private void setDoneExamDetail() {
        edtExamTitle.setText(examResultList.get(0).getExamTitle());
        edtBestCompleteTime.setText("" + String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(examResultList.get(0).getCompleteTime()),
                TimeUnit.MILLISECONDS.toSeconds(examResultList.get(0).getCompleteTime()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(examResultList.get(0).getCompleteTime()))));
    }


    private void getExamResult(String examID) {
        eQuizRef.child(RESULT_CHILD).child(userID).child(examID).orderByChild("completeTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setExamResults(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setExamResults(DataSnapshot dataSnapshot) {
        for (DataSnapshot examResultSnapshot : dataSnapshot.getChildren()) {
            ExamResult examResult = examResultSnapshot.getValue(ExamResult.class);
            examResultList.add(examResult);
        }
    }

}
