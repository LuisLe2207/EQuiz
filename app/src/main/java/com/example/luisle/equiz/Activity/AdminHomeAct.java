package com.example.luisle.equiz.Activity;

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

import com.example.luisle.equiz.Fragment.AccountFrag;
import com.example.luisle.equiz.Fragment.AdminExamFrag;
import com.example.luisle.equiz.Fragment.AdminQuestionFrag;
import com.example.luisle.equiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

public class AdminHomeAct extends AppCompatActivity {


    private Fragment fragment = null;
    private BottomNavigationView navigation;
    private Toolbar toolbar;
    private String fragmentTag = "ExamListFrag";

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_admin_home);

        // Init FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Init DatabaseRef
        eQuizRef = eQuizDatabase.getReference();
        // Get current user

        toolbar = (Toolbar) findViewById(R.id.toolbarActHomeAdmin);
        setSupportActionBar(toolbar);

        navigation = (BottomNavigationView) findViewById(R.id.navigationAdmin);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutActHomeAdmin, AdminExamFrag.newInstance(), fragmentTag);
        transaction.commit();

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
//                DoiMatKhauFragment doiMatKhauFragment = new DoiMatKhauFragment();
//                doiMatKhauFragment.show(getSupportFragmentManager(), "DoiMatKhau");
                break;
            case 222:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminHomeAct.this, LoginAct.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_exam:
                    getSupportFragmentManager().popBackStackImmediate();
                    fragment = AdminExamFrag.newInstance();
                    fragmentTag = "ExamListFrag";
                    break;
                case R.id.navigation_question:
                    getSupportFragmentManager().popBackStackImmediate();
                    fragment = AdminQuestionFrag.newInstance();
                    fragmentTag = "QuestionListFrag";
                    break;
                case R.id.navigation_account:
                    getSupportFragmentManager().popBackStackImmediate();
                    fragment = AccountFrag.newInstance("Hello", "Hello");
                    fragmentTag = "AccountFrag";
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutActHomeAdmin, fragment, fragmentTag);
            transaction.commit();
            return true;
        }

    };

    public BottomNavigationView getBottomNavigationView() {
        return navigation;
    }

}
