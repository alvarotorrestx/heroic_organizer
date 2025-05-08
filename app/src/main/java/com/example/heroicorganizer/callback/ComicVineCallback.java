package com.example.heroicorganizer.callback;

import com.example.heroicorganizer.model.ComicVineDisplay;

import java.util.List;

public interface ComicVineCallback {
    void onSuccess(List<ComicVineDisplay> results);
    void onFailure(String errorMessage);
}
