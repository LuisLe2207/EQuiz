package com.example.luisle.equiz.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.luisle.equiz.Model.User;
import com.example.luisle.equiz.MyFramework.MyEssential;
import com.example.luisle.equiz.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.changeEmail;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.changePassword;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.setProfile;
import static com.example.luisle.equiz.MyFramework.MyEssential.REQUEST_IMAGE_CAPTURE;
import static com.example.luisle.equiz.MyFramework.MyEssential.REQUEST_IMAGE_GALLERY;
import static com.example.luisle.equiz.MyFramework.MyEssential.USERS_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.USER_AVATAR;
import static com.example.luisle.equiz.MyFramework.MyEssential.convertImageViewToByte;
import static com.example.luisle.equiz.MyFramework.MyEssential.createDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQUizStorageRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizStorage;
import static com.example.luisle.equiz.MyFramework.MyEssential.firebaseUser;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/18/2017.
 */

public class AccountFrag extends Fragment{

    //Fragment Palette Layout
    private EditText edtAccFrag_FullName, edtAccFragEmail, edtAccFrag_Moblie;
    private Button btnAccFrag_ChangeEmail, btnAccFrag_ChangePassword, btnAccFrag_Save;
    private RoundedImageView imgAccFrag_Avatar;
    private EditText edtDialog_ChangeEmail_Email, edtDialog_ChangeEmail_Pass, edtDialog_ChangeEmail_NewEmail;
    private EditText edtDialog_ChangePassword_Email, edtDialog_ChangePassword_Pass, edtDialog_ChangePassword_NewPass;


    // Fragment variables
    private User user;

    public AccountFrag () {
        // Required empty public constructor
    }

    /**
     * Create new instance of Fragment
     * @return Fragment
     */
    public static AccountFrag newInstance() {
        AccountFrag fragment = new AccountFrag();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_account, container, false);

        // Init Firebase Storage
        eQuizStorage = FirebaseStorage.getInstance("gs://equiz-59c1f.appspot.com");
        // Init Firebase StorageRef
        eQUizStorageRef = eQuizStorage.getReference();

