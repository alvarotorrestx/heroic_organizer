package com.example.heroicorganizer.presenter;

import android.util.Log;
import com.example.heroicorganizer.callback.RecentComicsCallback;
import com.example.heroicorganizer.config.FirebaseDB;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.User;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecentComicsPresenter {
    public static void getRecentComics(User user, RecentComicsCallback callback) {
        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("recent_comics")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<LibraryComic> recentComics = new ArrayList<>();
                        for(QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            LibraryComic comic = document.toObject(LibraryComic.class);
                            recentComics.add(comic);
                        }

                        Log.d("RecentComics", "Successfully retrieved recent comics");
                        callback.onSuccess(recentComics);
                    } else {
                        Log.e("RecentComics", "Failed to retrieve recent comics", task.getException());
                        callback.onFailure("Failed to retrieve recent comics");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RecentComics", "Failed to retrieve recent comics", e);
                    callback.onFailure("Failed to retrieve recent comics");
                });
    }
    public static void addRecentComic(User user, LibraryComic comic) {
        FirebaseDB
                .getDb()
                .collection("users")
                .document(Objects.requireNonNull(user.getUid()))
                .collection("recent_comics")
                .add(comic)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RecentComics", "Added recent comic");
                })
                .addOnFailureListener(e -> {
                    Log.d("RecentComics", "Failed to add recent comic", e);
                });
    }
}
