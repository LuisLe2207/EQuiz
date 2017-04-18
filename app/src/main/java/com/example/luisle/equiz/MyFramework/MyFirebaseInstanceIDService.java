package com.example.luisle.equiz.MyFramework;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.example.luisle.equiz.MyFramework.MyEssential.registerToken;

/**
 * Created by LuisLe on 4/16/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private FirebaseUser firebaseUser;
    private String userID = "";

    @Override
    public void onTokenRefresh() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userID = firebaseUser.getUid();
        }

        String token = FirebaseInstanceId.getInstance().getToken();

        registerToken(userID, token);
    }


}
