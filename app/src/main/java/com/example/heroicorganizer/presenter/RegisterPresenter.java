package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.callback.RegisterCallback;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.UserPresenter;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterPresenter {

    private static final String TAG = "RegisterPresenter";

    private static final Pattern USERNAME_REGEX = Pattern.compile("^[a-z0-9-]{5,30}$");

    public static void registerUser(User user, RegisterCallback callback) {
        // Validate username format before registering
        if (!USERNAME_REGEX.matcher(user.getUsername()).matches()) {
            Log.w(TAG, "Invalid username format: " + user.getUsername());
            return;
        }

        // Check if username is unique - before email check
        FirebaseDB
                .getDb()
                .collection("users")
                .whereEqualTo("username", user.getUsername())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        callback.onFailure("Username already exists");
                        return;
                    }

                    FirebaseDB
                            .getAuth()
                            .createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                            .addOnCompleteListener(authTask -> {
                                if (authTask.isSuccessful()) {
                                    FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseDB.getAuth().getCurrentUser(), "Firebase user not found");

                                    user.setUid(firebaseUser.getUid());

                                    UserPresenter.createUser(user);

                                    callback.onSuccess(user.getUsername());
                                } else {
                                    callback.onFailure("Error registering: " + authTask.getException().getMessage());
                                }
                            })
                            .addOnFailureListener(e -> {
                                callback.onFailure("Error registering: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> callback.onFailure("Username check failed: " + e.getMessage()));
    }
}
