package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;

import java.util.Objects;
import java.util.UUID;

public class LibraryPresenter {

    private static final String TAG = "LibraryPresenter";

    public static void createFolder(User user, LibraryFolder folder) {
        if (folder.getId() == null || folder.getId().isEmpty()) {
            folder.setId(UUID.randomUUID().toString());
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseDB.getAuth().getUid()))
                .collection("folders")
                .document(folder.getId())
                .set(folder.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Folder created successfully with ID: " + folder.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating folder", e);
                });
    }
}
