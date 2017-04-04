package com.example.luisle.equiz.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.luisle.equiz.Model.User;
import com.example.luisle.equiz.MyFramework.MyEssential;
import com.example.luisle.equiz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.saveUser;
import static com.example.luisle.equiz.MyFramework.MyEssential.REQUEST_IMAGE_CAPTURE;
import static com.example.luisle.equiz.MyFramework.MyEssential.REQUEST_IMAGE_GALLERY;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQUizStorageRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizStorage;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

public class RegisterAct extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Layout
    private RoundedImageView imgAvatar;
    private EditText edtFullName, edtEmail, edtPass, edtCPass, edtMobile;
    private Button btnRegister;

    // Act variables
    private ProgressDialog registerProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);

        // Ini FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        // Ini AuthStateListener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    startActivity(new Intent(RegisterAct.this, HomeAct.class));
                }
            }
        };
        // Init FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Init DatabaseRef
        eQuizRef = eQuizDatabase.getReference();
        // Init Firebase Storage
        eQuizStorage = FirebaseStorage.getInstance("gs://equiz-59c1f.appspot.com");
        // Init Firebase StorageRef
        eQUizStorageRef = eQuizStorage.getReference();

        mappingLayout();
        onRegister();
        chooseAvatarAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data !=null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgAvatar.setImageBitmap(imageBitmap);
        }

        if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                imgAvatar.setImageBitmap(imageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Map layout
     */
    private void mappingLayout() {
        imgAvatar = (RoundedImageView) findViewById(R.id.imgRegAct_Avatar);
        edtFullName = (EditText) findViewById(R.id.edtRegAct_FullName);
        edtEmail = (EditText) findViewById(R.id.edtRegAct_Email);
        edtPass = (EditText) findViewById(R.id.edtRegAct_Pass);
        edtCPass = (EditText) findViewById(R.id.edtRegAct_CPass);
        edtMobile = (EditText) findViewById(R.id.edtRegAct_Moblie);
        btnRegister = (Button) findViewById(R.id.btnRegAct_Register);
    }

    /**
     * Register new user
     */
    public void onRegister() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String fullName = edtFullName.getText().toString().trim();
                final String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                String cPassword = edtCPass.getText().toString().trim();
                final String mobile = edtMobile.getText().toString().trim();
                if (inputValidate(fullName, email, password, cPassword)) {
                    // Create progress dialog
                    registerProgressDialog = createProgressDialog(
                                                RegisterAct.this,
                                                getResources().getString(R.string.text_progress_register));
                    registerProgressDialog.show();
                    btnRegister.setEnabled(false);
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterAct.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (createUser(fullName, email, mobile)) {
                                            onRegisterSuccess();
                                        } else {
                                            onRegisterFailure();
                                        }
                                    } else {
                                        onRegisterFailure();
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     *
     * @param fullName user's full name
     * @param email user's email
     * @param mobile user's mobile
     */
    private boolean createUser(String fullName, String email, String mobile) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            User newUser = new User(firebaseUser.getUid(), email, fullName, "", mobile);
            saveUser(eQuizRef, eQUizStorageRef, newUser, firebaseUser, imgAvatar);
            return true;
        }
        return false;
    }

    private void onRegisterSuccess() {
        showToast(
                getApplicationContext(),
                getResources().getString(R.string.register_success)
        );
        registerProgressDialog.dismiss();
        btnRegister.setEnabled(true);
//        FirebaseAuth.getInstance().signOut();
//        Intent loginActIntent = new Intent(RegisterAct.this, LoginAct.class);
//        loginActIntent.putExtra("New", true);
//        startActivity(loginActIntent);
    }

    private void onRegisterFailure() {
        registerProgressDialog.dismiss();
        showToast(
                getApplicationContext(),
                getResources().getString(R.string.register_failed)
        );
        btnRegister.setEnabled(true);
    }


    private void chooseAvatarAction() {
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyEssential.openAvatarActionDialog(RegisterAct.this);
            }
        });
    }

    /**
     * Check input's validation
     * @param name User name
     * @param email User email
     * @param pass User password
     * @param cpass User confirm password
     * @return boolean
     */
    private boolean inputValidate(String name, String email, String pass, String cpass) {
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(pass) && TextUtils.isEmpty(cpass)) {
            if (TextUtils.isEmpty(name)) {
                edtFullName.setError(getResources().getString(R.string.error_name_not_fill));
            }
            if (TextUtils.isEmpty(email)) {
                edtEmail.setError(getResources().getString(R.string.error_email_not_fill));
            }
            if (TextUtils.isEmpty(pass)) {
                edtPass.setError(getResources().getString(R.string.error_pass_not_fill));
            }
            if (TextUtils.isEmpty(cpass)) {
                edtCPass.setError(getResources().getString(R.string.error_cpass_not_fill));
            }
            return false;
        } else {

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(
                        getApplicationContext(),
                        getResources().getString(R.string.error_email_format)
                );
            }

            if (!TextUtils.equals(pass, cpass)) {
                showToast(
                        getApplicationContext(),
                        getResources().getString(R.string.error_pass_cpass_not_equal));
                return false;
            }
            if (pass.length() < 6 || pass.length() > 15) {
                if (pass.length() < 6) {
                    showToast(
                            getApplicationContext(),
                            getResources().getString(R.string.error_pass_min_length));
                } else {
                    showToast(
                            getApplicationContext(),
                            getResources().getString(R.string.error_pass_max_length)
                    );
                }
                return false;
            }
                return true;
        }
    }
}
