package com.example.luisle.equiz.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
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

public class AdminHomeAct extends AppCompatActivity implements AdminQuestionFrag.getFloatingButton{


    private Fragment fragment = null;
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView navigation;
    private Toolbar toolbar;
    private String fragmentTag = "ExamListFrag";

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


    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    public BottomNavigationView getBottomNavigationView() {
        return navigation;
    }

    @Override
    public FloatingActionButton getAddButton(FloatingActionButton fab) {
        return floatingActionButton = fab;
    }
}
