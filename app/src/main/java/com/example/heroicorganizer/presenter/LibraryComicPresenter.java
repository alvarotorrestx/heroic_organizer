package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.callback.LibraryComicCallback;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LibraryComicPresenter {

    public static final String TAG = "LibraryComicPresenter";

    // Get all Comics in Library - Folder
    public static void getComicsInFolder(User user, String folderId, LibraryComicCallback callback) {
        if (folderId == null || folderId.isEmpty()) {
            Log.e(TAG, "Folder ID is missing or wrong. Cannot retrieve comics.");
            callback.onFailure("Folder ID is missing or wrong.");
            return;
        }

        DocumentReference folderRef = FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folderId);

        folderRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        folderRef
                                .collection("comics")
                                .get()
                                .addOnCompleteListener(comicTask -> {
                                    if (comicTask.isSuccessful()) {
                                        List<LibraryComic> comicList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : comicTask.getResult()) {
                                            LibraryComic comic = document.toObject(LibraryComic.class);
                                            comicList.add(comic);
                                        }

                                        // Send comic list to FE
                                        callback.onSuccessComics(comicList);
                                    } else {
                                        Log.e(TAG, "Error retrieving comics.", comicTask.getException());
                                        callback.onFailure("Error retrieving comics.");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error retrieving comics.", e);
                                    callback.onFailure("Error retrieving comics.");
                                });
                    } else {
                        Log.e(TAG, "Folder not found.", task.getException());
                        callback.onFailure("Folder not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get folder.", e);
                    callback.onFailure("Failed to get folder.");
                });
    }

    // Get specific comic in folder
    // TODO: Determine use case of this
    public static void searchComicInFolder(User user, LibraryFolder folder, String field, String value, LibraryComicCallback callback) {
        if (folder.getId() == null || folder.getId().isEmpty()) {
            Log.e(TAG, "Folder ID is missing or wrong. Cannot search comics.");
            callback.onFailure("Folder ID is missing or wrong.");
            return;
        }

        if (value == null || value.isEmpty() || field == null || field.isEmpty()) {
            Log.e(TAG, "Field name or value is missing.");
            callback.onFailure("Field name or value is missing.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folder.getId())
                .collection("comics")
                // Allows multiple type of search, e.g. "title": "Spider-Man" or "id": "31213ad123b"
                .whereEqualTo(field, value)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<LibraryComic> foundComics = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LibraryComic comic = document.toObject(LibraryComic.class);
                            foundComics.add(comic);
                        }

                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(foundComics);
                        Log.d(TAG, "Search result: " + json);

                        callback.onSuccessComics(foundComics);
                    } else {
                        Log.e(TAG, "Error searching comics: ", task.getException());
                        callback.onFailure("Error searching comics: " + task.getException().getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error searching comics: ", e);
                    callback.onFailure("Error searching comics: " + e.getMessage());
                });
    }

    // Add Comic to Library - Folder
    public static void addComicToFolder(User user, String folderId, LibraryComic comic, LibraryComicCallback callback) {
        if (folderId == null || folderId.isEmpty()) {
            Log.e(TAG, "Folder ID is missing or wrong. Cannot add comic.");
            callback.onFailure("Folder ID is missing or wrong.");
            return;
        }

        if (comic.getId() == null || comic.getId().isEmpty()) {
            comic.setId(UUID.randomUUID().toString());
        }

        DocumentReference folderRef = FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folderId);

        folderRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        folderRef
                                .collection("comics")
                                .document(comic.getId())
                                .set(comic, SetOptions.merge())
                                .addOnCompleteListener(aVoid -> {
                                    Log.d(TAG, "Added comic: " + comic.getTitle() + " (ID: " + comic.getId() + ") to Folder.");
                                    callback.onSuccess("Added comic: " + comic.getTitle() + " to Folder.");

                                    // Add 1 to total comics on each comic addition
                                    folderRef
                                            .update("totalComics", FieldValue.increment(1));
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error adding comic: " + comic.getId() + " to Folder.", e);
                                    callback.onFailure("Error adding comic: " + comic.getId() + " to Folder.");
                                });
                    } else {
                        Log.e(TAG, "Folder does not exist. Cannot add comic.", task.getException());
                        callback.onFailure("Folder not found. Please try again.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding comic: ", e);
                    callback.onFailure("Error adding comic: " + e.getMessage());
                });
    }

    // Update Comic in Library - Folder
    public static void updateComicInFolder(User user, String folderId, LibraryComic comic, LibraryComicCallback callback) {
        if (folderId == null || folderId.isEmpty()) {
            Log.e(TAG, "Folder ID is missing or wrong. Cannot update comic.");
            callback.onFailure("Folder ID is missing or wrong.");
            return;
        }

        if (comic.getId() == null || comic.getId().isEmpty()) {
            Log.e(TAG, "Cannot update comic: Missing comic ID or is wrong.");
            callback.onFailure("Comic ID is missing or wrong.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folderId)
                .collection("comics")
                .document(comic.getId())
                .set(comic, SetOptions.merge())
                .addOnCompleteListener(aVoid -> {
                    Log.d(TAG, "Updated comic: " + comic.getTitle() + " (ID: " + comic.getId() + ") in Folder.");
                    callback.onSuccess("Updated comic: " + comic.getTitle() + " in Folder.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating comic: " + comic.getId() + " in Folder.", e);
                    callback.onFailure("Error updating comic: " + comic.getId() + " in Folder.");
                });
    }

    // Remove Comic from Library - Folder
    public static void deleteComicFromFolder(User user, String folderId, String comicId, LibraryComicCallback callback) {
        if (folderId == null || folderId.isEmpty()) {
            Log.e(TAG, "Folder ID is missing or wrong. Cannot delete comic.");
            callback.onFailure("Folder ID is missing or wrong.");
            return;
        }

        if (comicId == null || comicId.isEmpty()) {
            Log.e(TAG, "Cannot delete comic: Missing comic ID or is wrong.");
            callback.onFailure("Comic ID is missing or wrong.");
            return;
        }

        DocumentReference comicRef = FirebaseDB
                .getDb()
                .collection("users")
                .document(user.getUid())
                .collection("folders")
                .document(folderId)
                .collection("comics")
                .document(comicId);

        comicRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        comicRef
                                .delete()
                                .addOnCompleteListener(aVoid -> {
                                    Log.d(TAG, "Comic deleted successfully with ID: " + comicId + " from Folder.");
                                    callback.onSuccess("Comic deleted successfully with ID: " + comicId + " from Folder.");

                                    // Remove 1 from total comics on each comic removal
                                    FirebaseDB
                                            .getDb()
                                            .collection("users")
                                            .document(user.getUid())
                                            .collection("folders")
                                            .document(folderId)
                                            .update("totalComics", FieldValue.increment(-1));
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error deleting comic with ID: " + comicId + " from Folder.", e);
                                    callback.onFailure("Error deleting comic with ID: " + comicId + " from Folder.");
                                });
                    } else {
                        Log.e(TAG, "Comic not found. Cannot delete comic.", task.getException());
                        callback.onFailure("Comic not found. Cannot delete comic.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting comic with ID: " + comicId + " from Folder.", e);
                    callback.onFailure("Error deleting comic with ID: " + comicId + " from Folder.");
                });

    }
}
