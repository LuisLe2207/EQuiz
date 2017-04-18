package com.example.luisle.equiz.MyFramework;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.luisle.equiz.Activity.ResultAct;
import com.example.luisle.equiz.Adapter.ExamListAdapter;
import com.example.luisle.equiz.Adapter.QuestionChoiceAdapter;
import com.example.luisle.equiz.Adapter.QuestionListAdapter;
import com.example.luisle.equiz.Model.Choice;
import com.example.luisle.equiz.Model.Comment;
import com.example.luisle.equiz.Model.Exam;
import com.example.luisle.equiz.Model.ExamResult;
import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.Model.QuestionResult;
import com.example.luisle.equiz.Model.User;
import com.example.luisle.equiz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.luisle.equiz.MyFramework.MyEssential.COMMENT_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.EXAM_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.QUESTION_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.RESULT_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.USERS_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.USER_AVATAR;
import static com.example.luisle.equiz.MyFramework.MyEssential.choiceID;
import static com.example.luisle.equiz.MyFramework.MyEssential.convertImageViewToByte;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.dialogOnScreen;
import static com.example.luisle.equiz.MyFramework.MyEssential.inAddExamDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;

/**
 * Created by LuisLe on 2/11/2017.
 */

public class DatabaseLib {

    // region User Firebase

