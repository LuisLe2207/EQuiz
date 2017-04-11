package com.example.luisle.equiz.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.example.luisle.equiz.Activity.HomeAct;
import com.example.luisle.equiz.Activity.MainAct;
import com.example.luisle.equiz.Adapter.CommentListAdapter;
import com.example.luisle.equiz.Model.Comment;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.luisle.equiz.MyFramework.MyEssential.COMMENT_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.EXAM_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;

/**
 * Created by LuisLe on 4/5/2017.
 */

public class DetailExamFrag extends DialogFragment {


    private String examID;
    private Exam exam;
    private ArrayList<Comment> commentList;
    private CommentListAdapter commentListAdapter;

    private EditText edtTitle, edtDuration, edtNumberOFQuestion, edtCreatedDate;
    private TextView txtNumberOfComment;

    private RecyclerView rcvComment;
    private ProgressBar pgBarLoading;

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
        mappingLayout(view);
        if (getArguments() != null) {
            examID = getArguments().getString("ID");
        }
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

    private void mappingLayout(View view) {
        edtTitle = (EditText) view.findViewById(R.id.edtDialogDetailExam_Title);
        edtDuration = (EditText) view.findViewById(R.id.edtDialogDetailExam_Duration);
        edtNumberOFQuestion = (EditText) view.findViewById(R.id.edtDialogDetailExam_NumberOFQuestion);
        edtCreatedDate = (EditText) view.findViewById(R.id.edtDialogDetailExam_CreatedDate);
        txtNumberOfComment = (TextView) view.findViewById(R.id.txtDialogDetailExam_NumberOfComment);
        rcvComment = (RecyclerView) view.findViewById(R.id.rcvComment);
        pgBarLoading = (ProgressBar) view.findViewById(R.id.pgBarFragExamDetail_LoadingComment);
    }

    private void init() {
        pgBarLoading.setVisibility(View.VISIBLE);
        rcvComment.setVisibility(View.INVISIBLE);
        commentList = new ArrayList<>();
        if (examID != null && !examID.isEmpty()) {
            getExam(examID);
            getComment(examID);
            final ProgressDialog loadExamDetailProgressDialog = createProgressDialog(getContext()
                    , getContext().getResources().getString(R.string.text_progress_retrieving));
            if (exam == null) {
                loadExamDetailProgressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (exam != null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setExamDetail();
                                    setCommentList();
                                    commentListAdapter = new CommentListAdapter(getContext(), commentList);
                                    rcvComment.setAdapter(commentListAdapter);
                                    rcvComment.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                }
                            }, 500);
                        }
                        loadExamDetailProgressDialog.dismiss();
                        pgBarLoading.setVisibility(View.INVISIBLE);
                        rcvComment.setVisibility(View.VISIBLE);
                    }
                }, 1500);
            }

        }
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

    private void setExamDetail() {
        edtTitle.append(exam.getTitle());
        edtDuration.append(exam.getDuration() + "'");
        edtNumberOFQuestion.append(String.valueOf(exam.getNumberOfQuestion()) + " questions");
        edtCreatedDate.append(exam.getDateCreated());
    }

    private void setCommentList() {
        if (commentList.isEmpty()) {
            txtNumberOfComment.setText(getContext().getResources().getString(R.string.text_exam_no_comment));
        } else {
            txtNumberOfComment.setText(getContext().getResources().getString(R.string.text_exam_has_comment)
                    + " " + commentList.size()
                    + " " + getContext().getResources().getString(R.string.text_exam_comment));
        }
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

    public void getExam(String examID) {
        eQuizRef.child(EXAM_CHILD).child(examID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setExam(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setExam(DataSnapshot dataSnapshot) {
        exam = dataSnapshot.getValue(Exam.class);
    }

    private void getComment(String examID) {
        eQuizRef.child(COMMENT_CHILD).child(examID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setComments(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setComments(DataSnapshot dataSnapshot) {
        for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
            Comment comment = commentSnapshot.getValue(Comment.class);
            commentList.add(comment);
        }
    }
}
