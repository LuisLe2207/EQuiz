package com.example.luisle.equiz.MyFramework;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.luisle.equiz.Activity.LoginAct;
import com.example.luisle.equiz.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.luisle.equiz.MyFramework.MyEssential.isAdmin;

/**
 * Created by LuisLe on 4/16/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if (!TextUtils.equals(remoteMessage.getData().get("userID"), "IdqIxA6Bg0diKdoiRFzISpR2Z662")) {
//            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
//        }
        if(!isAdmin) {
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }
    }

    private void showNotification(String title, String message) {

        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = new Intent(this, LoginAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }
}
