package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.callback.LoginCallback;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginPresenter {

    public static void loginUser(User user, LoginCallback callback) {
        FirebaseDB
                .getAuth()
                .signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser(), "Firebase user not found");

                        callback.onSuccess(firebaseUser.getEmail());
                    } else {
                        callback.onFailure("Invalid user credentials.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Login failed. Please try again.");
                });
    }
}
