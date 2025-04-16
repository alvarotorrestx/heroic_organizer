package com.example.heroicorganizer.presenter;

import android.util.Log;
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

    public static void registerUser(User user) {
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
                        Log.w(TAG, "Username already exists: " + user.getUsername());
                        return;
                    }

                    FirebaseDB
                            .getAuth()
                            .createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                            .addOnCompleteListener(authTask -> {
                                if (authTask.isSuccessful()) {
                                    FirebaseUser firebaseUser = Objects.requireNonNull(FirebaseDB.getAuth().getCurrentUser(), "Firebase user not found");

                                    // Temporary log for development
                                    Log.d(TAG, "User registered: " + user.getUsername() + " with uid: " + firebaseUser.getUid());

                                    user.setUid(firebaseUser.getUid());
                                    UserPresenter.createUser(user);
                                } else {
                                    Log.w("Auth", "Error registering", authTask.getException());
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error registering", e);
                            });
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error checking username uniqueness", e));
    }
}
