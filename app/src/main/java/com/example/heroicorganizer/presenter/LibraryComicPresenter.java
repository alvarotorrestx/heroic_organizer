package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.callback.LibraryComicCallback;
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

public class LibraryComicPresenter {

    public static final String TAG = "LibraryComicPresenter";

    // Get all Comics in Library - Folder
    public static void getComicsInFolder(User user, LibraryFolder folder, LibraryComicCallback callback) {
        if (folder.getId() == null || folder.getId().length() == 0) {
            Log.e(TAG, "Folder ID is missing or wrong. Cannot retrieve comics.");
            callback.onFailure("Folder ID is missing or wrong.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folder.getId())
                .collection("comics")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<LibraryComic> comicList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            LibraryComic comic = document.toObject(LibraryComic.class);
                            comicList.add(comic);
                        }

                        // Temporary while testing / developing
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(comicList);
                        Log.d(TAG, "Comic List: " + json);

                        callback.onSuccessComics(comicList);
                    } else {
                        Log.e(TAG, "Error getting comics: ", task.getException());
                        callback.onFailure("Error getting comics: " + task.getException().getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting comics", e);
                    callback.onFailure("Error getting comics: " + e.getMessage());
                });
    }

    // Get specific comic in folder
    public static void searchComicInFolder(User user, LibraryFolder folder, String field, String value, LibraryComicCallback callback) {
        if (folder.getId() == null || folder.getId().length() == 0) {
            Log.e(TAG, "Folder ID is missing or wrong. Cannot search comics.");
            callback.onFailure("Folder ID is missing or wrong.");
            return;
        }

        if (value == null || value.length() == 0 || field == null || field.length() == 0) {
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
    public static void addComicToFolder(User user, LibraryFolder folder, LibraryComic comic, LibraryComicCallback callback) {
        if (comic.getId() == null || comic.getId().length() == 0) {
            comic.setId(UUID.randomUUID().toString());
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folder.getId())
                .collection("comics")
                .document(comic.getId())
                .set(comic)
                .addOnCompleteListener(aVoid -> {
                    Log.d(TAG, "Added comic: " + comic.getTitle() + " (ID: " + comic.getId() + ") to Folder: " + folder.getName());
                    callback.onSuccess("Added comic: " + comic.getTitle() + " to Folder: " + folder.getName());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding comic: " + comic.getId() + " to Folder: " + folder.getName(), e);
                    callback.onFailure("Error adding comic: " + comic.getId() + " to Folder: " + folder.getName());
                });
    }

    // Update Comic in Library - Folder
    public static void updateComicInFolder(User user, LibraryFolder folder, LibraryComic comic, LibraryComicCallback callback) {
        if (comic.getId() == null || comic.getId().length() == 0) {
            Log.e(TAG, "Cannot update comic: Missing comic ID or is wrong.");
            callback.onFailure("Comic ID is missing or wrong.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folder.getId())
                .collection("comics")
                .document(comic.getId())
                .set(comic, SetOptions.merge())
                .addOnCompleteListener(aVoid -> {
                    Log.d(TAG, "Updated comic: " + comic.getTitle() + " (ID: " + comic.getId() + ") in Folder: " + folder.getName());
                    callback.onSuccess("Updated comic: " + comic.getTitle() + " in Folder: " + folder.getName());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating comic: " + comic.getId() + " in Folder: " + folder.getName(), e);
                    callback.onFailure("Error updating comic: " + comic.getId() + " in Folder: " + folder.getName());
                });
    }

    // Remove Comic from Library - Folder
    public static void deleteComicFromFolder(User user, LibraryFolder folder, LibraryComic comic, LibraryComicCallback callback) {
        if (comic.getId() == null || comic.getId().length() == 0) {
            Log.e(TAG, "Cannot delete comic: Missing comic ID or is wrong.");
            callback.onFailure("Comic ID is missing or wrong.");
            return;
        }

        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("folders")
                .document(folder.getId())
                .collection("comics")
                .document(comic.getId())
                .delete()
                .addOnCompleteListener(aVoid -> {
                    Log.d(TAG, "Comic deleted successfully with ID: " + comic.getId() + " from Folder: " + folder.getName());
                    callback.onSuccess("Comic deleted successfully with ID: " + comic.getId() + " from Folder: " + folder.getName());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting comic: " + comic.getId() + " from Folder: " + folder.getName(), e);
                    callback.onFailure("Error deleting comic: " + comic.getId() + " from Folder: " + folder.getName());
                });
    }
}
