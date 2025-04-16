package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginPresenter {

    private static final String TAG = "LoginPresenter";

    public static void loginUser(User user) {
        FirebaseDB
                .getAuth()
                .signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(), "Firebase user not found");

                        // Temporary log for development
                        Log.d(TAG, "User logged in: " + user.getUsername() + " with uid: " + firebaseUser.getUid());
                    } else {
                        Log.w("Auth", "Error logging in", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error logging in", e);
                });
    }
}
