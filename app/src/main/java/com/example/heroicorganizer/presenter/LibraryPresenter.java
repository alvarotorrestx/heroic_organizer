package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
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

    public static void getFolders(User user) {
        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<LibraryFolder> folderList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                             LibraryFolder folder = document.toObject(LibraryFolder.class);
                            folderList.add(folder);
                        }

                        // Temporary while testing / developing
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(folderList);
                        Log.d(TAG, "Folder List: " + json);
                    } else {
                        Log.e(TAG, "Error getting folders", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting folders", e);
                });

    }
}
