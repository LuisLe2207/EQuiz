package com.example.luisle.equiz.MyFramework;

import android.content.Context;
import android.os.AsyncTask;

import com.example.luisle.equiz.Model.Notification;
import com.example.luisle.equiz.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.saveNotification;
import static com.example.luisle.equiz.MyFramework.DatabaseLib.setMaitainStatus;
import static com.example.luisle.equiz.MyFramework.MyEssential.allowMaintain;
import static com.example.luisle.equiz.MyFramework.MyEssential.changeBackEndRules;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.pushNotification;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;

/**
 * Created by LuisLe on 4/18/2017.
 */

public class PushNotifications extends AsyncTask<String, String, String> {

    private Context context;
    public PushNotifications(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        pushNotification(strings[0], strings[1]);
        switch (strings[0]) {
            case "Maintain":
                // Change backend rule
                changeBackEndRules(userID, "Maintain");
                // Parse startDatetime and endDateTime to Date
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date sDateTime = null;
                Date eDateTime = null;
                try {
                    sDateTime = dateFormat.parse(strings[2]);
                    eDateTime = dateFormat.parse(strings[3]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // Save notification to backend server
                Notification newNotification =
                        new Notification(strings[1], sDateTime.getTime(), eDateTime.getTime(), true);
                saveNotification(eQuizRef, newNotification);
                // Set allowMaintain var to true => allow user to modify exams and questions
                allowMaintain = true;
                setMaitainStatus(eQuizRef, allowMaintain);
                break;
            case "Finish":
                // Change backend rule
                changeBackEndRules(userID, "Finish");
                // Set allowMaintain var to false => not allow user to modify exams and questions
                allowMaintain = false;
                setMaitainStatus(eQuizRef, allowMaintain);
                break;
        }
        return strings[0];
    }

    @Override
    protected void onPostExecute(String s) {
        switch (s) {
            case "Maintain":
            showToast(context, context.getResources().getString(R.string.push_notification_success));
                break;
        }
    }
}
