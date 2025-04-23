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

public class ComicPresenter {

    public static final String TAG = "ComicPresenter";

    // Get all Comics in Library - Folder
    public static void getComics(User user, LibraryFolder folder) {
        if (folder.getId() == null || folder.getId().isEmpty()) {
            Log.e(TAG, "Folder ID is missing or Cannot retrieve comics.");
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
                    } else {
                        Log.e(TAG, "Error getting comics: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting comics", e);
                });
    }

    // Add Comic to Library

    // Update Comic in Library

    // Remove Comic from Library
}
