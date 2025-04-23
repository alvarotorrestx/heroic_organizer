package com.example.heroicorganizer.callback;

import com.example.heroicorganizer.model.LibraryComic;

import java.util.List;

public interface LibraryComicCallback {
    void onSuccess(String message);
    void onSuccessComics(List<LibraryComic> comics);
    void onFailure(String errorMessage);
}
