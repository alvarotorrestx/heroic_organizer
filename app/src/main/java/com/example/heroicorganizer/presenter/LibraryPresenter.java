package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LibraryPresenter {

    private static final String TAG = "LibraryPresenter";

    // Get all folders for user
    public static void getFolders(User user) {
        if (user.getUid() == null || user.getUid().isEmpty()) {
            Log.e(TAG, "Cannot get folders: User UID is null, empty, or wrong.");
            return;
        }

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

    // Create new folder for user
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

    // Update folder for user
    public static void updateFolder(User user, LibraryFolder folder) {
        if (folder.getId() == null || folder.getId().isEmpty()) {
            Log.e(TAG, "Cannot update folder: Missing folder ID or is wrong.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseDB.getAuth().getUid()))
                .collection("folders")
                .document(folder.getId())
                .set(folder.toMap(), SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Folder updated successfully with ID: " + folder.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating folder", e);
                });
    }

    // Delete Folder from user's library
    public static void deleteFolder(User user, LibraryFolder folder) {
        if (folder.getId() == null || folder.getId().isEmpty()) {
            Log.e(TAG, "Cannot delete folder: Missing folder ID or is wrong.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseDB.getAuth().getUid()))
                .collection("folders")
                .document(folder.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Folder deleted successfully with ID: " + folder.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting folder", e);
                });
    }
}
