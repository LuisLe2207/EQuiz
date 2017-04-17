package com.example.luisle.equiz.MyFramework;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.example.luisle.equiz.MyFramework.MyEssential.registerToken;

/**
 * Created by LuisLe on 4/16/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

        registerToken(token);
    }


}
