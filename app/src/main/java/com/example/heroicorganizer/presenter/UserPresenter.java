package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

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
}
