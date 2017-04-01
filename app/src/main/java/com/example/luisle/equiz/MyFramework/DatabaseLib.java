package com.example.luisle.equiz.MyFramework;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.example.luisle.equiz.Model.Question;
import com.example.luisle.equiz.Model.User;
import com.example.luisle.equiz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import static com.example.luisle.equiz.MyFramework.MyEssential.QUESTION_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.USERS_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.USER_AVATAR;
import static com.example.luisle.equiz.MyFramework.MyEssential.convertImageViewToByte;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

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
    public static void addQuestion(final Context context, final DatabaseReference dataRef, Question newQuestion) {
        final ProgressDialog saveQuestionProgressDialog = createProgressDialog(context,
                context.getResources().getString(R.string.text_progress_save));
        saveQuestionProgressDialog.show();
        final String questionID = dataRef.child(QUESTION_CHILD).push().getKey();
        newQuestion.setID(questionID);
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
    // endregion

}