        mappingPaletteLayout(view);
        getUserProfile();
        openChangeEmailDialog();
        openChangePasswordDialog();
        chooseAvatarAction();
        saveChanges();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data !=null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgAccFrag_Avatar.setImageBitmap(imageBitmap);
            Log.d("Hello", "hello");
        }

        if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                imgAccFrag_Avatar.setImageBitmap(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Mapping Fragment Palette Layout
     * @param view layout
     */
    private void mappingPaletteLayout(View view) {
        edtAccFrag_FullName = (EditText) view.findViewById(R.id.edtAccFrag_FullName);
        edtAccFragEmail = (EditText) view.findViewById(R.id.edtAccFrag_Email);
        edtAccFrag_Moblie = (EditText) view.findViewById(R.id.edtAccFrag_Moblie);
        btnAccFrag_ChangeEmail = (Button) view.findViewById(R.id.btnAccFrag_ChangeEmail);
        btnAccFrag_ChangePassword = (Button) view.findViewById(R.id.btnAccFrag_ChangePassword);
        btnAccFrag_Save = (Button) view.findViewById(R.id.btnAccFrag_Save);
        imgAccFrag_Avatar = (RoundedImageView) view.findViewById(R.id.imgAccFrag_Avatar);
    }

    /**
     * Get userProfile
     */
    private void getUserProfile() {
        if (firebaseUser != null) {
            edtAccFrag_FullName.setText(firebaseUser.getDisplayName());
            edtAccFragEmail.setText(firebaseUser.getEmail());
            Picasso.with(getContext())
                    .load(firebaseUser.getPhotoUrl())
                    .placeholder(R.mipmap.ic_avatar)
                    .error(R.mipmap.ic_avatar)
                    .into(imgAccFrag_Avatar);

        }
        eQuizRef.child(USERS_CHILD).child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    user = dataSnapshot.getValue(User.class);
                    edtAccFrag_Moblie.setText(user.getMobile());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Open Choose Avatar Dialog
     */
    private void chooseAvatarAction() {
        imgAccFrag_Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyEssential.openAvatarActionDialog((AppCompatActivity) getActivity());
            }
        });
    }

    /**
     * Open Change Email Dialog
     */
    private void openChangeEmailDialog() {
        btnAccFrag_ChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Declare and create dialog
                Dialog changeEmailDialog = createDialog(getContext(),
                        R.layout.dialog_change_email,
                        getContext().getResources().getString(R.string.text_change_email));
                // Mapping dialog layout
                Button btnChangeEmail = (Button) changeEmailDialog.findViewById(R.id.btnDialog_ChangeEmail_Save);
                edtDialog_ChangeEmail_Email = (EditText) changeEmailDialog.findViewById(R.id.edtDialog_ChangeEmail_Email);
                edtDialog_ChangeEmail_Pass = (EditText) changeEmailDialog.findViewById(R.id.edtDialog_ChangeEmail_Pass);
                edtDialog_ChangeEmail_NewEmail = (EditText) changeEmailDialog.findViewById(R.id.edtDialog_ChangeEmail_NewEmail);
                btnChangeEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = edtDialog_ChangeEmail_Email.getText().toString().trim();
                        String password = edtDialog_ChangeEmail_Pass.getText().toString().trim();
                        String newEmail = edtDialog_ChangeEmail_NewEmail.getText().toString().trim();
                        if (validateInput(email, password, newEmail, "email")) {
                            changeEmail(getContext(), firebaseUser, email, password, newEmail);
                        }
                    }
                });
                changeEmailDialog.show();
            }
        });
    }

    /**
     * Open Change Password Dialog
     */
    private void openChangePasswordDialog() {
        // Declare and create dialog
        btnAccFrag_ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog changePasswordDialog = createDialog(getContext(),
                        R.layout.dialog_change_password,
                        getContext().getResources().getString(R.string.text_change_password));
                // Mapping dialog layout
                Button btnChangePassword = (Button) changePasswordDialog.findViewById(R.id.btnDialog_ChangePassword_Save);
                edtDialog_ChangePassword_Email = (EditText) changePasswordDialog.findViewById(R.id.edtDialog_ChangePassword_Email);
                edtDialog_ChangePassword_Pass = (EditText) changePasswordDialog.findViewById(R.id.edtDialog_ChangePassword_Pass);
                edtDialog_ChangePassword_NewPass = (EditText) changePasswordDialog.findViewById(R.id.edtDialog_ChangePassword_NewPass);
                btnChangePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = edtDialog_ChangePassword_Email.getText().toString().trim();
                        String password = edtDialog_ChangePassword_Pass.getText().toString().trim();
                        String newPassword = edtDialog_ChangePassword_NewPass.getText().toString().trim();
                        if (validateInput(email, password, newPassword, "password")) {
                            changePassword(getContext(), firebaseUser, email, password, newPassword);
                        }
                    }
                });
                changePasswordDialog.show();
            }
        });
    }

    /**
     * Save user changes
     */
    private void saveChanges() {
        btnAccFrag_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullName = edtAccFrag_FullName.getText().toString().trim();
                final String mobile = edtAccFrag_Moblie.getText().toString().trim();
                if (!TextUtils.isEmpty(fullName)) {
                    final ProgressDialog saveProgressDialog = createProgressDialog(
                                                                    getContext(),
                                                                    getResources().getString(R.string.text_progress_save));

                    saveProgressDialog.show();
                    // Declare StorageRef
                    final StorageReference userAvatarRef = eQUizStorageRef.child(USER_AVATAR + user.getID());
                    UploadTask uploadTask = userAvatarRef.putBytes(convertImageViewToByte(imgAccFrag_Avatar));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            user.setFullName(fullName);
                            user.setMobile(mobile);
                            @SuppressWarnings("VisibleForTests") Uri userAvatarURL = taskSnapshot.getDownloadUrl();
                            user.setProfilePicture(String.valueOf(userAvatarURL));
                            Map<String, Object> childUpdate = new HashMap<>();
                            childUpdate.put(USERS_CHILD + "/" + firebaseUser.getUid() + "/", user);
                            eQuizRef.updateChildren(childUpdate);
                            setProfile(firebaseUser, user);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    saveProgressDialog.dismiss();
                                    showToast(
                                            getContext(),
                                            getResources().getString(R.string.update_profile_success)
                                    );
                                }
                            }, 2000);

                        }
                    });
                } else {
                    edtAccFrag_FullName.setError(getResources().getString(R.string.error_name_not_fill));
                }
            }
        });
    }

    /**
     * Validate user input
     * @param email String
     * @param password String
     * @param changeValue String
     * @param action String validate for email or password dialog
     * @return
     */
    public boolean validateInput(String email, String password, String changeValue, String action) {
        // Email Dialog
        if (TextUtils.equals(action, "email")) {
            // Check empty
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(changeValue)) {
                if (TextUtils.isEmpty(email)) {
                    edtDialog_ChangeEmail_Email.setError(getResources().getString(R.string.error_email_not_fill));
                }
                if (TextUtils.isEmpty(password)) {
                    edtDialog_ChangeEmail_Pass.setError(getResources().getString(R.string.error_pass_not_fill));
                }
                if (TextUtils.isEmpty(changeValue)) {
                    edtDialog_ChangeEmail_NewEmail.setError(getResources().getString(R.string.error_email_not_fill));
                }
                return false;
            } else {
                // Check email pattern
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showToast(
                            getContext(),
                            getResources().getString(R.string.error_email_format)
                    );
                }
            }
        } else { // Password Dialog
            // Check Email
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(changeValue)) {
                if (TextUtils.isEmpty(email)) {
                    edtDialog_ChangePassword_Email.setError(getResources().getString(R.string.error_email_not_fill));
                }
                if (TextUtils.isEmpty(password)) {
                    edtDialog_ChangePassword_Pass.setError(getResources().getString(R.string.error_pass_not_fill));
                }
                if (TextUtils.isEmpty(changeValue)) {
                    edtDialog_ChangePassword_NewPass.setError(getResources().getString(R.string.error_pass_not_fill));
                }
                return false;
            } else {
                // Check email pattern
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showToast(
                            getContext(),
                            getResources().getString(R.string.error_email_format)
                    );
                }
                // Check password length
                if (password.length() < 6 || password.length() > 15) {
                    if (password.length() < 6) {
                        showToast(
                                getContext(),
                                getResources().getString(R.string.error_pass_min_length));
                    } else {
                        showToast(
                                getContext(),
                                getResources().getString(R.string.error_pass_max_length)
                        );
                    }
                    return false;
                }
            }
        }

        return true;
    }

}
