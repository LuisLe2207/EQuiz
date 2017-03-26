package com.example.luisle.equiz.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQUizStorageRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizStorage;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

/**
 * Created by LuisLe on 3/18/2017.
 */

public class AccountFrag extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Fragment Layout
    private EditText edtAccFrag_FullName, edtAccFragEmail, edtAccFrag_Moblie;
    private Button btnAccFrag_ChangeEmail, btnAccFrag_ChangePassword, btnAccFrag_Save;
    private RoundedImageView imgAccFrag_Avatar;
    private EditText edtDialog_ChangeEmail_Email, edtDialog_ChangeEmail_Pass, edtDialog_ChangeEmail_NewEmail;
    private EditText edtDialog_ChangePassword_Email, edtDialog_ChangePassword_Pass, edtDialog_ChangePassword_NewPass;

    // Firebase Variables
    private FirebaseUser firebaseUser;

    // Fragment variables
    private User user;

    public AccountFrag () {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFrag newInstance(String param1, String param2) {
        AccountFrag fragment = new AccountFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_account, container, false);

        // Get Firebase User
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Init Firebase Storage
        eQuizStorage = FirebaseStorage.getInstance("gs://equiz-59c1f.appspot.com");
        // Init Firebase StorageRef
        eQUizStorageRef = eQuizStorage.getReference();

        mappingLayout(view);
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

    private void mappingLayout(View view) {
        edtAccFrag_FullName = (EditText) view.findViewById(R.id.edtAccFrag_FullName);
        edtAccFragEmail = (EditText) view.findViewById(R.id.edtAccFrag_Email);
        edtAccFrag_Moblie = (EditText) view.findViewById(R.id.edtAccFrag_Moblie);
        btnAccFrag_ChangeEmail = (Button) view.findViewById(R.id.btnAccFrag_ChangeEmail);
        btnAccFrag_ChangePassword = (Button) view.findViewById(R.id.btnAccFrag_ChangePassword);
        btnAccFrag_Save = (Button) view.findViewById(R.id.btnAccFrag_Save);
        imgAccFrag_Avatar = (RoundedImageView) view.findViewById(R.id.imgAccFrag_Avatar);
    }

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

    private void chooseAvatarAction() {
        imgAccFrag_Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyEssential.openAvatarActionDialog((AppCompatActivity) getActivity());
            }
        });
    }

    private void openChangeEmailDialog() {
        btnAccFrag_ChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog changeEmailDialog = new Dialog(getContext());
                changeEmailDialog.setContentView(R.layout.dialog_change_email);
                changeEmailDialog.setTitle("Change Email");
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

    private void openChangePasswordDialog() {
        btnAccFrag_ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog changePasswordDialog = new Dialog(getContext());
                changePasswordDialog.setContentView(R.layout.dialog_change_password);
                changePasswordDialog.setTitle("Change Password");
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
                            saveProgressDialog.dismiss();
                            showToast(
                                    getContext(),
                                    getResources().getString(R.string.update_profile_success)
                            );
                        }
                    });
                } else {
                    edtAccFrag_FullName.setError(getResources().getString(R.string.error_name_not_fill));
                }
            }
        });
    }

    public boolean validateInput(String email, String password, String changeValue, String action) {
        if (TextUtils.equals(action, "email")) {
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
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showToast(
                            getContext(),
                            getResources().getString(R.string.error_email_format)
                    );
                }
            }
        } else {
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
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    showToast(
                            getContext(),
                            getResources().getString(R.string.error_email_format)
                    );
                }
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