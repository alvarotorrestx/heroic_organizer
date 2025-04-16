package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.UserPresenter;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class RegisterPresenter {

    private static final String TAG = "RegisterPresenter";

    public static void registerUser(User user) {
        FirebaseDB
                .getAuth()
                .createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseDB.getAuth().getCurrentUser(), "Firebase user not found");

                        // Temporary log for development
                        Log.d(TAG, "User registered: " + user.getUsername() + " with uid: " + firebaseUser.getUid());

                        user.setUid(firebaseUser.getUid());
                        UserPresenter.createUser(user);
                    } else {
                        Log.w("Auth", "Error registering", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error registering", e);
                });
    }
}
