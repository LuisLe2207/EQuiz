package com.example.luisle.equiz.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.luisle.equiz.MyFramework.MyEssential;
import com.example.luisle.equiz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.isAdmin;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;

public class LoginAct extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Layout
    private EditText edtEmail, edtPass;
    private Button btnLogin, btnForgotPass, btnRegister;

    // Act Variables
    private ProgressDialog loginProgressDialog;
    private boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        // Initilize Firebase FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        isNew  = getIntent().getBooleanExtra("New", false);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userID = user.getUid();
                    if (!TextUtils.equals(user.getUid(), "IdqIxA6Bg0diKdoiRFzISpR2Z662")) {
                        isAdmin = false;
                        startActivity(new Intent(LoginAct.this, HomeAct.class));
                    } else {
                        isAdmin = true;
                        startActivity(new Intent(LoginAct.this, AdminHomeAct.class));
                    }

                }

            }
        };
        mappingLayout();
        onLogin();
        onForgotPass();
        onRegister();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Map layout
     */
    private void mappingLayout() {
        edtEmail = (EditText) findViewById(R.id.edtLoginAct_Email);
        edtPass = (EditText) findViewById(R.id.edtLoginAct_Pass);
        btnLogin = (Button) findViewById(R.id.btnLoginAct_Login);
        btnForgotPass = (Button) findViewById(R.id.btnLoginAct_Forgotpassword);
        btnRegister = (Button) findViewById(R.id.btnLoginAct_Register);
    }

    /**
     * Login into app
     */
    public void onLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                if (inputValidate(email, password)) {
                    // Create progress dialog
                    loginProgressDialog = createProgressDialog(
                                                LoginAct.this,
                                                getResources().getString(R.string.text_progress_login));
                    loginProgressDialog.show();
                    // Disable Any Button when Login
                    disableButton();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginAct.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        onSuccess();
                                    } else {
                                        onFailure();
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * Switch to register screen
     */
    public void onRegister() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginAct.this, RegisterAct.class));
            }
        });
    }

    /**
     * Open forgot password dialog
     */
    public void onForgotPass() {
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog resetPasswordDialog = new Dialog(LoginAct.this);
                resetPasswordDialog.setContentView(R.layout.dialog_reset_password);
                resetPasswordDialog.setTitle("Reset Password");
                Button btnResetPassword = (Button) resetPasswordDialog.findViewById(R.id.btnDialog_ResetPassword_Reset);
                final EditText edtDialog_ResetPassword_Email = (EditText) resetPasswordDialog.findViewById(R.id.edtDialog_ResetPassword_Email);
                btnResetPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = edtDialog_ResetPassword_Email.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            if (TextUtils.isEmpty(email)) {
                                edtDialog_ResetPassword_Email.setError(getResources().getString(R.string.error_email_not_fill));
                            }
                        } else {
                            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                MyEssential.showToast(
                                        getApplicationContext(),
                                        getResources().getString(R.string.error_email_format)
                                );
                            } else {
                                final ProgressDialog resetPasswordProgressDialog = createProgressDialog(
                                                                                LoginAct.this,
                                                                                getResources().getString(R.string.text_progress_reset));
                                resetPasswordProgressDialog.show();
                                mAuth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            resetPasswordProgressDialog.dismiss();
                                                            resetPasswordDialog.dismiss();
                                                            showToast(
                                                                    getApplicationContext(),
                                                                    getResources().getString(R.string.reset_password_success));
                                                        }
                                                    }, 2000);

                                                } else {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            resetPasswordProgressDialog.dismiss();
                                                            showToast(
                                                                    getApplicationContext(),
                                                                    getResources().getString(R.string.reset_password_failed));
                                                        }
                                                    }, 2000);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
                resetPasswordDialog.show();
            }
        });
    }


    public void onSuccess() {
        MyEssential.showToast(
                getApplicationContext(),
                getResources().getString(R.string.login_success)
        );
        loginProgressDialog.dismiss();
        enableButton();
        isNew = false;
    }

    public void onFailure() {
        loginProgressDialog.dismiss();
        MyEssential.showToast (
                getApplicationContext(),
                getResources().getString(R.string.login_failed)
        );
        enableButton();
    }

    /**
     * Check input's validation
     * @param email User email
     * @param pass User password
     * @return boolean
     */
    private boolean inputValidate(String email, String pass) {
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)) {
            if (TextUtils.isEmpty(email)) {
                edtEmail.setError(getResources().getString(R.string.error_email_not_fill));
            }
            if (TextUtils.isEmpty(pass)) {
                edtPass.setError(getResources().getString(R.string.error_pass_not_fill));
            }
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MyEssential.showToast(
                    getApplicationContext(),
                    getResources().getString(R.string.error_email_format)
            );
            return false;
        }

        if (pass.length() < 6 || pass.length() > 15) {
            if (pass.length() < 6) {
                MyEssential.showToast(
                        getApplicationContext(),
                        getResources().getString(R.string.error_pass_min_length));
            } else {
                MyEssential.showToast(
                        getApplicationContext(),
                        getResources().getString(R.string.error_pass_max_length)
                );
            }
            return false;
        }
        return true;
    }

    /**
     * Disable button's action when login
     */
    public void disableButton() {
        btnLogin.setEnabled(false);
        btnForgotPass.setEnabled(false);
        btnRegister.setEnabled(false);
    }

    /**
     * Enable button's when not login
     */
    public void enableButton() {
        btnLogin.setEnabled(true);
        btnForgotPass.setEnabled(true);
        btnRegister.setEnabled(true);
    }
}
