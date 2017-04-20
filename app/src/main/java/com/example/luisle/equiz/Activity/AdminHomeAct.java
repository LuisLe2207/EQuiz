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
import com.example.luisle.equiz.Model.Notification;
import com.example.luisle.equiz.MyFramework.MyService;
import com.example.luisle.equiz.MyFramework.PushNotifications;
import com.example.luisle.equiz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.removeDoneNotification;
import static com.example.luisle.equiz.MyFramework.MyEssential.MAINTAIN_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.NOTIFICATION_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.allowMaintain;
import static com.example.luisle.equiz.MyFramework.MyEssential.createDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.createProgressDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizDatabase;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.firebaseUser;
import static com.example.luisle.equiz.MyFramework.MyEssential.openDateDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.openTimeDialog;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;

public class AdminHomeAct extends AppCompatActivity {

    // region VARIABLES

    // Act Palette Layouts
    private EditText edtStartDate, edtEndDate, edtStartTime, edtEndTime;
    private Spinner spnNotificationTpe;
    private Button btnPush;
    private BottomNavigationView navigation;
    private Toolbar toolbar;

    // Act Fragment Layout
    private Fragment fragment = null;

    // Act Variables
    private String fragmentTag = "ExamListFrag";
    private boolean doubleBackToExitPressedOnce = false;
    private Calendar calendar;
    private Notification notification;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_admin_home);

        createActionBar();
        initFragmentLayout();
        initVariable();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem pushNotificationItem =  menu.add(1, 111, 1, getResources().getString(R.string.menu_push_notification));
        pushNotificationItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        pushNotificationItem.setIcon(R.mipmap.ic_add_notification);
        menu.add(1, 222, 3, getResources().getString(R.string.menu_about));
        menu.add(1, 444, 4, getResources().getString(R.string.menu_logout));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 111:
                if (notification == null) {
                    createPushNotification();
                } else {
                    showToast(getApplicationContext(), "You already have push notification to inform maintain time. Please finish it before create a new one");
                }
                break;
            case 222:
                showToast(getApplicationContext(), notification.getMessage());
                break;
            case 444:
                FirebaseAuth.getInstance().signOut();
                final ProgressDialog signOutProgressDialog = createProgressDialog(AdminHomeAct.this,
                        getResources().getString(R.string.text_progress_sign_out));
                signOutProgressDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        signOutProgressDialog.dismiss();
                        startActivity(new Intent(AdminHomeAct.this, LoginAct.class));
                    }
                }, 2000);

                break;
            case 555:
                // remove done notification and push finish notification
                removeDoneNotification(AdminHomeAct.this, eQuizRef);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            Intent maintainServiceIntent = new Intent(AdminHomeAct.this, MyService.class);
                            stopService(maintainServiceIntent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allowMaintain) {
                    MenuItem finishItem = menu.findItem(555);
                    if (finishItem == null) {
                        menu.add(1, 555, 2, getResources().getString(R.string.notification_type_finish));
                    }
                } else {
                    menu.removeItem(555);
                }
            }
        }, 1500);

        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Init variables
     */
    private void initVariable() {
        // Get FirebaseDatabase
        eQuizDatabase = FirebaseDatabase.getInstance();
        // Get DatabaseRef
        eQuizRef = eQuizDatabase.getReference();
        // Get current date time
        calendar = Calendar.getInstance();

        // Get firebase user and set user id
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

        notification = null;
    }

    /**
     * Init Act Data
     */
    private void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNotification();
                getMaintainStatus();
            }
        }, 1000);
    }

    /**
     *  Create action bar layout
     */
    private void createActionBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbarActHomeAdmin);
        setSupportActionBar(toolbar);

        navigation = (BottomNavigationView) findViewById(R.id.navigationAdmin);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * Init first page fragment of bottom navigation bar
     */
    private void initFragmentLayout() {
        getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutActHomeAdmin, AdminExamFrag.newInstance(), fragmentTag);
        transaction.commit();
    }

    /**
     *  Create Push Notification Dialog
     */
    private void createPushNotification() {
        // Init dialog
        final Dialog notificationDialog = createDialog(AdminHomeAct.this,
                R.layout.dialog_push_notification,
                getResources().getString(R.string.text_notification));
        // Mapping Dialog Layout
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

        // Set Notification type Spinner value
        final ArrayList<String> notificationTypeList = new ArrayList<>();
        notificationTypeList.add(getResources().getString(R.string.notification_type_maintain));
        notificationTypeList.add(getResources().getString(R.string.notification_type_finish));
        ArrayAdapter spinnerAdapter = new ArrayAdapter(AdminHomeAct.this, android.R.layout.simple_spinner_dropdown_item, notificationTypeList);
        spnNotificationTpe.setAdapter(spinnerAdapter);

        // Declare notification type
        final String[] type = new String[1];
        // Set notification type
        spnNotificationTpe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type[0] = notificationTypeList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Set click listener for push button
        btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get start, end datetime
                String startDate = edtStartDate.getText().toString().trim();
                String endDate = edtEndDate.getText().toString().trim();
                String startTime = edtStartTime.getText().toString().trim();
                String endTime = edtEndTime.getText().toString().trim();
                try {
                    // Check input is valid?
                    if (validateInput(type[0], startDate, endDate, startTime, endTime)) {
                        // Declare notification message
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
                        // Create new Notification model
                        // Get datetime of start and end
                        final String startDateTime = startDate + " " + startTime;
                        final String endDateTime = endDate + " " + endTime;
                        // Push notification
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {new PushNotifications(AdminHomeAct.this).execute(type[0], finalMessage, startDateTime, endDateTime);

                            }
                        });
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(10000);
                                    if (notification != null) {
                                        Intent maintainServiceIntent = new Intent(AdminHomeAct.this, MyService.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putLong("NotificationStartDateTime", notification.getStartDateTime());
                                        bundle.putLong("NotificationEndDateTime", notification.getEndDateTime());
                                        maintainServiceIntent.putExtra("Data",bundle);
                                        startService(maintainServiceIntent);
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        notificationDialog.dismiss();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        notificationDialog.show();
    }

    /**
     *
     * @param type Notification type
     * @param startDate start date of notification
     * @param endDate end date of notification
     * @param startTime start time of notification
     * @param endTime end time of notification
     * @return boolean
     * @throws ParseException parse date error
     */
    private boolean validateInput(String type, String startDate, String endDate, String startTime, String endTime) throws ParseException {
        boolean result = false;

        // Check notification type is empty?
        if (TextUtils.isEmpty(type)) {
            result = false;
            showToast(getApplicationContext(),
                    getResources().getString(R.string.error_empty_noti_type));
        }
        // Check time and date is empty?
        if (startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            result =false;
            showToast(getApplicationContext(),
                    getResources().getString(R.string.error_empty_noti_time));
        } else {
            // Get datetime of start and end
            String startDateTime = startDate + " " + startTime;
            String endDateTime = endDate + " " + endTime;
            // Parse startDatetime and endDateTime to Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Long sDateTime = dateFormat.parse(startDateTime).getTime();
            Long eDateTime = dateFormat.parse(endDateTime).getTime();
            // Get current time in millisecond
            Calendar calendar = Calendar.getInstance();
            long cDateTime = calendar.getTimeInMillis();

            // Start DateTime > End DateTime
            if (eDateTime - sDateTime < 0) {
                showToast(getApplicationContext(),
                        getResources().getString(R.string.error_invalid_noti_time));
            }
            // Start DateTime < Current DateTime
            if (sDateTime < cDateTime) {
                showToast(getApplicationContext(),
                        getResources().getString(R.string.error_invalid_noti_time));
            } else {
                result = true;
            }
        }

        return result;
    }

    /**
     * Get notification data from server
     */
    private void getNotification() {
        eQuizRef.child(NOTIFICATION_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setNotification(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set notification data from dataSnapshot to notification model
     * @param dataSnapshot DataSnapshot
     */
    private void setNotification(DataSnapshot dataSnapshot) {
        notification = dataSnapshot.getValue(Notification.class);
    }

    /**
     * Get maintain status from backend server
     */
    private void getMaintainStatus() {
        eQuizRef.child(MAINTAIN_CHILD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    allowMaintain = (boolean) dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Open Date dialog
    View.OnClickListener showDateDialog(final EditText editText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(AdminHomeAct.this, editText);
            }
        };
    }

    // Open Time dialog
    View.OnClickListener showTimeDialog(final EditText editText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog(AdminHomeAct.this, editText);
            }
        };
    }

    // Set switch bottom navigation view
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
                    fragment = AccountFrag.newInstance();
                    fragmentTag = "AccountFrag";
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutActHomeAdmin, fragment, fragmentTag);
            transaction.commit();
            return true;
        }

    };

    // Get navigation view for Fragment
    public BottomNavigationView getBottomNavigationView() {
        return navigation;
    }

}
