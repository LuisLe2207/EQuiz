package com.example.luisle.equiz.MyFramework;

import android.content.Context;
import android.os.AsyncTask;

import com.example.luisle.equiz.R;

import static com.example.luisle.equiz.MyFramework.MyEssential.pushNotification;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;

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
        pushNotification(strings[0], strings[1], strings[2]);
        return strings[1];
    }

    @Override
    protected void onPostExecute(String s) {
        showToast(context, context.getResources().getString(R.string.push_notification_success));
    }
}