    /**
     *
     * @param dataRef FirebaseRef
     * @param user User model
     */
    public static void saveUser(
            final DatabaseReference dataRef,
            StorageReference storageRef,
            final User user,
            final FirebaseUser firebaseUser, RoundedImageView userAvatar) {
        StorageReference userAvatarRef = storageRef.child(USER_AVATAR + user.getID());
        UploadTask uploadTask = userAvatarRef.putBytes(convertImageViewToByte(userAvatar));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                @SuppressWarnings("VisibleForTests") Uri userAvatarURL = taskSnapshot.getDownloadUrl();
                user.setProfilePicture(String.valueOf(userAvatarURL));
                dataRef.child(USERS_CHILD).child(user.getID()).setValue(user, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        setProfile(firebaseUser, user);
                    }
                });
            }
        });
    }

    public static void setProfile(FirebaseUser firebaseUser, User user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getFullName())
                .setPhotoUri(Uri.parse(user.getProfilePicture())).build();
        firebaseUser.updateProfile(profileUpdates);
    }

    public static void changeEmail(final Context context, final FirebaseUser firebaseUser, String oldEmail, String password, final String newEmail) {
        final ProgressDialog emailProgressDialog = createProgressDialog(
                                                    context,
                                                    context.getResources().getString(R.string.text_progress_change));
        emailProgressDialog.show();
        AuthCredential credential = EmailAuthProvider
                .getCredential(oldEmail, password);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.updateEmail(newEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        emailProgressDialog.dismiss();
                                                        showToast(
                                                                context,
                                                                context.getResources().getString(R.string.change_email_success));
                                                    }
                                                }, 2000);

                                            } else {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        emailProgressDialog.dismiss();
                                                        showToast(
                                                                context,
                                                                context.getResources().getString(R.string.change_email_failed));
                                                    }
                                                }, 2000);

                                            }
                                        }
                                    });
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    emailProgressDialog.dismiss();
                                    showToast(
                                            context,
                                            context.getResources().getString(R.string.login_failed)
                                    );
                                }
                            }, 2000);
                        }

                    }
                });
    }

    public static void changePassword(final Context context, final FirebaseUser firebaseUser, String email, String password, final String newPassword) {
        final ProgressDialog passwordProgressDialog = createProgressDialog(
                                                    context,
                                                    context.getResources().getString(R.string.text_progress_change));
        passwordProgressDialog.show();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        passwordProgressDialog.dismiss();
                                                        showToast(
                                                                context,
                                                                context.getResources().getString(R.string.change_password_success));
                                                    }
                                                }, 2000);

                                            } else {
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        passwordProgressDialog.dismiss();
                                                        showToast(
                                                                context,
                                                                context.getResources().getString(R.string.change_password_failed));
                                                    }
                                                }, 2000);

                                            }
                                        }
                                    });
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    passwordProgressDialog.dismiss();
                                    showToast(
                                            context,
                                            context.getResources().getString(R.string.login_failed)
                                    );
                                }
                            }, 2000);
                        }
                    }
                });
    }

    // endregion

    // region Question Firebase
    public static void saveQuestion(final Context context, final DatabaseReference dataRef, Question newQuestion, String id) {
        final ProgressDialog saveQuestionProgressDialog = createProgressDialog(context,
                context.getResources().getString(R.string.text_progress_save));
        saveQuestionProgressDialog.show();
        String questionID = "";
        if (TextUtils.isEmpty(id)) {
            questionID = dataRef.child(QUESTION_CHILD).push().getKey();
            newQuestion.setID(questionID);
        } else {
            questionID = id;
            newQuestion.setID(questionID);
        }

        dataRef.child(QUESTION_CHILD).child(questionID).setValue(newQuestion, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           saveQuestionProgressDialog.dismiss();
                           showToast(context, context.getResources().getString(R.string.save_question_sucess));
                       }
                   }, 2000);

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveQuestionProgressDialog.dismiss();
                            showToast(context, context.getResources().getString(R.string.save_question_failed));
                        }
                    }, 2000);
                }

            }
        });
    }

    public static void getQuestions(
            final DatabaseReference dataRef,
            final RecyclerView rcv,
            final ProgressBar pgb,
            final ArrayList<Question> questionList,
            final QuestionListAdapter adapter) {
        dataRef.child(QUESTION_CHILD).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Question question = dataSnapshot.getValue(Question.class);
                questionList.add(question);
                adapter.notifyDataSetChanged();
                rcv.setAdapter(adapter);
                if (!dialogOnScreen || inAddExamDialog) {
                    pgb.setVisibility(View.INVISIBLE);
                    rcv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getQuestion(final DatabaseReference dataRef, String id,
                                   final ArrayList<Choice> choiceList,
                                   final ArrayList<Integer> answerList,
                                   final EditText edtTitle,
                                   final QuestionChoiceAdapter adapter) {
        dataRef.child(QUESTION_CHILD).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question question = dataSnapshot.getValue(Question.class);
                choiceList.clear();
                choiceList.addAll(question.getChoiceList());
                answerList.addAll(question.getAnswerList());
                edtTitle.setText(question.getTitle());
                adapter.notifyDataSetChanged();
                choiceID = question.getChoiceList().size() + 1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // endregion

    // region Exam Firebase

    public static void saveExam(final Context context,
                                final DatabaseReference dataRef,
                                final Exam newExam,
                                String id) {
        final ProgressDialog saveExamProgressDialog = createProgressDialog(context,
                context.getResources().getString(R.string.text_progress_save));
        saveExamProgressDialog.show();
        String examID = "";
        if (TextUtils.isEmpty(id)) {
            examID = dataRef.child(EXAM_CHILD).push().getKey();
            newExam.setID(examID);
        } else  {
            examID = id;
            newExam.setID(examID);
        }

        dataRef.child(EXAM_CHILD).child(examID).setValue(newExam, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveExamProgressDialog.dismiss();
                            showToast(context, context.getResources().getString(R.string.save_exam_sucess));
                        }
                    }, 2000);
                    Handler mainHandler = new Handler(context.getMainLooper());
                    mainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new PushNotifications(context).execute(userID,
                                    context.getResources().getString(R.string.notification_new_exam),
                                    newExam.getTitle() + " " + context.getResources().getString(R.string.notification_new_exam_message));
                        }
                    }, 2000);

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveExamProgressDialog.dismiss();
                            showToast(context, context.getResources().getString(R.string.save_exam_failed));
                        }
                    }, 2000);
                }

            }
        });

    }

    public static void getExams(final DatabaseReference dataRef,
                                final RecyclerView rcv,
                                final ProgressBar pgb,
                                final ArrayList<Exam> examList,
                                final ExamListAdapter adapter) {
        dataRef.child(EXAM_CHILD).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Exam exam = dataSnapshot.getValue(Exam.class);
                examList.add(exam);
                adapter.notifyDataSetChanged();
                rcv.setAdapter(adapter);
                if (!dialogOnScreen) {
                    pgb.setVisibility(View.INVISIBLE);
                    rcv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getExam(final DatabaseReference dataRef,
                               final String examID,
                               final ArrayList<String> examQuestionList,
                               final EditText edtTitle,
                               final QuestionListAdapter adapter) {
        dataRef.child(EXAM_CHILD).child(examID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Exam exam = dataSnapshot.getValue(Exam.class);
                examQuestionList.clear();
                examQuestionList.addAll(exam.getQuestionList());
                edtTitle.setText(exam.getTitle());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // endregion

    // region RESULT
    public static void saveResult(final Context context, DatabaseReference dataRef, String userID,final String examID, ExamResult examResult) {
        final ProgressDialog saveResultProgressDialog = createProgressDialog(context,
                context.getResources().getString(R.string.text_progress_submit));
        saveResultProgressDialog.show();
        // Get date finish exam
        Calendar calendar = Calendar.getInstance();
        // Create count of correct answer
        Integer correctAnswer = 0;
        for (QuestionResult result : examResult.getQuestionResults()) {
            if (result.getBoolResult()) {
                correctAnswer++;
            }
        }
        examResult.setID(String.valueOf(calendar.getTimeInMillis()));
        examResult.setCorrectAnswer(correctAnswer);
        dataRef.child(RESULT_CHILD)
                .child(userID)
                .child(examID)
                .child(String.valueOf(calendar.getTimeInMillis()))
                .setValue(examResult, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveResultProgressDialog.dismiss();
                            showToast(context, context.getResources().getString(R.string.save_result_success));
//                            Intent commentIntent = new Intent(context, CommentAct.class);
//                            commentIntent.putExtra("ID", examID);
//                            context.startActivity(commentIntent);
                        }
                    }, 2000);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveResultProgressDialog.dismiss();
                            showToast(context, context.getResources().getString(R.string.save_result_failed));
                        }
                    }, 2000);
                }
            }
        });

    }
    // endregion

    // region COMMENT

    public static void submitComment(final Context context, DatabaseReference dataRef, final String examID, Comment comment) {
        final ProgressDialog saveCommentProgressDialog = createProgressDialog(context,
                context.getResources().getString(R.string.text_progress_submit));
        saveCommentProgressDialog.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final String createDate = simpleDateFormat.format(calendar.getTime());
        String commentID = dataRef.child(COMMENT_CHILD).child(examID).push().getKey();
        comment.setID(commentID);
        comment.setDateCreated(createDate);
        dataRef.child(COMMENT_CHILD).child(examID).child(commentID).setValue(comment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveCommentProgressDialog.dismiss();
                            showToast(context, context.getResources().getString(R.string.submit_comment_success));
                            Bundle iDBundle = new Bundle();
                            iDBundle.putString("examID", examID);
                            iDBundle.putString("examResultID", "");
                            Intent resultIntent = new Intent(context, ResultAct.class);
                            resultIntent.putExtra("ID", iDBundle);
                            context.startActivity(resultIntent);
                        }
                    }, 2000);

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            saveCommentProgressDialog.dismiss();
                            showToast(context, context.getResources().getString(R.string.submit_comment_failed));
                        }
                    }, 2000);
                }
            }
        });
    }

    // endregion

    // region User Statistics
    public static void getDoneExam(final DatabaseReference dataRef,
                                   final String examID,
                                   final RecyclerView rcv,
                                   final ProgressBar pgb,
                                   final ArrayList<Exam> examList,
                                   final ExamListAdapter adapter) {
        dataRef.child(EXAM_CHILD).child(examID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Exam exam = dataSnapshot.getValue(Exam.class);
                examList.add(exam);
                adapter.notifyDataSetChanged();
                rcv.setAdapter(adapter);
                if (!dialogOnScreen) {
                    pgb.setVisibility(View.INVISIBLE);
                    rcv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // endregion

}
