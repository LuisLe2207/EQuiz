package com.example.luisle.equiz.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.luisle.equiz.Fragment.AccountFrag;
import com.example.luisle.equiz.Fragment.DetailExamFrag;
import com.example.luisle.equiz.Fragment.HomeFrag;
import com.example.luisle.equiz.Fragment.UserStatisticsFrag;
import com.example.luisle.equiz.MyFramework.RegisterUserToken;
import com.example.luisle.equiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.getMaintainStatus;
import static com.example.luisle.equiz.MyFramework.MyEssential.createDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.firebaseUser;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;


public class HomeAct extends AppCompatActivity {

    // Act Palette Layout
    private BottomNavigationView navigation;

    // Act fragment layout
    private Fragment fragment = null;

    // Act Variables
    private boolean doubleBackToExitPressedOnce = false;
    private String fragmentTag = "HomeFrag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);

        initVariables();
        sendUserTokenToServer();
        createActionBar();
        initFragmentLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 111, 1, getResources().getString(R.string.menu_about));
        menu.add(1, 222, 2, getResources().getString(R.string.menu_logout));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 111:
                openAboutDialog();
                break;
            case 222:
                FirebaseAuth.getInstance().signOut();
                final ProgressDialog signOutProgressDialog = createProgressDialog(HomeAct.this,
                        getResources().getString(R.string.text_progress_sign_out));
                signOutProgressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        signOutProgressDialog.dismiss();
                        startActivity(new Intent(HomeAct.this, LoginAct.class));
                    }
                }, 2000);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }

        this.doubleBackToExitPressedOnce = true;
        showToast(getApplicationContext(), getResources().getString(R.string.press_twice_to_minimize));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    /**
     * Init variables
     */
    private void initVariables() {
        // Get FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Get DatabaseRef
        eQuizRef = eQuizDatabase.getReference();
        // Get firebase user and Get user id
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               getMaintainStatus(HomeAct.this);
            }
        }, 2000);
    }

    /**
     *  Send user token to server and register it
     */
    private void sendUserTokenToServer() {
        // Send token to PHP server
        FirebaseMessaging.getInstance().subscribeToTopic("Notification");
        FirebaseInstanceId.getInstance().getToken();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new RegisterUserToken().execute(userID, FirebaseInstanceId.getInstance().getToken());
            }
        });
    }
    /**
     *  Create action bar layout
     */
    private void createActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarActHome);
        setSupportActionBar(toolbar);


        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * Init first page fragment of bottom navigation bar
     */
    private void initFragmentLayout() {
        DetailExamFrag detailExamFrag = (DetailExamFrag) getSupportFragmentManager().findFragmentByTag("DetailExamFrag");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (detailExamFrag != null) {
            transaction.remove(detailExamFrag);
        }
        transaction.replace(R.id.frameLayoutActHome, HomeFrag.newInstance(), fragmentTag);
        transaction.commit();
    }

    /**
     * Open About Dialog
     */
    private void openAboutDialog() {
        Dialog dialog = createDialog(HomeAct.this, R.layout.dialog_about, getResources().getString(R.string.text_about));
        final RoundedImageView imgAvatar = (RoundedImageView) dialog.findViewById(R.id.imgFragAbout_Avatar);
        TextView txtName = (TextView) dialog.findViewById(R.id.txtDialogAbout_Name);
        TextView txtID = (TextView) dialog.findViewById(R.id.txtDialogAbout_ID);
        txtName.setText(getResources().getString(R.string.text_about_name) + " Lê Đăng Khoa");
        txtID.setText(getResources().getString(R.string.text_about_id) + " K39.104.039");
        Picasso.with(HomeAct.this).load(R.drawable.me).into(imgAvatar);
        dialog.show();
    }

    // Set switch bottom navigation view
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().popBackStackImmediate();
                    fragment = HomeFrag.newInstance();
                    fragmentTag = "HomeFrag";
                    break;
                case R.id.navigation_statistics:
                    getSupportFragmentManager().popBackStackImmediate();
                    fragment = UserStatisticsFrag.newInstance();
                    fragmentTag = "UserStatisticsFrag";
                    break;
                case R.id.navigation_account:
                    getSupportFragmentManager().popBackStackImmediate();
                    fragment = AccountFrag.newInstance();
                    fragmentTag = "AccountFrag";
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutActHome, fragment, fragmentTag);
            transaction.commit();
            return true;
        }

    };

    // Get navigation view for Fragment
    public BottomNavigationView getBottomNavigationView() {
        return navigation;
    }

}
