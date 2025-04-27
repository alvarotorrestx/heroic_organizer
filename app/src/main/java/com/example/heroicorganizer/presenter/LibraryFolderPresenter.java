package com.example.heroicorganizer.presenter;

import android.util.Log;
import android.util.Patterns;
import com.example.heroicorganizer.callback.LibraryFolderCallback;
import com.example.heroicorganizer.config.FirebaseDB;
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

public class LibraryFolderPresenter {

    private static final String TAG = "LibraryPresenter";

    private static final String DEFAULT_COLOR_TAG = "#FFBB86FC";

    // Get all folders for user
    public static void getFolders(User user, LibraryFolderCallback callback) {
        if (user.getUid() == null || user.getUid().isEmpty()) {
            Log.e(TAG, "Cannot get folders: User UID is null, empty, or wrong.");
            callback.onFailure("User UID is null, empty, or wrong.");
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

                        callback.onSuccessFolders(folderList);
                    } else {
                        Log.e(TAG, "Error getting folders", task.getException());
                        callback.onFailure("Error getting folders");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting folders", e);
                    callback.onFailure("Error getting folders");
                });
    }

    // Create new folder for user
    public static void createFolder(User user, LibraryFolder folder, LibraryFolderCallback callback) {
        if (folder.getId() == null || folder.getId().isEmpty()) {
            folder.setId(UUID.randomUUID().toString());
        }

        if (folder.getName() == null || folder.getName().isEmpty()) {
            callback.onFailure("Folder name is required.");
            return;
        }

        if (folder.getDescription() == null || folder.getDescription().isEmpty()) {
            callback.onFailure("Folder description is required.");
            return;
        }

        if (!folder.getCoverImage().isEmpty()) {
            if (!Patterns.WEB_URL.matcher(folder.getCoverImage()).matches()) {
                callback.onFailure("Cover image must be a valid URL.");
                return;
            }
        }

        if ((folder.getCoverImage() == null || folder.getCoverImage().isEmpty()) &&
                (folder.getColorTag() == null || folder.getColorTag().isEmpty())) {
            folder.setColorTag(DEFAULT_COLOR_TAG);
            callback.onFailure("Default color tag is being set.");
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folder.getId())
                .set(folder.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Folder created successfully with ID: " + folder.getId());
                    callback.onSuccess("Folder \"" + folder.getName() + "\" created successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating folder", e);
                    callback.onFailure("Failed to create folder: " + e.getMessage());
                });
    }

    // Update folder for user
    public static void updateFolder(User user, LibraryFolder folder, LibraryFolderCallback callback) {
        if (folder.getId() == null || folder.getId().isEmpty()) {
            Log.e(TAG, "Cannot update folder: Missing folder ID or is wrong.");
            callback.onFailure("Missing folder ID or is wrong.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folder.getId())
                .set(folder.toMap(), SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Folder updated successfully with ID: " + folder.getId());
                    callback.onSuccess("Folder \"" + folder.getName() + "\" updated successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating folder", e);
                    callback.onFailure("Failed to update folder: " + e.getMessage());
                });
    }

    // Delete Folder from user's library
    public static void deleteFolder(User user, LibraryFolder folder, LibraryFolderCallback callback) {
        if (folder.getId() == null || folder.getId().isEmpty()) {
            Log.e(TAG, "Cannot delete folder: Missing folder ID or is wrong.");
            callback.onFailure("Missing folder ID or is wrong.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folder.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Folder deleted successfully with ID: " + folder.getId());
                    callback.onSuccess("Folder \"" + folder.getName() + "\" deleted successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting folder", e);
                    callback.onFailure("Failed to delete folder: " + e.getMessage());
                });
    }
}
