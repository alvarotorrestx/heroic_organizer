package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.config.FirebaseDB;

public class LogoutPresenter {

    private static final String TAG = "LogoutPresenter";

    public static void logoutUser() {
        FirebaseDB
                .getAuth()
                .signOut();

        Log.d(TAG, "User signed out");
    }
}
