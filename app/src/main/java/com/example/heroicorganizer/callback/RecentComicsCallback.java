package com.example.heroicorganizer.callback;

import com.example.heroicorganizer.model.LibraryComic;

import java.util.List;

public interface RecentComicsCallback {
    void onSuccess(List<LibraryComic> comics);
    void onFailure(String errorMessage);
}
