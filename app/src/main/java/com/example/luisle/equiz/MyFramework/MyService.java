package com.example.luisle.equiz.MyFramework;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.luisle.equiz.R;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import static com.example.luisle.equiz.MyFramework.DatabaseLib.setMaitainStatus;
import static com.example.luisle.equiz.MyFramework.MyEssential.NOTIFICATION_CHILD;
import static com.example.luisle.equiz.MyFramework.MyEssential.allowMaintain;
import static com.example.luisle.equiz.MyFramework.MyEssential.changeBackEndRules;
import static com.example.luisle.equiz.MyFramework.MyEssential.eQuizRef;
import static com.example.luisle.equiz.MyFramework.MyEssential.showToast;
import static com.example.luisle.equiz.MyFramework.MyEssential.userID;

/**
 * Created by LuisLe on 4/20/2017.
 */

public class MyService extends Service {

    private Context context;
    private CountDownTimer countDownTimer;
    private Handler handler;
    private boolean isStartMaintain = false;
    private int service_id;

    private class MyThread implements Runnable {

        int service_id;
        Intent intent;

        public MyThread(int service_id, Intent intent) {
            this.service_id = service_id;
            this.intent = intent;
        }

        @Override
        public void run() {
            synchronized (this) {
                Bundle bundle = intent.getBundleExtra("Data");
                long eDateTime = bundle.getLong("NotificationEndDateTime");
                long sDateTime = bundle.getLong("NotificationStartDateTime");
                final long duration = eDateTime - sDateTime;
                final Calendar calendar = Calendar.getInstance();
                long cDateTime = calendar.getTimeInMillis();
                while (cDateTime < sDateTime) {
                    if (cDateTime > sDateTime - 5000) {
                        isStartMaintain = true;
                        allowMaintain = true;
                        // Set maintain status
                        setMaitainStatus(eQuizRef, allowMaintain);
                        // Change backend rule
                        changeBackEndRules(userID, "Maintain");
                    }
                    Calendar newCalendar = Calendar.getInstance();
                    cDateTime = newCalendar.getTimeInMillis();
                }
                Looper.prepare();
                if (isStartMaintain) {
                    countDownTimer = new CountDownTimer(duration, 1000) {
                        @Override
                        public void onTick(long l) {
                            Log.d("Hello", "Hello");
                        }

                        @Override
                        public void onFinish() {
                            removeDoneNotification(eQuizRef);
                        }
                    };
                    countDownTimer.start();
                    Looper.loop();
                }
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            service_id = startId;
            Thread thread = new Thread(new MyThread(startId, intent));
            thread.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStartMaintain = false;
        countDownTimer.cancel();
        stopSelf(service_id);
    }

    private void removeDoneNotification(DatabaseReference dataRef) {
        dataRef.child(NOTIFICATION_CHILD).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    showToast(context, context.getResources().getString(R.string.finish_maintain));
                    new PushNotifications(context).execute("Finish",
                            context.getResources().getString(R.string.notification_message_finish));
                }
            }
        });
    }
}
