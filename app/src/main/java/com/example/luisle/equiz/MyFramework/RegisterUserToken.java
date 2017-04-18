package com.example.luisle.equiz.MyFramework;

import android.os.AsyncTask;

import static com.example.luisle.equiz.MyFramework.MyEssential.registerToken;

/**
 * Created by LuisLe on 4/18/2017.
 */

public class RegisterUserToken extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {
        registerToken(strings[0], strings[1]);
        return null;
    }
}
