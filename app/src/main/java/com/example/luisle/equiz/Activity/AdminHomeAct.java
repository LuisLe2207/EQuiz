package com.example.luisle.equiz.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.luisle.equiz.Fragment.AccountFrag;
import com.example.luisle.equiz.Fragment.AdminExamFrag;
import com.example.luisle.equiz.Fragment.AdminQuestionFrag;
import com.example.luisle.equiz.MyFramework.PushNotifications;
import com.example.luisle.equiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.luisle.equiz.MyFramework.MyEssential.allowMaintain;
import static com.example.luisle.equiz.MyFramework.MyEssential.createDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.firebaseUser;
import static com.example.luisle.equiz.MyFramework.MyEssential.openDateDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.openTimeDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;

public class AdminHomeAct extends AppCompatActivity {


    private EditText edtStartDate, edtEndDate, edtStartTime, edtEndTime;
    private Spinner spnNotificationTpe;
    private Button btnPush;
    private BottomNavigationView navigation;
    private Toolbar toolbar;

    private Fragment fragment = null;
    private String fragmentTag = "ExamListFrag";
    private Calendar calendar;

    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_admin_home);

        // Init FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Init DatabaseRef
        eQuizRef = eQuizDatabase.getReference();
        // Get current date time
        calendar = Calendar.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

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
        MenuItem pushNotificationItem =  menu.add(1, 111, 1, getResources().getString(R.string.menu_push_notification));
        pushNotificationItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        pushNotificationItem.setIcon(R.mipmap.ic_add_notification);
        menu.add(1, 222, 2, getResources().getString(R.string.menu_about));
        menu.add(1, 444, 3, getResources().getString(R.string.menu_logout));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 111:
                createPushNotification();
                break;
            case 222:
//                DoiMatKhauFragment doiMatKhauFragment = new DoiMatKhauFragment();
//                doiMatKhauFragment.show(getSupportFragmentManager(), "DoiMatKhau");
                break;
            case 444:
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

    private void createPushNotification() {
        Dialog notificationDialog = createDialog(AdminHomeAct.this,
                R.layout.dialog_push_notification,
                getResources().getString(R.string.text_notification));

        edtStartDate = (EditText) notificationDialog.findViewById(R.id.edtDialogPushNoti_StartDate);
        edtEndDate = (EditText) notificationDialog.findViewById(R.id.edtDialogPushNoti_EndDate);
        edtStartTime = (EditText) notificationDialog.findViewById(R.id.edtDialogPushNoti_StartTime);
        edtEndTime = (EditText) notificationDialog.findViewById(R.id.edtDialogPushNoti_EndTime);
        spnNotificationTpe = (Spinner) notificationDialog.findViewById(R.id.spnDialogPushNoti_NotificationType);
        btnPush = (Button) notificationDialog.findViewById(R.id.btnDialogPushNoti_Push);

        // Set click for each Edit text
        edtStartDate.setOnClickListener(showDateDialog(edtStartDate));
        edtEndDate.setOnClickListener(showDateDialog(edtEndDate));
        edtStartTime.setOnClickListener(showTimeDialog(edtStartTime));
        edtEndTime.setOnClickListener(showTimeDialog(edtEndTime));

        // Set Spinner value
        final ArrayList<String> notificationTypeList = new ArrayList<>();
        notificationTypeList.add(getResources().getString(R.string.notification_type_maintain));
        notificationTypeList.add(getResources().getString(R.string.notification_type_finish));
        ArrayAdapter spinnerAdapter = new ArrayAdapter(AdminHomeAct.this, android.R.layout.simple_spinner_dropdown_item, notificationTypeList);
        spnNotificationTpe.setAdapter(spinnerAdapter);

        final String[] type = new String[1];
        spnNotificationTpe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type[0] = notificationTypeList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startDate = edtStartDate.getText().toString().trim();
                String endDate = edtEndDate.getText().toString().trim();
                String startTime = edtStartTime.getText().toString().trim();
                String endTime = edtEndTime.getText().toString().trim();
                try {
                    if (validateInput(type[0], startDate, endDate, startTime, endTime)) {
                        String message = "";
                        switch (type[0]) {
                            case "Maintain":
                                message = getResources().getString(R.string.notification_message_maintain)
                                        + " " + startDate + " " + startTime + " "
                                        + getResources().getString(R.string.notification_to)
                                        + " " + endDate + " " + endTime;
                                break;
                            case "Finish":
                                message = getResources().getString(R.string.notification_message_finish);
                                break;
                        }
                        final String finalMessage = message;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new PushNotifications(AdminHomeAct.this).execute(userID, type[0], finalMessage);
                                allowMaintain = true;
                            }
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        notificationDialog.show();
    }

    private boolean validateInput(String type, String startDate, String endDate, String startTime, String endTime) throws ParseException {
        boolean result = false;

        if (TextUtils.isEmpty(type)) {
            result = false;
            showToast(getApplicationContext(),
                    getResources().getString(R.string.error_empty_noti_type));
        }

        if (startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            result =false;
            showToast(getApplicationContext(),
                    getResources().getString(R.string.error_empty_noti_time));
        } else {
            String startDateTime = startDate + " " + startTime;
            String endDateTime = endDate + " " + endTime;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date sDate = dateFormat.parse(startDateTime);
            Date eDate = dateFormat.parse(endDateTime);

            if (sDate.getTime() > eDate.getTime()) {
                showToast(getApplicationContext(),
                        getResources().getString(R.string.error_invalid_noti_time));
                result = false;
            } else {
                result = true;
            }

        }

        return result;
    }

    View.OnClickListener showDateDialog(final EditText editText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(AdminHomeAct.this, editText);
            }
        };
    }

    View.OnClickListener showTimeDialog(final EditText editText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog(AdminHomeAct.this, editText);
            }
        };
    }

    public BottomNavigationView getBottomNavigationView() {
        return navigation;
    }

}
