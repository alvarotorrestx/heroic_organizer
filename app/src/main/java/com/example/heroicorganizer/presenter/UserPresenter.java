package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserPresenter {

    private static final String TAG = "UserPresenter";

    // Get All Users
    public static void getUsers() {
        FirebaseDB
                .getDb()
                .collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }

                        // Temporary while testing / developing
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(userList);
                        Log.d(TAG, "User List: " + json);
                    } else {
                        Log.e(TAG, "Error getting users", task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Firestore access failed", e));
    }

    // Create New User
    public static void createUser(User user) {
        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseDB.getAuth().getUid()))
                .set(user.toMap())
                .addOnSuccessListener(unused -> Log.d(TAG, "User with Username: " + user.getUsername() + " created"))
                .addOnFailureListener(e -> Log.e(TAG, "Error creating user", e));
    }

    // Update User
    public static void updateUser(User user) {
        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseDB.getAuth().getUid()))
                .update(user.toMap())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "User updated successfully.");
                    } else {
                        Log.e(TAG, "You are not authorized to update this user.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error modifying user", e));
    }

    // Delete User
    public static void deleteUser(User user) {
        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseDB.getAuth().getUid()))
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "User deleted successfully.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting user", e));
    }
}
